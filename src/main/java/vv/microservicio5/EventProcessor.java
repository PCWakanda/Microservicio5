package vv.microservicio5;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

@Service
public class EventProcessor {

    private final AtomicInteger totalEvents = new AtomicInteger(0);
    private final AtomicInteger cameraEvents = new AtomicInteger(0);
    private final AtomicInteger droneEvents = new AtomicInteger(0);
    private final List<Event> cameraEventList = new CopyOnWriteArrayList<>();
    private final List<Event> droneEventList = new CopyOnWriteArrayList<>();

    @RabbitListener(queues = "cameraQueue")
    public void processCameraEvent(Event event) {
        if (!"none".equals(event.getSubtype())) {
            cameraEvents.incrementAndGet();
            totalEvents.incrementAndGet();
        }
        cameraEventList.add(event);
    }

    @RabbitListener(queues = "droneQueue")
    public void processDroneEvent(Event event) {
        if (!"none".equals(event.getSubtype())) {
            droneEvents.incrementAndGet();
            totalEvents.incrementAndGet();
        }
        droneEventList.add(event);
    }

    public void startLogging() {
        Flux.interval(Duration.ofSeconds(4))
                .subscribe(tick -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("--------tic ").append(tick).append("------\n");

                    sb.append("Camera Events:\n");
                    for (Event event : cameraEventList) {
                        sb.append("Camera Event: ").append(event.getSource())
                                .append(", Subtype: ").append(event.getSubtype())
                                .append(", Timestamp: ").append(event.getTimestamp()).append("\n");
                    }
                    cameraEventList.clear();

                    sb.append("Drone Events:\n");
                    for (Event event : droneEventList) {
                        sb.append("Drone Event: ").append(event.getSource())
                                .append(", Subtype: ").append(event.getSubtype())
                                .append(", Timestamp: ").append(event.getTimestamp()).append("\n");
                    }
                    droneEventList.clear();

                    sb.append("Total Camera Events: ").append(cameraEvents.get()).append("\n");
                    sb.append("Total Drone Events: ").append(droneEvents.get()).append("\n");
                    sb.append("Total Events: ").append(totalEvents.get()).append("\n");

                    System.out.println(sb.toString());
                });
    }
}