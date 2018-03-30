package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the STUDENT_AVAILABLE_TIMELOCATION database table.
 * 
 */
@Entity
@Table(name="STUDENT_AVAILABLE_TIMELOCATION")
@NamedQuery(name="StudentAvailableTimelocation.findAll", query="SELECT s FROM StudentAvailableTimelocation s")
public class StudentAvailableTimelocation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SAV_ID")
	private Long savId;

	@Column(name="SAV_DAY_ID")
	private Long savDayId;

	@Column(name="SAV_END_TIME")
	private Double savEndTime;

	@Column(name="SAV_LAT")
	private Double savLat;

	@Column(name="SAV_LOCATION")
	private String savLocation;

	@Column(name="SAV_LONG")
	private Double savLong;

	@Column(name="SAV_START_TIME")
	private Double savStartTime;

	//bi-directional many-to-one association to Student
	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="SAV_STU_ID")
	private Student student;

	public StudentAvailableTimelocation() {
	}

	public Long getSavId() {
		return this.savId;
	}

	public void setSavId(Long savId) {
		this.savId = savId;
	}

	public Long getSavDayId() {
		return this.savDayId;
	}

	public void setSavDayId(Long savDayId) {
		this.savDayId = savDayId;
	}

	public Double getSavEndTime() {
		return this.savEndTime;
	}

	public void setSavEndTime(Double savEndTime) {
		this.savEndTime = savEndTime;
	}

	public Double getSavLat() {
		return this.savLat;
	}

	public void setSavLat(Double savLat) {
		this.savLat = savLat;
	}

	public String getSavLocation() {
		return this.savLocation;
	}

	public void setSavLocation(String savLocation) {
		this.savLocation = savLocation;
	}

	public Double getSavLong() {
		return this.savLong;
	}

	public void setSavLong(Double savLong) {
		this.savLong = savLong;
	}

	public Double getSavStartTime() {
		return this.savStartTime;
	}

	public void setSavStartTime(Double savStartTime) {
		this.savStartTime = savStartTime;
	}

	public Student getStudent() {
		return this.student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

}