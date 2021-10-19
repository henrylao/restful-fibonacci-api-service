package com.example.restfulfibonacciapiservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestfulPrimeApiApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulPrimeApiApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(RestfulPrimeApiApplication.class, args);
    }
    @Bean
    public CommandLineRunner run(FibonacciRepository fibonacciRepository) {
        return (args) -> {
            System.out.println(fibonacciRepository.findAll());
        };
    }

}
