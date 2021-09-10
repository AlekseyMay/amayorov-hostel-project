package com.amayorov.hostel.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "GUEST")
@Data
@NoArgsConstructor // JPA
public class Guest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;


	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "PATRONYMIC")
	private String patronymic;

	@Column(name = "PASSPORT")
	private String passport;

	@Column(name = "PHOTO")
	private byte[] photo;

	@Column(name = "DATE_OF_BIRTH")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOfBirth;

	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "guest")
	@JsonManagedReference
	private Set<Presence> presences = new HashSet<>();


	public Guest(String firstName, String lastName, String patronymic,
	             String passport, byte[] photo, Date dateOfBirth) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.patronymic = patronymic;
		this.passport = passport;
		this.photo = photo;
		this.dateOfBirth = dateOfBirth;
	}
}
