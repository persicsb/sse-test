package hu.persicsb.ssetest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSseController<T> {

    private final Map<String, T> requestMap = new ConcurrentHashMap<>();

    @PostMapping(path = {"", "/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> startSse(@RequestBody T request) {
        var uuid = UUID.randomUUID().toString();
        requestMap.put(uuid, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uuid);
    }

    @GetMapping(path = "/{uuid}")
    private ResponseEntity<ResponseBodyEmitter> serveSse(@PathVariable("uuid") String uuid) {
        var request = requestMap.get(uuid);
        if(request == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        var emitter =  createEmitter(request, () -> requestMap.remove(uuid));
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }

    protected abstract SseEmitter createEmitter(T request, Runnable remover);
}
