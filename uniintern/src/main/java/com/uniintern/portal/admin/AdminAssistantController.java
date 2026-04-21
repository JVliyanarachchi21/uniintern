package com.uniintern.portal.admin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/admin/chat")
public class AdminAssistantController {

    private final AdminAssistantService assistantService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public AdminAssistantController(AdminAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam String q) {
        SseEmitter emitter = new SseEmitter(60000L); // 60s timeout
        String fullResponse = assistantService.getResponseForQuery(q);

        executor.execute(() -> {
            try {
                String[] words = fullResponse.split(" ");
                for (String word : words) {
                    // Simulate AI typing delay
                    Thread.sleep(40);
                    emitter.send(word + " ");
                }
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
