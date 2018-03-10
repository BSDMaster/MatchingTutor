package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TUTOR_AVAILABLE_TIMELOACATION database table.
 * 
 */
@Entity
@Table(name="TUTOR_AVAILABLE_TIMELOACATION")
@NamedQuery(name="TutorAvailableTimeloacation.findAll", query="SELECT t FROM TutorAvailableTimeloacation t")
public class TutorAvailableTimeloacation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TAV_ID")
	private Integer tavId;

	@Column(name="TAV_DAY_ID")
	private Integer tavDayId;

	@Column(name="TAV_END_TIME")
	private Double tavEndTime;

	@Column(name="TAV_START_LAT")
	private Double tavStartLat;

	@Column(name="TAV_START_LOCATION")
	private String tavStartLocation;

	@Column(name="TAV_START_LONG")
	private Double tavStartLong;

	@Column(name="TAV_END_LAT")
	private Double tavEndLat;

	@Column(name="TAV_END_LOCATION")
	private String tavEndLocation;

	@Column(name="TAV_END_LONG")
	private Double tavEndLong;

	@Column(name="TAV_START_TIME")
	private Double tavStartTime;

	@Column(name="TAV_TRA_BUS")
	private String tavTraBus;

	@Column(name="TAV_TRA_DRIVE")
	private String tavTraDrive;

	@Column(name="TAV_TRA_TAXI")
	private String tavTraTaxi;

	@Column(name="TAV_TRA_TRAIN")
	private String tavTraTrain;

	//bi-directional many-to-one association to Tutor
	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="TAV_TUR_ID")
	private Tutor tutor;

	public TutorAvailableTimeloacation() {
	}

	public Integer getTavId() {
		return this.tavId;
	}

	public void setTavId(Integer tavId) {
		this.tavId = tavId;
	}

	public Integer getTavDayId() {
		return this.tavDayId;
	}

	public void setTavDayId(Integer tavDayId) {
		this.tavDayId = tavDayId;
	}

	public Double getTavEndTime() {
		return this.tavEndTime;
	}

	public void setTavEndTime(Double tavEndTime) {
		this.tavEndTime = tavEndTime;
	}

	public Double getTavStartLat() {
		return this.tavStartLat;
	}

	public void setTavStartLat(Double tavStartLat) {
		this.tavStartLat = tavStartLat;
	}

	public String getTavStartLocation() {
		return this.tavStartLocation;
	}

	public void setTavStartLocation(String tavStartLocation) {
		this.tavStartLocation = tavStartLocation;
	}

	public Double getTavStartLong() {
		return this.tavStartLong;
	}

	public void setTavStartLong(Double tavStartLong) {
		this.tavStartLong = tavStartLong;
	}

	public Double getTavStartTime() {
		return this.tavStartTime;
	}

	public void setTavStartTime(Double tavStartTime) {
		this.tavStartTime = tavStartTime;
	}

	public String getTavTraBus() {
		return this.tavTraBus;
	}

	public void setTavTraBus(String tavTraBus) {
		this.tavTraBus = tavTraBus;
	}

	public String getTavTraDrive() {
		return this.tavTraDrive;
	}

	public void setTavTraDrive(String tavTraDrive) {
		this.tavTraDrive = tavTraDrive;
	}

	public String getTavTraTaxi() {
		return this.tavTraTaxi;
	}

	public void setTavTraTaxi(String tavTraTaxi) {
		this.tavTraTaxi = tavTraTaxi;
	}

	public String getTavTraTrain() {
		return this.tavTraTrain;
	}

	public void setTavTraTrain(String tavTraTrain) {
		this.tavTraTrain = tavTraTrain;
	}

	public Tutor getTutor() {
		return this.tutor;
	}

	public void setTutor(Tutor tutor) {
		this.tutor = tutor;
	}

	public Double getTavEndLat() {
		return tavEndLat;
	}

	public void setTavEndLat(Double tavEndLat) {
		this.tavEndLat = tavEndLat;
	}

	public String getTavEndLocation() {
		return tavEndLocation;
	}

	public void setTavEndLocation(String tavEndLocation) {
		this.tavEndLocation = tavEndLocation;
	}

	public Double getTavEndLong() {
		return tavEndLong;
	}

	public void setTavEndLong(Double tavEndLong) {
		this.tavEndLong = tavEndLong;
	}
}