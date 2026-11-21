package app.components;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Formatting for messages and SMS text. */
final class TimeFormat {
	private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("MMM d");

	private TimeFormat() {
	}

	static String hm(LocalTime t) {
		return t.format(HM);
	}

	static String shortDate(LocalDate d) {
		return d.format(SHORT_DATE);
	}

	static String plural(long n, String singular, String plural) {
		return n + " " + (n == 1 ? singular : plural);
	}
}
