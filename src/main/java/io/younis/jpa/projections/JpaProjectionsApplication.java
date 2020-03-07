package io.younis.jpa.projections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JpaProjectionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaProjectionsApplication.class, args);
    }

}
