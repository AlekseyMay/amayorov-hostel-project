package com.amayorov.hostel.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PRESENCE")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor // JPA
/* we need this extra entity, because as i understood,
 it is possible that one GUEST has 2 QUARTERS, so it is
 impossible to give GUEST info about 2 different check-in
 and check-out variables for two QUARTERS, so here we can link
 guests and quarters, even if the the GUEST have 2 QUARTERS
 and the time of Check-in and Check-out is different for these
 2 QUARTERS
 */
public class Presence {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CHECKIN_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkInDate;

	@Column(name = "CHECKOUT_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkOutDate;

	@ManyToOne
	@JoinColumn(name = "GUEST_ID")
	@JsonBackReference
	@EqualsAndHashCode.Exclude         // need this custom equals and hashcode because Fail-safe cleanup (collections) on some requests
    private Guest guest;               // because entities calling each other's hashcode recursively, See: https://stackoverflow.com/questions/53540056/what-causes-spring-boot-fail-safe-cleanup-collections-to-occur

	@ManyToOne
	@JoinColumn(name = "QUARTERS_ID")
	private Quarters quarters;

	public Presence(
			Date checkInDate,
			Date checkOutDate,
			Guest guest,
			Quarters quarters
	) {
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.guest = guest;
		this.quarters = quarters;
	}
}
