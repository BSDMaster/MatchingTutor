package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the MERGED_CLASS_AVAILABLETIME database table.
 * 
 */
@Entity
@Table(name="MERGED_CLASS_AVAILABLETIMELOCATION")
@NamedQuery(name="MergedClassAvailabletimeLocation.findAll", query="SELECT m FROM MergedClassAvailabletimeLocation m")
public class MergedClassAvailabletimeLocation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="MEAV_ID")
	private Long meavId;

	@Column(name="MEAV_DAY_ID")
	private Long meavDayId;

	@Column(name="MEAV_END_TIME")
	private Double meavEndTime;

	@Column(name="MEAV_MERGE_FLAG")
	private String meavMergeFlag;

	@Column(name="MERV_START_TIME")
	private Double mervStartTime;

	@Column(name="MERV_CLS_START")
	private Long mervClsStart;

	@Column(name="MERV_LAT")
	private Double mervLat;

	@Column(name="MERV_LOCATION")
	private String mervLocation;

	@Column(name="MERV_LONG")
	private Double mervLong;

	//bi-directional many-to-one association to MergedClass
	@ManyToOne
	@JoinColumn(name="MERV_MER_ID")
	private MergedClass mergedClass;

	public MergedClassAvailabletimeLocation() {
	}
	public MergedClassAvailabletimeLocation(MergedClass mergedClass, Long meavDayId) {
		this.mergedClass = mergedClass;
		this.meavDayId = meavDayId;
	}
	public Long getMeavId() {
		return this.meavId;
	}

	public void setMeavId(Long meavId) {
		this.meavId = meavId;
	}

	public Long getMeavDayId() {
		return this.meavDayId;
	}

	public void setMeavDayId(Long meavDayId) {
		this.meavDayId = meavDayId;
	}

	public Double getMeavEndTime() {
		return this.meavEndTime;
	}

	public void setMeavEndTime(Double meavEndTime) {
		this.meavEndTime = meavEndTime;
	}

	public String getMeavMergeFlag() {
		return this.meavMergeFlag;
	}

	public void setMeavMergeFlag(String meavMergeFlag) {
		this.meavMergeFlag = meavMergeFlag;
	}

	public Double getMervStartTime() {
		return this.mervStartTime;
	}

	public void setMervStartTime(Double mervStartTime) {
		this.mervStartTime = mervStartTime;
	}

	public MergedClass getMergedClass() {
		return this.mergedClass;
	}

	public void setMergedClass(MergedClass mergedClass) {
		this.mergedClass = mergedClass;
	}

	public Long getMervClsStart() {
		return mervClsStart;
	}

	public void setMervClsStart(Long mervClsStart) {
		this.mervClsStart = mervClsStart;
	}

	public Double getMervLat() {
		return mervLat;
	}

	public void setMervLat(Double mervLat) {
		this.mervLat = mervLat;
	}

	public String getMervLocation() {
		return mervLocation;
	}

	public void setMervLocation(String mervLocation) {
		this.mervLocation = mervLocation;
	}

	public Double getMervLong() {
		return mervLong;
	}

	public void setMervLong(Double mervLong) {
		this.mervLong = mervLong;
	}
}