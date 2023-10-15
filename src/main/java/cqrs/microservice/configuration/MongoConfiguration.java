package cqrs.microservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MongoConfiguration {
    @PostConstruct
    public void mongoInit(){
        log.info("MongoDB connected");
    }
}
