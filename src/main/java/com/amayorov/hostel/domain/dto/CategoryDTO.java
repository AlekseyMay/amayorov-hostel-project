package com.amayorov.hostel.domain.dto;

import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.domain.enums.CategoryEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class CategoryDTO {

	@Schema(
			description = "Category name, taken from Enum, so it is predefined name List.",
			example = "Apartment",
			required = true
	)
	@NotNull
	@JsonProperty("category-name")
	CategoryEnum categoryName;

	@Schema(
			description = "Description of category.",
			example = "DescriptionDescriptionDescription",
			required = true
	)
	@NotBlank
	@JsonProperty("short-description")
	String shortDescription;

	public Category toEntity() {
		var categoryEntity = new Category();
		categoryEntity.setCategoryName(this.getCategoryName());
		categoryEntity.setShortDescription(this.getShortDescription());
		return categoryEntity;
	}
}
