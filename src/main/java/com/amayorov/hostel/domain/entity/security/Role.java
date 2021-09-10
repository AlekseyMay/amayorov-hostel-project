package com.amayorov.hostel.domain.entity.security;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ROLE")
@Data
@NoArgsConstructor
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long roleId;

	@Column(name = "ROLE_NAME")
	private String roleName;

	@Hidden
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
			inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"))
	@JsonBackReference
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	Set<User> users = new HashSet<>();

}
