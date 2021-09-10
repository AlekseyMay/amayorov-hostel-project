package com.amayorov.hostel.domain.dto;

import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.domain.transfer.ChangeGuest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.Date;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuestDTO {
	@Schema(
			description = "Guest name.",
			example = "Aleksey",
			required = true
	)
	@JsonProperty("first-name")
	@NotBlank(groups = {Default.class, ChangeGuest.class})
	String firstName;
	@Schema(
			description = "Guest lastname.",
			example = "Mayorov",
			required = true
	)
	@JsonProperty("last-name")
	@NotBlank(groups = {Default.class, ChangeGuest.class})
	String lastName;
	@Schema(
			description = "Guest patronymic name.",
			example = "Andreevich",
			required = true
	)
	@JsonProperty("patronymic")
	@NotBlank(groups = {Default.class, ChangeGuest.class})
	String patronymic;
	@Schema(
			description = "Guest passport number.",
			example = "1111556677, | if Changing existing guest this field is obligatory to be filled",
			required = true
	)
	@JsonProperty("passport")
	@NotBlank(groups = {Default.class, ChangeGuest.class})
	String passport;
	@Schema(
			description = "Guest photo as byte array.",
			example = "[60, 115, -109, 56]",
			required = true
	)
	@NotNull(groups = {Default.class, ChangeGuest.class})
	@JsonProperty("photo")
	byte[] photo;
	@Schema(
			implementation = String.class,
			description = "Guest date of birth.",
			example = "2021-01-01",
			required = true
	)
	@JsonProperty("date-of-birth")
	@NotNull(groups = {Default.class, ChangeGuest.class})
	Date dateOfBirth;
	@Schema(
			description = "Guest booked quarters and period of time. | in case of 'Change guest (PUT)' pass null or leave example with no change."
	)
	@JsonProperty("presence")
	@Valid
	@NotNull(groups = {Default.class})
	PresenceDTO presence;

	public Guest toEntity() {
		var guestEntity = new Guest();
		guestEntity.setFirstName(this.getFirstName().replaceAll("\\s",""));
		guestEntity.setLastName(this.getLastName().replaceAll("\\s",""));
		guestEntity.setPatronymic(this.getPatronymic().replaceAll("\\s",""));
		guestEntity.setPassport(this.getPassport().replaceAll("\\s",""));
		guestEntity.setPhoto(this.getPhoto());
		guestEntity.setDateOfBirth(this.getDateOfBirth());

		return guestEntity;
	}
}