package com.amayorov.hostel.domain.entity;

import com.amayorov.hostel.domain.enums.CategoryEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(name = "CATEGORY")
@Data
@NoArgsConstructor // JPA
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CATEGORY_NAME")
	@Enumerated(EnumType.STRING)
	private CategoryEnum categoryName;

	@Column(name = "SHORT_DESCRIPTION")
	private String shortDescription;


	public Category(CategoryEnum categoryName, String shortDescription) {
		this.categoryName = categoryName;
		this.shortDescription = shortDescription;
	}
}
