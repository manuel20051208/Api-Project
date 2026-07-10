package com.example.apiproject.services.user.admin;

import com.example.apiproject.DTOs.Admin.NotificationEventDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long adminId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitter.onCompletion(() -> emitters.remove(adminId));
        emitter.onTimeout(() -> emitters.remove(adminId));
        emitter.onError(e -> emitters.remove(adminId));

        emitters.put(adminId, emitter);
        return emitter;
    }

    public void push(Long adminId, NotificationEventDTO event) {
        SseEmitter emitter = emitters.get(adminId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event()
                    .name(event.tipo())
                    .data(event));
        } catch (IOException e) {
            emitters.remove(adminId);
        }
    }
}