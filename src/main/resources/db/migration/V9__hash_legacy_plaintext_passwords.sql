-- Cifra con bcrypt las contrasenas legacy que quedaron en texto plano.
-- Reemplaza el fallback de login (UserService/ClientService.passwordMatches) que las aceptaba tal cual.
-- pgcrypto genera hashes $2a$ compatibles con BCryptPasswordEncoder de Spring.
-- Idempotente: solo toca filas que no son ya un hash bcrypt ($2a$/$2b$/$2y$).

CREATE EXTENSION IF NOT EXISTS pgcrypto;

UPDATE users
SET password = crypt(password, gen_salt('bf', 10))
WHERE password IS NOT NULL
  AND password <> ''
  AND password !~ '^\$2[aby]\$\d{2}\$';

UPDATE clients
SET password = crypt(password, gen_salt('bf', 10))
WHERE password IS NOT NULL
  AND password <> ''
  AND password !~ '^\$2[aby]\$\d{2}\$';
