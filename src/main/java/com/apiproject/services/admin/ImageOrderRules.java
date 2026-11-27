package com.apiproject.services.admin;

import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

/** Reglas comunes de imagenes (productos normales y de segunda mano). */
final class ImageOrderRules {

    private ImageOrderRules() {
    }

    /** 409 si el producto ya tiene el maximo de imagenes permitido. */
    static void checkCapacity(long currentCount, int maxPerProduct) {
        if (currentCount >= maxPerProduct) {
            throw new ResponseStatusException(CONFLICT,
                    "El producto ya tiene el maximo de " + maxPerProduct + " imagenes; borra una antes de subir otra");
        }
    }

    /** 400 si {@code requested} no es exactamente el conjunto {@code existing} (sin duplicados). */
    static void checkSameImages(List<Long> existing, List<Long> requested) {
        Set<Long> requestedSet = new HashSet<>(requested);
        if (requestedSet.size() != requested.size()) {
            throw new ResponseStatusException(BAD_REQUEST, "imageIds tiene ids repetidos");
        }
        if (!requestedSet.equals(new HashSet<>(existing))) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "imageIds debe contener exactamente las imagenes del producto " + existing);
        }
    }
}
