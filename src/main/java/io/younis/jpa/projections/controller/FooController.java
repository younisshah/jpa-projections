package io.younis.jpa.projections.controller;

import io.younis.jpa.projections.model.Data;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class FooController {

    @GetMapping("/sse")
    @CrossOrigin
    public SseEmitter sse() {
        SseEmitter emitter = new SseEmitter();
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(() -> {
            try {

                Data data = Data.builder().email("younis@email.com").build();
                emitter.send(data);

                TimeUnit.SECONDS.sleep(3); // simulate delay
                data.setName("Younis Shah");

                emitter.send(data); // send more data
                emitter.complete();

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        es.shutdown();
        return emitter;
    }

    @GetMapping("/response-body")
    @CrossOrigin
    public ResponseBodyEmitter responseBody() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(() -> {
            try {

                Data data = Data.builder().email("younis@email.com").build();
                emitter.send(data);

                TimeUnit.SECONDS.sleep(3); // simulate delay
                data.setName("Younis");

                emitter.send(data); // send more data
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        es.shutdown();
        return emitter;
    }


}
