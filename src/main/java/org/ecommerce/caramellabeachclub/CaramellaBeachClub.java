package org.ecommerce.caramellabeachclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CaramellaBeachClub {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context = SpringApplication.run(CaramellaBeachClub.class, args);
            if (context.isActive()) {
                System.out.println("Application started successfully.");
            } else {
                System.err.println("Application failed to start – context is not active.");
            }
        } catch (WebServerException e) {
            System.err.println("Web server failed to start: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Application context initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
