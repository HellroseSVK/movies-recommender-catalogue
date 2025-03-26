package org.movierc.shared.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.movierc.shared")
public class SharedConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
