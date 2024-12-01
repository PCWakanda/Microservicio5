package vv.microservicio5;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

@RestController
public class EventController {

    private final Sinks.Many<Event> cameraSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Event> droneSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Random random = new Random();
    private final RabbitTemplate rabbitTemplate;
    private final List<Event> cameraEvents = new ArrayList<>();
    private final List<Event> droneEvents = new ArrayList<>();
    private final MeterRegistry meterRegistry;
    private final EventRepository eventRepository;

    @Autowired
    public EventController(RabbitTemplate rabbitTemplate, MeterRegistry meterRegistry, EventRepository eventRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.meterRegistry = meterRegistry;
        this.eventRepository = eventRepository;
        meterRegistry.gauge("events.camera.size", cameraEvents, List::size);
        meterRegistry.gauge("events.drone.size", droneEvents, List::size);
    }

    @PostConstruct
    public void init() {
        eventRepository.deleteAll();
    }

    @GetMapping("/cameraEvents")
    public Flux<Event> getCameraEvents() {
        return cameraSink.asFlux();
    }

    @GetMapping("/droneEvents")
    public Flux<Event> getDroneEvents() {
        return droneSink.asFlux();
    }

    public void startCameraFlow() {
        Flux.interval(Duration.ofSeconds(4))
                .subscribe(tick -> {
                    if (random.nextDouble() > 0.25) {
                        int eventCount = Math.min(random.nextInt(3) + 1, 5);
                        for (int i = 0; i < eventCount; i++) {
                            String cameraId = "Camara-" + (random.nextInt(10) + 1);
                            String[] subtypes = {"robo", "accidente", "aglomeracion"};
                            String subtype = subtypes[random.nextInt(subtypes.length)];
                            Event event = new Event(UUID.randomUUID(), "camera", cameraId, OffsetDateTime.now().toString(), subtype);
                            cameraEvents.add(event);
                            eventRepository.save(event);
                            cameraSink.tryEmitNext(event);
                            rabbitTemplate.convertAndSend("cameraQueue", event);
                        }
                    } else {
                        String cameraId = "Camara-" + (random.nextInt(10) + 1);
                        Event event = new Event(UUID.randomUUID(), "camera", cameraId, OffsetDateTime.now().toString(), "none");
                        cameraEvents.add(event);
                        eventRepository.save(event);
                        cameraSink.tryEmitNext(event);
                    }
                });
    }

    public void startDroneFlow() {
        Flux.interval(Duration.ofSeconds(4))
                .subscribe(tick -> {
                    if (random.nextDouble() > 0.25) {
                        int eventCount = Math.min(random.nextInt(3) + 1, 5);
                        for (int i = 0; i < eventCount; i++) {
                            String droneId = "Dron-" + (random.nextInt(5) + 1);
                            String[] subtypes = {"desastre natural", "accidente", "incendio"};
                            String subtype = subtypes[random.nextInt(subtypes.length)];
                            Event event = new Event(UUID.randomUUID(), "drone", droneId, OffsetDateTime.now().toString(), subtype);
                            droneEvents.add(event);
                            eventRepository.save(event);
                            droneSink.tryEmitNext(event);
                            rabbitTemplate.convertAndSend("droneQueue", event);
                        }
                    } else {
                        String droneId = "Dron-" + (random.nextInt(5) + 1);
                        Event event = new Event(UUID.randomUUID(), "drone", droneId, OffsetDateTime.now().toString(), "none");
                        droneEvents.add(event);
                        eventRepository.save(event);
                        droneSink.tryEmitNext(event);
                    }
                });
    }
}