package app.dto;

import javax.validation.constraints.NotNull;

public class LikeRequest {
	@NotNull
	public Long hopperId;
	@NotNull
	public Long barId;
}
