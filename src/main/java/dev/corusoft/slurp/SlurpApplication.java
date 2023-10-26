package dev.corusoft.slurp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan("dev.corusoft.slurp.common")
@ComponentScan("dev.corusoft.slurp.config")
@ComponentScan("dev.corusoft.slurp.users")
@SpringBootApplication
public class SlurpApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlurpApplication.class, args);
	}

}
