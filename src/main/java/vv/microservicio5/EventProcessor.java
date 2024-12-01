package vv.microservicio5;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Service
public class EventProcessor {

    private final List<Event> cameraEventList = new CopyOnWriteArrayList<>();
    private final List<Event> droneEventList = new CopyOnWriteArrayList<>();

    @RabbitListener(queues = "cameraQueue")
    public void processCameraEvent(Event event) {
        cameraEventList.add(event);
    }

    @RabbitListener(queues = "droneQueue")
    public void processDroneEvent(Event event) {
        droneEventList.add(event);
    }

    public void startLogging() {
        Flux.interval(Duration.ofSeconds(4))
                .subscribe(tick -> {
                    int cameraEventsCount = Math.min(cameraEventList.size(), 15);
                    int droneEventsCount = Math.min(droneEventList.size(), 15 - cameraEventsCount);
                    int totalEventsCount = cameraEventsCount + droneEventsCount;

                    StringBuilder sb = new StringBuilder();
                    sb.append("--------tic ").append(tick).append("------\n");

                    sb.append("Camera Events:\n");
                    for (int i = 0; i < cameraEventsCount; i++) {
                        Event event = cameraEventList.get(i);
                        sb.append("Camera Event: ").append(event.getSource())
                                .append(", Subtype: ").append(event.getSubtype())
                                .append(", Timestamp: ").append(event.getTimestamp()).append("\n");
                    }
                    cameraEventList.subList(0, cameraEventsCount).clear();

                    sb.append("Drone Events:\n");
                    for (int i = 0; i < droneEventsCount; i++) {
                        Event event = droneEventList.get(i);
                        sb.append("Drone Event: ").append(event.getSource())
                                .append(", Subtype: ").append(event.getSubtype())
                                .append(", Timestamp: ").append(event.getTimestamp()).append("\n");
                    }
                    droneEventList.subList(0, droneEventsCount).clear();

                    sb.append("Total Camera Events: ").append(cameraEventsCount).append("\n");
                    sb.append("Total Drone Events: ").append(droneEventsCount).append("\n");
                    sb.append("Total Events: ").append(totalEventsCount).append("\n");

                    System.out.println(sb.toString());
                });
    }
}