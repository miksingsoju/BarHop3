package app.rest.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import app.rest.errorhandlers.ApiException;

/** Helpers for required / typed query parameters. */
final class Params {

	private Params() {
	}

	static <T> T require(T value, String name) {
		if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
			throw ApiException.badRequest("Query parameter '" + name + "' is required.");
		}
		return value;
	}

	static LocalDate date(String value, String name) {
		if (value == null || value.trim().isEmpty()) return null;
		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException e) {
			throw ApiException.badRequest("Invalid " + name + " '" + value + "'. Use yyyy-MM-dd.");
		}
	}

	static LocalTime time(String value, String name) {
		if (value == null || value.trim().isEmpty()) return null;
		try {
			return LocalTime.parse(value.trim());
		} catch (DateTimeParseException e) {
			throw ApiException.badRequest("Invalid " + name + " '" + value + "'. Use HH:mm.");
		}
	}
}
