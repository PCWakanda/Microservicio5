package vv.microservicio5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Microservicio5Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Microservicio5Application.class, args);
        MasterScheduler masterScheduler = context.getBean(MasterScheduler.class);
        masterScheduler.startSequentialFlows();
    }
}