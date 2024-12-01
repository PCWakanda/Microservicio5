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
                    int cameraEventsCount = cameraEventList.size();
                    int droneEventsCount = droneEventList.size();
                    int totalEventsCount = cameraEventsCount + droneEventsCount;

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

                    sb.append("Total Camera Events: ").append(cameraEventsCount).append("\n");
                    sb.append("Total Drone Events: ").append(droneEventsCount).append("\n");
                    sb.append("Total Events: ").append(totalEventsCount).append("\n");

                    System.out.println(sb.toString());
                });
    }
}