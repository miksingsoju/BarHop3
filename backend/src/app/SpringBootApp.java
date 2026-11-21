package app;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootApp {

	public static void main(String[] args) {
		// Likes, reminders and the nightly close-out all run on Manila time.
		TimeZone.setDefault(TimeZone.getTimeZone(Clock.ZONE));
		SpringApplication.run(SpringBootApp.class, args);
	}
}
