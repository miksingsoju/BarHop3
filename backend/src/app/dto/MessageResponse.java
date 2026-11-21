package app.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

/** {"message": "...", ...extra fields} for delete endpoints. */
public class MessageResponse {
	private final Map<String, Object> fields = new LinkedHashMap<>();

	public MessageResponse(String message) {
		fields.put("message", message);
	}

	public MessageResponse with(String key, Object value) {
		fields.put(key, value);
		return this;
	}

	@JsonAnyGetter
	public Map<String, Object> getFields() {
		return fields;
	}
}
