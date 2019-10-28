package hu.persicsb.ssetest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Flow;

@RestController
@RequestMapping("/hello")
public class HelloController extends AbstractReactiveSseController<SseRequest, String> {

    @Override
    protected Flow.Publisher<String> createPublisher(SseRequest request) {
        return JdkFlowAdapter.publisherToFlowPublisher(
                Flux.interval(Duration.of(2, ChronoUnit.SECONDS))
                    .take(5)
                    .map(i -> "Hello, " + request.getName())
        );
    }

}
