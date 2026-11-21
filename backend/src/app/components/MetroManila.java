package app.components;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;

/** The 17 LGUs of Metro Manila. Bars outside these are rejected. */
public final class MetroManila {

	private static final String[] CITIES = { "Caloocan", "Las Piñas", "Makati", "Malabon", "Mandaluyong", "Manila",
			"Marikina", "Muntinlupa", "Navotas", "Parañaque", "Pasay", "Pasig", "Pateros", "Quezon City", "San Juan",
			"Taguig", "Valenzuela" };

	/** normalized key -> official display name */
	private static final Map<String, String> BY_KEY = new LinkedHashMap<>();

	static {
		for (String c : CITIES) {
			BY_KEY.put(key(c), c);
		}
	}

	private MetroManila() {
	}

	/** Returns the official name ("makati city" -> "Makati", "quezon city" -> "Quezon City") or null. */
	public static String canonical(String city) {
		if (city == null) return null;
		return BY_KEY.get(key(city));
	}

	private static String key(String city) {
		String s = Normalizer.normalize(city, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		s = s.trim().toLowerCase().replaceAll("\\s+", " ");
		if (s.startsWith("city of ")) s = s.substring(8);
		if (s.endsWith(" city")) s = s.substring(0, s.length() - 5);
		return s;
	}
}
