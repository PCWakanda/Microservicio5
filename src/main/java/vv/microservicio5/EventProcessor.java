package vv.microservicio5;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EventProcessor {

    private final AtomicInteger totalEvents = new AtomicInteger(0);
    private final AtomicInteger cameraEvents = new AtomicInteger(0);
    private final AtomicInteger droneEvents = new AtomicInteger(0);

    @RabbitListener(queues = "cameraQueue")
    public void processCameraEvent(Event event) {
        cameraEvents.incrementAndGet();
        totalEvents.incrementAndGet();
        System.out.println("Camera Event: " + event.getSource() + " - Total Camera Events: " + cameraEvents.get());
    }

    @RabbitListener(queues = "droneQueue")
    public void processDroneEvent(Event event) {
        droneEvents.incrementAndGet();
        totalEvents.incrementAndGet();
        System.out.println("Drone Event: " + event.getSource() + " - Total Drone Events: " + droneEvents.get());
    }

    public int getTotalEvents() {
        return totalEvents.get();
    }

    public int getCameraEvents() {
        return cameraEvents.get();
    }

    public int getDroneEvents() {
        return droneEvents.get();
    }
}