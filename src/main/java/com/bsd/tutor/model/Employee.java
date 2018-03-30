package com.bsd.tutor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EMPLOYEE")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name="age")
	private Long age;
	
	public Employee() {
	}

	public Employee(Long id, String name, Long age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}
	
	public Employee(String name, Long age) {
		this.name = name;
		this.age = age;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}
	
	@Override
	public String toString() {
		return "Employee: " + this.id + ", " + this.name + ", " + this.age; 
	}
	
}