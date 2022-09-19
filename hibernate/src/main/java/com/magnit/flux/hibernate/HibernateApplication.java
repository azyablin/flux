package com.magnit.flux.hibernate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.magnit.flux")
@EnableJpaRepositories("com.magnit.flux")
@EntityScan(basePackages="com.magnit.flux")
public class HibernateApplication {

    public static void main(String[] args) {

        SpringApplication.run(HibernateApplication.class, args);
    }

}
