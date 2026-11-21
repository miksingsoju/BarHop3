package app.db.entity;

/** Live activity level of a bar, derived from its active (unexpired) timed likes. */
public enum ActivityStatus {
	QUIET, MODERATE, LIVELY, PACKED;

	public static ActivityStatus fromLikeCount(long count) {
		if (count >= 30) return PACKED;
		if (count >= 15) return LIVELY;
		if (count >= 5) return MODERATE;
		return QUIET;
	}
}
