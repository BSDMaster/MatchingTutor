package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the PARENT database table.
 * 
 */
@Entity
@NamedQuery(name="Parent.findAll", query="SELECT p FROM Parent p")
public class Parent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PAR_ID")
	private Integer parId;

	@Column(name="PAR_EMAIL")
	private String parEmail;

	@Column(name="PAR_FIRSTNAME")
	private String parFirstname;

	@Column(name="PAR_LASTNAME")
	private String parLastname;

	@Column(name="PAR_PASS")
	private String parPass;

	@Column(name="PAR_PHONENUM")
	private String parPhonenum;

	//bi-directional many-to-one association to Student
	@OneToMany(mappedBy="parent")
	private List<Student> students;

	public Parent() {
	}

	public Integer getParId() {
		return this.parId;
	}

	public void setParId(Integer parId) {
		this.parId = parId;
	}

	public String getParEmail() {
		return this.parEmail;
	}

	public void setParEmail(String parEmail) {
		this.parEmail = parEmail;
	}

	public String getParFirstname() {
		return this.parFirstname;
	}

	public void setParFirstname(String parFirstname) {
		this.parFirstname = parFirstname;
	}

	public String getParLastname() {
		return this.parLastname;
	}

	public void setParLastname(String parLastname) {
		this.parLastname = parLastname;
	}

	public String getParPass() {
		return this.parPass;
	}

	public void setParPass(String parPass) {
		this.parPass = parPass;
	}

	public String getParPhonenum() {
		return this.parPhonenum;
	}

	public void setParPhonenum(String parPhonenum) {
		this.parPhonenum = parPhonenum;
	}

	public List<Student> getStudents() {
		return this.students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public Student addStudent(Student student) {
		getStudents().add(student);
		student.setParent(this);

		return student;
	}

	public Student removeStudent(Student student) {
		getStudents().remove(student);
		student.setParent(null);

		return student;
	}

}