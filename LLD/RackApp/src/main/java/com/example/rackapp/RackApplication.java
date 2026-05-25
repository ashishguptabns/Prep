package com.example.rackapp;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RackApplication implements CommandLineRunner, ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(RackApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("CommandLineRunner: Application started with command-line arguments: " + String.join(", ", args));
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("ApplicationRunner: Application started with option names: " + args.getOptionNames());
    }
}
