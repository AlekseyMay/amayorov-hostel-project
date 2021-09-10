package com.amayorov.hostel.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Value
public class CleaningDateDTO {

	@Schema(
			description = "Number of a quarter.",
			example = "999",
			required = true
	)
	@NotNull
	@JsonProperty("quarter-number")
	Integer quarterNumber;

	@Schema(
			implementation = String.class,
			description = "New cleaning date.",
			example = "2021-01-01",
			required = true
	)
	@NotNull
	@JsonProperty("cleaning-date")
	Date cleaningDate;
}
