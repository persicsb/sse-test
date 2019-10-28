package hu.persicsb.ssetest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.adapter.JdkFlowAdapter;

import java.io.IOException;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

public abstract class AbstractReactiveSseController<T, R> extends AbstractSseController<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractReactiveSseController.class);

    @Override
    protected final SseEmitter createEmitter(T request, Runnable remover) {
        var publisher = createPublisher(request);
        var flux = JdkFlowAdapter.flowPublisherToFlux(publisher);
        var emitter = new SseEmitter();
        flux.doOnComplete(() -> {
                remover.run();
                emitter.complete();
            })
            .subscribe(emitSseEvent(emitter));
        return emitter;
    }

    private Consumer<R> emitSseEvent(SseEmitter emitter) {
        return r -> {
            try {
                emitter.send(r);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        };
    }

    protected abstract Flow.Publisher<R> createPublisher(T request);
}
