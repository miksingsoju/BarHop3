package app.dto;

import java.math.BigDecimal;

/** JSON date/time formats used in every response. */
public final class Formats {
	public static final String DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String TIME = "HH:mm";

	private Formats() {
	}

	/** 5000.00 -> 5000, 12.50 -> 12.5 (JSON is written with WRITE_BIGDECIMAL_AS_PLAIN). */
	public static BigDecimal plain(BigDecimal value) {
		return value == null ? null : value.stripTrailingZeros();
	}
}
