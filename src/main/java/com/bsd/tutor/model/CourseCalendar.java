package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the COURSE_CALENDAR database table.
 * 
 */
@Entity
@Table(name="COURSE_CALENDAR")
@NamedQuery(name="CourseCalendar.findAll", query="SELECT c FROM CourseCalendar c")
public class CourseCalendar implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CAR_ID")
	private Long carId;

	@Temporal(TemporalType.DATE)
	@Column(name="CAR_DATE")
	private Date carDate;

	@Column(name="CAR_ENDTIME")
	private Double carEndtime;

	@Column(name="CAR_LOC_LAT")
	private Double carLocLat;

	@Column(name="CAR_LOC_LONG")
	private Double carLocLong;

	@Column(name="CAR_LOC_NAME")
	private String carLocName;

	@Column(name="CAR_STARTTIME")
	private Double carStarttime;

	//bi-directional many-to-one association to Class
	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="CAR_COS_ID")
	private Class clazz;

	public CourseCalendar() {
	}

	public Long getCarId() {
		return this.carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	public Date getCarDate() {
		return this.carDate;
	}

	public void setCarDate(Date carDate) {
		this.carDate = carDate;
	}

	public Double getCarEndtime() {
		return this.carEndtime;
	}

	public void setCarEndtime(Double carEndtime) {
		this.carEndtime = carEndtime;
	}

	public Double getCarLocLat() {
		return this.carLocLat;
	}

	public void setCarLocLat(Double carLocLat) {
		this.carLocLat = carLocLat;
	}

	public Double getCarLocLong() {
		return this.carLocLong;
	}

	public void setCarLocLong(Double carLocLong) {
		this.carLocLong = carLocLong;
	}

	public String getCarLocName() {
		return this.carLocName;
	}

	public void setCarLocName(String carLocName) {
		this.carLocName = carLocName;
	}

	public Double getCarStarttime() {
		return this.carStarttime;
	}

	public void setCarStarttime(Double carStarttime) {
		this.carStarttime = carStarttime;
	}

	/*public Class getClazz() {
		return this.clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}*/

}