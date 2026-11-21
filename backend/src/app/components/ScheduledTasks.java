package app.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Timers only; the work is done by the components. */
@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired private LikeComponent likeComponent;
	@Autowired private ReservationComponent reservationComponent;

	/** Every minute: remove likes older than 90 minutes and refresh bar statuses. */
	@Scheduled(fixedRate = 60000)
	public void expireTimedLikes() {
		int[] result = likeComponent.expireLikes();
		if (result[0] > 0) {
			log.info("Expired {} likes; updated {} bars", result[0], result[1]);
		}
	}

	/** Every 15 minutes: text reminders for confirmed bookings starting within 2 hours. */
	@Scheduled(cron = "0 */15 * * * *", zone = "Asia/Manila")
	public void sendReservationReminders() {
		int sent = reservationComponent.sendUpcomingReminders();
		if (sent > 0) {
			log.info("Sent {} reservation reminders", sent);
		}
	}

	/** Every day at 6:00 AM, after the bars close: complete or cancel past bookings. */
	@Scheduled(cron = "0 0 6 * * *", zone = "Asia/Manila")
	public void closeOutReservations() {
		reservationComponent.closeOutPastReservations();
	}
}
