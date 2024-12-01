package vv.microservicio5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Component
public class MasterScheduler {

    private final EventController eventController;
    private Disposable disposable;
    private int ticsTotales = 0;

    @Autowired
    public MasterScheduler(EventController eventController) {
        this.eventController = eventController;
    }

    public void startSequentialFlows() {
        disposable = Flux.interval(Duration.ofSeconds(4))
                .doOnNext(tic -> {
                    ticsTotales++;
                    if (ticsTotales % 2 == 1) {
                        eventController.startCameraFlow();
                    } else {
                        eventController.startDroneFlow();
                    }
                })
                .subscribeOn(Schedulers.parallel())
                .subscribe();
    }
}