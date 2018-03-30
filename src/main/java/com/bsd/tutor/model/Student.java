package com.bsd.tutor.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the STUDENT database table.
 * 
 */
@Entity
@NamedQuery(name="Student.findAll", query="SELECT s FROM Student s")
public class Student implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="STU_ID")
	private Long stuId;

	@Column(name="STU_EMAIL")
	private String stuEmail;

	@Column(name="STU_FIRSTNAME")
	private String stuFirstname;

	@Column(name="STU_LASTNAME")
	private String stuLastname;

	@Column(name="STU_LEVEL")
	private Long stuLevel;

	@Column(name="STU_NICKNAME")
	private String stuNickname;

	@Column(name="STU_PASS")
	private String stuPass;

	@Column(name="STU_PHONENUM")
	private String stuPhonenum;

	@Column(name="STU_SCHOOL")
	private String stuSchool;

	@Column(name="STU_SEX")
	private String stuSex;

	//bi-directional many-to-one association to Class
	@OneToMany(mappedBy="student")
	private List<Class> clazzs;

	//bi-directional many-to-one association to Parent
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="STU_PAR_ID")
	private Parent parent;

	//bi-directional many-to-one association to StudentAvailableTimelocation
	@OneToMany(mappedBy="student",fetch=FetchType.EAGER)//, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<StudentAvailableTimelocation> studentAvailableTimelocations;

	public Student() {
	}

	public Long getStuId() {
		return this.stuId;
	}

	public void setStuId(Long stuId) {
		this.stuId = stuId;
	}

	public String getStuEmail() {
		return this.stuEmail;
	}

	public void setStuEmail(String stuEmail) {
		this.stuEmail = stuEmail;
	}

	public String getStuFirstname() {
		return this.stuFirstname;
	}

	public void setStuFirstname(String stuFirstname) {
		this.stuFirstname = stuFirstname;
	}

	public String getStuLastname() {
		return this.stuLastname;
	}

	public void setStuLastname(String stuLastname) {
		this.stuLastname = stuLastname;
	}

	public Long getStuLevel() {
		return this.stuLevel;
	}

	public void setStuLevel(Long stuLevel) {
		this.stuLevel = stuLevel;
	}

	public String getStuNickname() {
		return this.stuNickname;
	}

	public void setStuNickname(String stuNickname) {
		this.stuNickname = stuNickname;
	}

	public String getStuPass() {
		return this.stuPass;
	}

	public void setStuPass(String stuPass) {
		this.stuPass = stuPass;
	}

	public String getStuPhonenum() {
		return this.stuPhonenum;
	}

	public void setStuPhonenum(String stuPhonenum) {
		this.stuPhonenum = stuPhonenum;
	}

	public String getStuSchool() {
		return this.stuSchool;
	}

	public void setStuSchool(String stuSchool) {
		this.stuSchool = stuSchool;
	}

	public String getStuSex() {
		return this.stuSex;
	}

	public void setStuSex(String stuSex) {
		this.stuSex = stuSex;
	}

	public List<Class> getClazzs() {
		return this.clazzs;
	}

	public void setClazzs(List<Class> clazzs) {
		this.clazzs = clazzs;
	}

	public Class addClazz(Class clazz) {
		getClazzs().add(clazz);
		clazz.setStudent(this);

		return clazz;
	}

	public Class removeClazz(Class clazz) {
		getClazzs().remove(clazz);
		clazz.setStudent(null);

		return clazz;
	}

	public Parent getParent() {
		return this.parent;
	}

	public void setParent(Parent parent) {
		this.parent = parent;
	}

	public List<StudentAvailableTimelocation> getStudentAvailableTimelocations() {
		return this.studentAvailableTimelocations;
	}

	public void setStudentAvailableTimelocations(List<StudentAvailableTimelocation> studentAvailableTimelocations) {
		this.studentAvailableTimelocations = studentAvailableTimelocations;
	}

	public StudentAvailableTimelocation addStudentAvailableTimelocation(StudentAvailableTimelocation studentAvailableTimelocation) {
		getStudentAvailableTimelocations().add(studentAvailableTimelocation);
		studentAvailableTimelocation.setStudent(this);

		return studentAvailableTimelocation;
	}

	public StudentAvailableTimelocation removeStudentAvailableTimelocation(StudentAvailableTimelocation studentAvailableTimelocation) {
		getStudentAvailableTimelocations().remove(studentAvailableTimelocation);
		studentAvailableTimelocation.setStudent(null);

		return studentAvailableTimelocation;
	}

}