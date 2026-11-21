package app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/** Single source of "now" (Asia/Manila), truncated to seconds. */
public final class Clock {

	public static final ZoneId ZONE = ZoneId.of("Asia/Manila");

	private Clock() {
	}

	public static LocalDateTime now() {
		return LocalDateTime.now(ZONE).truncatedTo(ChronoUnit.SECONDS);
	}

	public static LocalDate today() {
		return LocalDate.now(ZONE);
	}
}
