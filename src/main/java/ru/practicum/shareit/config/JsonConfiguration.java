package ru.practicum.shareit.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// To support lazy fetch for nested entities
public class JsonConfiguration {
    @Bean
    public Module hibernateModule() {
        return new Hibernate5JakartaModule();
    }
}
