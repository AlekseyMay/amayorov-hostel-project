package com.amayorov.hostel.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Value
public class ValidateQuartersDTO {

	@Schema(
			implementation = String.class,
			description = "Date from which checking availability.",
			example = "2021-01-01",
			required = true
	)
	@NotNull
	Date from;
	@Schema(
			implementation = String.class,
			description = "Date until which checking availability.",
			example = "2021-02-01",
			required = true
	)
	@NotNull
	Date till;
	@Schema(
			description = "Set of categories based on which quarters are going to be searched for. \n\n" +
					"Take into account to use only categories that are enum List",
			example = "[\"Apartment\",\"Business\",\"etc\"]",
			required = true
	)
	@NotEmpty
	@JsonProperty("names-of-categories")
	Set<String> categories;
	@Schema(
			description = "Amount of needed quarters to search for.",
			example = "1",
			required = true
	)
	@NotNull
	@JsonProperty("amount-of-quarters")
	int amountOfQuartersNeeded;
}
