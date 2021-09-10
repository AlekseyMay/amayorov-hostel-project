package com.amayorov.hostel.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "QUARTERS")
@Data
@NoArgsConstructor // JPA
public class Quarters {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "QUARTER_NUMBER")
	private Integer quarterNumber;

	@Column(name = "PREMISES_NUMBER")
	private Integer premisesNumber;

	@Column(name = "CLEANING_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date cleaningDate;

	@OneToMany(mappedBy= "quarters", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JsonBackReference
	@EqualsAndHashCode.Exclude
	@Hidden
	private Set<Presence> presences = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "CATEGORY_ID" )
	private Category category;

	public Quarters(Integer quarterNumber, Integer premisesNumber, Date cleaningDate) {
		this.quarterNumber = quarterNumber;
		this.premisesNumber = premisesNumber;
		this.cleaningDate = cleaningDate;
	}
}
