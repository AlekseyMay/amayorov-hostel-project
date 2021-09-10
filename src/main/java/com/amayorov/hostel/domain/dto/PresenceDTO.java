package com.amayorov.hostel.domain.dto;

import com.amayorov.hostel.domain.transfer.CreatePresence;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.Date;


@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresenceDTO {
    @Schema(
            description = "Guest passport number. | in case of 'Adding new guest' or 'Changing existing guest' pass null or leave this example with no change",
            example = "1111556677",
            required = true
    )
    @JsonProperty("guest-passport")
    @NotBlank(groups = CreatePresence.class)
    String guestPassport;
    @Schema(
            description = "Quarters` number. | in case of 'Changing existing guest' pass null or leave this example with no change",
            example = "999",
            required = true
    )
    @JsonProperty("quarters-number")
    @NotNull(groups = {CreatePresence.class, Default.class})
    Integer quartersNumber;
    @Schema(
            implementation = String.class,
            description = "Guest check-in date. | in case of 'Changing existing guest' pass null or leave this example with no change",
            example = "2021-01-01",
            required = true
    )
    @JsonProperty("check-in-date")
    @NotNull(groups = {CreatePresence.class, Default.class})
    Date checkInDate;
    @Schema(
            implementation = String.class,
            description = "Guest check-out date. | in case of 'Changing existing guest' pass null or leave this example with no change",
            example = "2021-02-01",
            required = true
    )
    @JsonProperty("check-out-date")
    @NotNull(groups = {CreatePresence.class, Default.class})
    Date checkOutDate;
}
