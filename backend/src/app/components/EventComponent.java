package app.components;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import app.Clock;
import app.db.entity.Bar;
import app.db.entity.Event;
import app.db.repositories.BarRepository;
import app.db.repositories.EventRepository;
import app.dto.EventRequest;
import app.dto.EventResponse;
import app.dto.MessageResponse;
import app.rest.errorhandlers.ApiException;

/** Event schedules for bars. */
@Component
@Transactional
public class EventComponent {

	@Autowired private EventRepository eventRepository;
	@Autowired private BarRepository barRepository;

	@Transactional(readOnly = true)
	public List<EventResponse> getEvents(Long barId, boolean upcomingOnly) {
		List<Event> events;
		if (barId != null) {
			loadBar(barId);
			events = upcomingOnly
					? eventRepository.findByBarIdAndStartDateTimeAfterOrderByStartDateTimeAsc(barId, Clock.now())
					: eventRepository.findByBarIdOrderByStartDateTimeAsc(barId);
		} else {
			events = upcomingOnly
					? eventRepository.findByStartDateTimeAfterOrderByStartDateTimeAsc(Clock.now())
					: eventRepository.findAllByOrderByStartDateTimeAsc();
		}
		List<EventResponse> out = new ArrayList<>();
		for (Event e : events) {
			out.add(EventResponse.from(e));
		}
		return out;
	}

	@Transactional(readOnly = true)
	public EventResponse getEvent(Long id) {
		return EventResponse.from(load(id));
	}

	public EventResponse createEvent(EventRequest req) {
		Bar bar = loadBar(req.barId);
		BarComponent.checkOwner(bar, req.ownerId);
		if (!req.endDateTime.isAfter(req.startDateTime)) {
			throw ApiException.badRequest("endDateTime must be after startDateTime.");
		}
		Event e = new Event();
		e.setBar(bar);
		e.setTitle(req.title.trim());
		e.setDescription(req.description == null || req.description.trim().isEmpty() ? null : req.description.trim());
		e.setStartDateTime(req.startDateTime);
		e.setEndDateTime(req.endDateTime);
		e.setCoverCharge(req.coverCharge);
		return EventResponse.from(eventRepository.save(e));
	}

	public MessageResponse deleteEvent(Long id, Long ownerId) {
		Event e = load(id);
		BarComponent.checkOwner(e.getBar(), ownerId);
		eventRepository.delete(e);
		return new MessageResponse("Event " + id + " deleted.");
	}

	private Event load(Long id) {
		return eventRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Event with id " + id + " does not exist."));
	}

	private Bar loadBar(Long id) {
		return barRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Bar with id " + id + " does not exist."));
	}
}
