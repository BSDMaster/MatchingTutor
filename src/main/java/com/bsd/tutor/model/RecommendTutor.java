package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the RECOMMEND_TUTOR database table.
 * 
 */
@Entity
@Table(name="RECOMMEND_TUTOR")
@NamedQuery(name="RecommendTutor.findAll", query="SELECT r FROM RecommendTutor r")
public class RecommendTutor implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="REC_ID")
	private Integer recId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="REC_DATETIME")
	private Date recDatetime = new Date();

	@Column(name="REC_ORDER")
	private Integer recOrder;

	@Column(name="REC_TUTOR_EXP_FLAG")
	private String recTutorExpFlag;

	@Column(name="REC_TUTOR_FACULTY_FLAG")
	private String recTutorFacultyFlag;

	@Column(name="REC_TUTOR_ORDER")
	private Integer recTutorOrder;

	@Column(name="REC_TUTOR_SEX_FLAG")
	private String recTutorSexFlag;

	@Column(name="REC_TUTOR_UNIVERSITY_FLAG")
	private String recTutorUniversityFlag;

	@Column(name="REC_TUTOR_YEAR_EXP")
	private Integer recTutorYearExp;

	//bi-directional many-to-one association to RecommendTimelocation
	@OneToMany(mappedBy="recommendTutor", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private List<RecommendTimelocation> recommendTimelocations;

	//bi-directional many-to-one association to Class
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="REC_COS_ID")
	private Class clazz;

	//bi-directional many-to-one association to Class
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="REC_MER_ID")
	private MergedClass mergedClass;

	//bi-directional many-to-one association to Tutor
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="REC_TUR_ID")
	private Tutor tutor;

	@Transient
	private Double minTravelTime;

	@Transient
	private Double availableRatio;

	@Transient
	private Double availableDays;

	@Transient
	private Integer numOfDays = 0;

	public RecommendTutor() {
	}

	public Integer getRecId() {
		return this.recId;
	}

	public void setRecId(Integer recId) {
		this.recId = recId;
	}

	public Date getRecDatetime() {
		return this.recDatetime;
	}

	public void setRecDatetime(Date recDatetime) {
		this.recDatetime = recDatetime;
	}

	public Integer getRecOrder() {
		return this.recOrder;
	}

	public void setRecOrder(Integer recOrder) {
		this.recOrder = recOrder;
	}

	public String getRecTutorExpFlag() {
		return this.recTutorExpFlag;
	}

	public void setRecTutorExpFlag(String recTutorExpFlag) {
		this.recTutorExpFlag = recTutorExpFlag;
	}

	public String getRecTutorFacultyFlag() {
		return this.recTutorFacultyFlag;
	}

	public void setRecTutorFacultyFlag(String recTutorFacultyFlag) {
		this.recTutorFacultyFlag = recTutorFacultyFlag;
	}

	public Integer getRecTutorOrder() {
		return this.recTutorOrder;
	}

	public void setRecTutorOrder(Integer recTutorOrder) {
		this.recTutorOrder = recTutorOrder;
	}

	public String getRecTutorSexFlag() {
		return this.recTutorSexFlag;
	}

	public void setRecTutorSexFlag(String recTutorSexFlag) {
		this.recTutorSexFlag = recTutorSexFlag;
	}

	public String getRecTutorUniversityFlag() {
		return this.recTutorUniversityFlag;
	}

	public void setRecTutorUniversityFlag(String recTutorUniversityFlag) {
		this.recTutorUniversityFlag = recTutorUniversityFlag;
	}

	public Integer getRecTutorYearExp() {
		return this.recTutorYearExp;
	}

	public void setRecTutorYearExp(Integer recTutorYearExp) {
		this.recTutorYearExp = recTutorYearExp;
	}

	public List<RecommendTimelocation> getRecommendTimelocations() {
		return this.recommendTimelocations;
	}

	public void setRecommendTimelocations(List<RecommendTimelocation> recommendTimelocations) {
		this.recommendTimelocations = recommendTimelocations;
	}

	public RecommendTimelocation addRecommendTimelocation(RecommendTimelocation recommendTimelocation) {
		getRecommendTimelocations().add(recommendTimelocation);
		recommendTimelocation.setRecommendTutor(this);

		return recommendTimelocation;
	}

	public RecommendTimelocation removeRecommendTimelocation(RecommendTimelocation recommendTimelocation) {
		getRecommendTimelocations().remove(recommendTimelocation);
		recommendTimelocation.setRecommendTutor(null);

		return recommendTimelocation;
	}

	public Class getClazz() {
		return this.clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public Tutor getTutor() {
		return this.tutor;
	}

	public void setTutor(Tutor tutor) {
		this.tutor = tutor;
	}


	public Double getMinTravelTime() {
		return minTravelTime;
	}

	public void setMinTravelTime(Double minTravelTime) {
		this.minTravelTime = minTravelTime;
	}

	public Double getAvailableRatio() {
		return availableRatio;
	}

	public void setAvailableRatio(Double availableRatio) {
		this.availableRatio = availableRatio;
	}

	public MergedClass getMergedClass() {
		return mergedClass;
	}

	public void setMergedClass(MergedClass mergedClass) {
		this.mergedClass = mergedClass;
	}

	public Double getAvailableDays() {
		return availableDays;
	}
/*
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RecommendTutor)) return false;

		RecommendTutor that = (RecommendTutor) o;

		if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;
		if (mergedClass != null ? !mergedClass.equals(that.mergedClass) : that.mergedClass != null) return false;
		return tutor != null ? tutor.equals(that.tutor) : that.tutor == null;

	}

	@Override
	public int hashCode() {
		int result = clazz != null ? clazz.hashCode() : 0;
		result = 31 * result + (mergedClass != null ? mergedClass.hashCode() : 0);
		result = 31 * result + (tutor != null ? tutor.hashCode() : 0);
		return result;
	}
*/
	public void setAvailableDays(Double availableDays) {
		this.availableDays = availableDays;
	}

	public Integer getNumOfDays() {
		return numOfDays;
	}

	public void setNumOfDays(Integer numOfDays) {
		this.numOfDays = numOfDays;
	}
}