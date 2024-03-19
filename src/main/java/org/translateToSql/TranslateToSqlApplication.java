package org.translateToSql;
//
//import javax.ws.rs.ApplicationPath;
//import org.glassfish.jersey.server.ResourceConfig;
//
//
//@ApplicationPath("/api")
//public class Application extends ResourceConfig {
//    public Application() {
//        packages("org.translateToSql.twoVL"); // Specify the package where your resource classes are located
//    }
//}

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TranslateToSqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranslateToSqlApplication.class, args);
    }
}

