package com.amayorov.hostel.domain.dto;

import com.amayorov.hostel.domain.entity.Quarters;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Value
public class QuartersDTO {
	@Schema(
			description = "Number of a quarter.",
			example = "999",
			required = true
	)
	@NotNull
	@JsonProperty("quarters-number")
	Integer quarterNumber;

	@Schema(
			description = "Number of premises.",
			example = "999",
			required = true
	)
	@NotNull
	@JsonProperty("premises-number")
	Integer premisesNumber;
	@Schema(
			implementation = String.class,
			description = "Date of most recent cleaning.",
			example = "2021-01-01",
			required = true
	)
	@NotNull
	@JsonProperty("cleaning-date")
	Date cleaningDate;
	@Schema(
			description = "Name of quarters` category.",
			example = "Apartment",
			allowableValues = {"Apartment", "Business", "Deluxe", "Duplex", "Superior", "Standard"},
			required = true
	)
	@NotBlank
	@JsonProperty("category-name")
	String categoryName;

	public Quarters toEntity() {
		var quartersEntity = new Quarters();
		quartersEntity.setQuarterNumber(this.getQuarterNumber());
		quartersEntity.setPremisesNumber(this.getPremisesNumber());
		quartersEntity.setCleaningDate(this.getCleaningDate());
		return quartersEntity;
	}
}


