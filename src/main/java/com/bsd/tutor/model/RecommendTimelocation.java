package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.criteria.Predicate;


/**
 * The persistent class for the RECOMMEND_TIMELOCATION database table.
 * 
 */
@Entity
@Table(name="RECOMMEND_TIMELOCATION")
@NamedQuery(name="RecommendTimelocation.findAll", query="SELECT r FROM RecommendTimelocation r")
public class RecommendTimelocation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="RECD_ID")
	private Integer recdId;

	@Column(name="RECD_DAY_ID")
	private Integer recdDayId;

	@Column(name="RECD_END")
	private Double recdEnd;

	@Column(name="RECD_START")
	private Double recdStart;

	@Column(name="RECD_TRAVELTIME")
	private Double recdTraveltime;

	@Column(name="RECD_TRAVELTIME_1")
	private Double recdTraveltime1;

	@Column(name="RECD_TRAVELTIME_2")
	private Double recdTraveltime2;

	@Column(name="RECD_TRAVELSTART")
	private Double recdTravelStart;

	@Column(name="RECD_TRAVELEND")
	private Double recdTravelEnd;

	//bi-directional many-to-one association to RecommendTutor
	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="RECD_REC_ID")
	private RecommendTutor recommendTutor;

	@Transient
	private boolean isOverlapse;


	public RecommendTimelocation() {
	}

	public Integer getRecdId() {
		return this.recdId;
	}

	public void setRecdId(Integer recdId) {
		this.recdId = recdId;
	}

	public Integer getRecdDayId() {
		return this.recdDayId;
	}

	public void setRecdDayId(Integer recdDayId) {
		this.recdDayId = recdDayId;
	}

	public Double getRecdEnd() {
		return this.recdEnd;
	}

	public void setRecdEnd(Double recdEnd) {
		this.recdEnd = recdEnd;
	}

	public Double getRecdStart() {
		return this.recdStart;
	}

	public void setRecdStart(Double recdStart) {
		this.recdStart = recdStart;
	}

	public Double getRecdTraveltime() {
		return this.recdTraveltime;
	}

	public void setRecdTraveltime(Double recdTraveltime) {
		this.recdTraveltime = recdTraveltime;
	}

	public Double getRecdTraveltime1() {
		return this.recdTraveltime1;
	}

	public void setRecdTraveltime1(Double recdTraveltime1) {
		this.recdTraveltime1 = recdTraveltime1;
	}

	public Double getRecdTraveltime2() {
		return this.recdTraveltime2;
	}

	public void setRecdTraveltime2(Double recdTraveltime2) {
		this.recdTraveltime2 = recdTraveltime2;
	}

	public RecommendTutor getRecommendTutor() {
		return this.recommendTutor;
	}

	public void setRecommendTutor(RecommendTutor recommendTutor) {
		this.recommendTutor = recommendTutor;
	}

	public boolean isOverlapse() {
		return isOverlapse;
	}

	public void setOverlapse(boolean overlapse) {
		isOverlapse = overlapse;
	}

	public Double getRecdTravelStart() {
		return recdTravelStart;
	}

	public void setRecdTravelStart(Double recdTravelStart) {
		this.recdTravelStart = recdTravelStart;
	}

	public Double getRecdTravelEnd() {
		return recdTravelEnd;
	}

	public void setRecdTravelEnd(Double recdTravelEnd) {
		this.recdTravelEnd = recdTravelEnd;
	}
}