package app.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.db.entity.Reservation;
import okhttp3.ResponseBody;
import retrofit2.Response;

/** Writes the SMS texts and sends them through Twilio (or FakeTwilio) with Retrofit. Never throws. */
@Component
public class NotificationComponent {

	private static final Logger log = LoggerFactory.getLogger(NotificationComponent.class);

	@Autowired private TwilioApi twilioApi;
	@Autowired private ObjectMapper objectMapper;

	@Value("${twilio.enabled:false}")
	private boolean enabled;

	@Value("${twilio.messages-url:}")
	private String messagesUrl;

	@Value("${twilio.account-sid:}")
	private String accountSid;

	@Value("${twilio.from-number:}")
	private String fromNumber;

	@Value("${twilio.messaging-service-sid:}")
	private String messagingServiceSid;

	public void reservationPlaced(Reservation r) {
		sendSms(r.getContactNumber(), "BarHop: Your table " + slot(r) + " is PENDING. Ref #" + r.getId() + ".");
		sendSms(r.getBar().getOwner().getPhoneNumber(), "BarHop: New booking #" + r.getId() + " at "
				+ r.getBar().getName() + ": " + r.getTable().getLabel() + ", " + TimeFormat.shortDate(r.getReservationDate())
				+ ", " + times(r) + ", " + r.getGuestCount() + " guests (" + r.getContactName() + ", "
				+ r.getContactNumber() + ").");
	}

	public void reservationConfirmed(Reservation r) {
		sendSms(r.getContactNumber(), "BarHop: Your table " + slot(r) + " is CONFIRMED. See you there! Ref #" + r.getId() + ".");
	}

	public void reservationCancelledByHopper(Reservation r) {
		sendSms(r.getBar().getOwner().getPhoneNumber(), "BarHop: Booking #" + r.getId() + " at " + r.getBar().getName()
				+ " (" + r.getTable().getLabel() + ", " + TimeFormat.shortDate(r.getReservationDate()) + ", " + times(r)
				+ ") was cancelled by the guest. The table is free again.");
	}

	public void reservationCancelledByBar(Reservation r) {
		sendSms(r.getContactNumber(), "BarHop: Sorry, your table " + slot(r) + " was CANCELLED by the bar. Ref #"
				+ r.getId() + ".");
	}

	public void reservationReminder(Reservation r) {
		sendSms(r.getContactNumber(), "BarHop reminder: your table " + slot(r) + " starts soon. Ref #" + r.getId() + ".");
	}

	/** Sends one SMS. Logs the outcome; never throws to the caller. */
	public void sendSms(String to, String body) {
		if (!enabled) {
			log.info("[SMS disabled] to {}: {}", to, body);
			return;
		}
		try {
			String url = messagesUrl.replace("{accountSid}", accountSid);
			Response<ResponseBody> response = twilioApi
					.sendMessage(url, to, emptyToNull(fromNumber), emptyToNull(messagingServiceSid), body)
					.execute();
			if (response.isSuccessful()) {
				String sid = null;
				ResponseBody rb = response.body();
				if (rb != null) {
					JsonNode json = objectMapper.readTree(rb.string());
					sid = json.has("sid") ? json.get("sid").asText()
							: json.has("messaging_service_sid") ? json.get("messaging_service_sid").asText() : null;
				}
				log.info("SMS sent to {} (sid {}): {}", to, sid, body);
			} else {
				String error = response.errorBody() != null ? response.errorBody().string() : "";
				log.warn("SMS to {} failed with HTTP {}: {}", to, response.code(), abbreviate(error));
			}
		} catch (Exception e) {
			log.warn("SMS to {} could not be sent ({}). Message was: {}", to, e.toString(), body);
		}
	}

	private static String slot(Reservation r) {
		return r.getTable().getLabel() + " at " + r.getBar().getName() + " on " + TimeFormat.shortDate(r.getReservationDate())
				+ ", " + times(r) + " for " + r.getGuestCount();
	}

	private static String times(Reservation r) {
		return TimeFormat.hm(r.getStartTime()) + "–" + TimeFormat.hm(r.getEndTime());
	}

	private static String emptyToNull(String s) {
		return s == null || s.trim().isEmpty() ? null : s.trim();
	}

	private static String abbreviate(String s) {
		return s.length() > 300 ? s.substring(0, 300) + "..." : s;
	}
}
