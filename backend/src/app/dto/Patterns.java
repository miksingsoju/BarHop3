package app.dto;

/** Shared validation patterns. */
public final class Patterns {
	/** PH mobile number in E.164 format, as Twilio expects. */
	public static final String PH_MOBILE = "^\\+639\\d{9}$";
	public static final String PH_MOBILE_MESSAGE = "must be a PH mobile number in the format +639XXXXXXXXX";

	private Patterns() {
	}
}
