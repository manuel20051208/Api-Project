package com.example.apiproject.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT que se ejecuta UNA SOLA VEZ por cada petición HTTP.
 *
 * Su responsabilidad es interceptar el token Bearer del header Authorization,
 * validarlo y, si es válido, establecer el usuario autenticado en el SecurityContext
 * para que el resto de la cadena de filtros (y Spring Security) lo reconozca.
 *
 * Al anotarlo con @Component, Spring lo detecta automáticamente y lo registra
 * en el ApplicationContext. Luego debes añadirlo explícitamente a la
 * SecurityFilterChain con:
 *   http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
 */
@Component
@RequiredArgsConstructor // Lombok genera el constructor con los campos 'final' — equivale a @Autowired en constructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Servicio que encapsula toda la lógica de JWT: parsing, validación de firma,
     * expiración y extracción del usuario. Al ser 'final', Lombok lo inyecta
     * por constructor (patrón recomendado sobre @Autowired en campo).
     */

    private final JwtService jwtService;
    /**
     * Método principal del filtro. Spring lo llama en cada petición HTTP.
     *
     * OncePerRequestFilter garantiza que este método se ejecuta exactamente
     * una vez por petición, incluso en casos de forward o include internos
     * de Servlet, evitando doble validación accidental.
     *
     * @param request     la petición HTTP entrante
     * @param response    la respuesta HTTP saliente
     * @param filterChain la cadena de filtros restante — SIEMPRE hay que invocarla
     *                    al final para que la petición siga su curso
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Leer el header "Authorization" de la petición.
        //    HttpHeaders.AUTHORIZATION es la constante de Spring para "Authorization".
        //    Un token Bearer tiene este formato:
        //      Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 2. Cortocircuito: si no hay header o no empieza con "Bearer ",
        //    esta petición no usa JWT (puede ser un endpoint público, una petición
        //    de login, un health-check, etc.).
        //    Se invoca filterChain.doFilter() para pasar la petición al siguiente
        //    filtro SIN intentar autenticar. Luego se hace return para no continuar
        //    ejecutando este método.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token JWT eliminando el prefijo "Bearer " (7 caracteres).
        //    Después de substring(7) queda solo el JWT:
        //      eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIn0.abc123
        String token = authHeader.substring(7);

        // 4. Verificar que el SecurityContext aún NO tiene una autenticación establecida.
        //    Esto evita sobreescribir una autenticación que ya fue procesada antes
        //    en esta misma petición (por ejemplo, por otro filtro upstream).
        //    Es una guardia de idempotencia: si ya está autenticado, no hacemos nada.
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Delegar en JwtService el trabajo pesado:
            //    - Parsear el JWT (header.payload.signature)
            //    - Verificar la firma con la clave secreta
            //    - Comprobar que no ha expirado
            //    - Extraer el usuario y sus authorities del payload
            //
            //    Devuelve Optional<AuthenticatedUser>: presente si el token es válido,
            //    vacío si es inválido o expirado. Con ifPresent() evitamos un if/else
            //    y solo ejecutamos el bloque si el token fue válido.
            jwtService.parseAuthenticatedUser(token).ifPresent(authenticatedUser -> {

                // 6. Construir el objeto Authentication que Spring Security reconoce.
                //    UsernamePasswordAuthenticationToken con 3 argumentos indica
                //    autenticación EXITOSA (el constructor con 2 argumentos es para
                //    credenciales sin verificar aún):
                //      - principal:   el usuario autenticado (AuthenticatedUser)
                //      - credentials: null — ya no necesitamos la contraseña,
                //                     el JWT es la prueba de identidad
                //      - authorities: los roles/permisos extraídos del token
                //                     (ej: [ROLE_USER, ROLE_ADMIN])
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        authenticatedUser.getAuthorities()
                );

                // 7. Adjuntar detalles adicionales de la petición al objeto Authentication.
                //    WebAuthenticationDetails incluye: IP del cliente y sessionId.
                //    Esto enriquece los logs de auditoría y es útil para detectar
                //    tokens usados desde IPs distintas a la de emisión.
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 8. Registrar la autenticación en el SecurityContextHolder.
                //    A partir de este momento, cualquier código en esta petición que
                //    llame a SecurityContextHolder.getContext().getAuthentication()
                //    obtendrá este objeto — incluyendo @PreAuthorize, hasRole(), etc.
                //
                //    El SecurityContext vive en un ThreadLocal, por eso es seguro
                //    en entornos multihilo: cada hilo (petición) tiene su propio contexto.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        // 9. Continuar la cadena de filtros pase lo que pase.
        //    Si el token era válido: la petición sigue con el usuario autenticado.
        //    Si el token era inválido o inexistente: la petición sigue como anónima
        //    y será el AuthorizationFilter más adelante quien decida si permite
        //    o rechaza el acceso al recurso (lanzando 401/403).
        //
        //    IMPORTANTE: nunca omitas esta línea — si no se llama a doFilter(),
        //    la petición queda colgada y el cliente nunca recibe respuesta.
        filterChain.doFilter(request, response);
    }
}