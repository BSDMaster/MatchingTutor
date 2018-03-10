package com.bsd.tutor.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
import java.util.Set;


/**
 * The persistent class for the MERGED_CLASS database table.
 * 
 */
@Entity
@Table(name="MERGED_CLASS")
@NamedQuery(name="MergedClass.findAll", query="SELECT m FROM MergedClass m")
public class MergedClass implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="MER_ID")
	private int merId;

	@Column(name="MER_MERGERATIO")
	private double merMergeratio;

	@Column(name="MER_MINDAYPERWEEK")
	private int merMindayperweek;

	@Column(name="MER_SAMEPARENT_FLAG")
	private String merSameparentFlag;

	@Column(name="MER_SAMESTU_FLAG")
	private String merSamestuFlag;

	@Column(name="MER_SAMESUB_FLAG")
	private String merSamesubFlag;

	@Column(name="MER_SAMESUBG_FLAG")
	private String merSamesubgFlag;

	@Column(name="MER_TUR_EXP")
	private String merTurExp;


	@Column(name="MER_TUR_SEX")
	private String merTurSex;

	@Column(name="MER_TUR_PRG_ID")
	private Integer merTurPrgId;


	//bi-directional many-to-one association to Class
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="MER_CLS1_ID")
	private Class clazz1;

	//bi-directional many-to-one association to Class
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="MER_CLS2_ID")
	private Class clazz2;

	//bi-directional many-to-one association to MergedClassAvailabletime
	@OneToMany(mappedBy="mergedClass", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private List<MergedClassAvailabletimeLocation> mergedClassAvailabletimes;

	//bi-directional many-to-one association to RecommendTutor
	@OneToMany(mappedBy="mergedClass",fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private List<RecommendTutor> recommendTutors;

	//bi-directional many-to-one association to ClassTutorStudy
	@OneToMany(mappedBy="mergedClass" ,fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private List<ClassTutorFaculty> classTutorFaculties;

	//bi-directional many-to-one association to ClassTutorStudy
	@OneToMany(mappedBy="mergedClass" ,fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private List<ClassTutorUniversity> classTutorUniversities;

	@Transient
	private Integer numberOfAvaDays;

	@Transient
	private Set<Integer> avaDays;

	@Transient
	private Integer macDays;

	public MergedClass() {
	}
	public MergedClass(Class clazz1,Class clazz2) {
		this.clazz1 = clazz1;
		this.clazz2 = clazz2;
	}

	public int getMerId() {
		return this.merId;
	}

	public void setMerId(int merId) {
		this.merId = merId;
	}

	public double getMerMergeratio() {
		return this.merMergeratio;
	}

	public void setMerMergeratio(double merMergeratio) {
		this.merMergeratio = merMergeratio;
	}

	public int getMerMindayperweek() {
		return this.merMindayperweek;
	}

	public void setMerMindayperweek(int merMindayperweek) {
		this.merMindayperweek = merMindayperweek;
	}

	public String getMerSameparentFlag() {
		return this.merSameparentFlag;
	}

	public void setMerSameparentFlag(String merSameparentFlag) {
		this.merSameparentFlag = merSameparentFlag;
	}

	public String getMerSamestuFlag() {
		return this.merSamestuFlag;
	}

	public void setMerSamestuFlag(String merSamestuFlag) {
		this.merSamestuFlag = merSamestuFlag;
	}

	public String getMerSamesubFlag() {
		return this.merSamesubFlag;
	}

	public void setMerSamesubFlag(String merSamesubFlag) {
		this.merSamesubFlag = merSamesubFlag;
	}

	public String getMerSamesubgFlag() {
		return this.merSamesubgFlag;
	}

	public void setMerSamesubgFlag(String merSamesubgFlag) {
		this.merSamesubgFlag = merSamesubgFlag;
	}

	public String getMerTurExp() {
		return this.merTurExp;
	}

	public void setMerTurExp(String merTurExp) {
		this.merTurExp = merTurExp;
	}

	public String getMerTurSex() {
		return this.merTurSex;
	}

	public void setMerTurSex(String merTurSex) {
		this.merTurSex = merTurSex;
	}

	public List<MergedClassAvailabletimeLocation> getMergedClassAvailabletimes() {
		return this.mergedClassAvailabletimes;
	}

	public void setMergedClassAvailabletimes(List<MergedClassAvailabletimeLocation> mergedClassAvailabletimes) {
		this.mergedClassAvailabletimes = mergedClassAvailabletimes;
	}

	public MergedClassAvailabletimeLocation addMergedClassAvailabletime(MergedClassAvailabletimeLocation mergedClassAvailabletime) {
		getMergedClassAvailabletimes().add(mergedClassAvailabletime);
		mergedClassAvailabletime.setMergedClass(this);

		return mergedClassAvailabletime;
	}

	public MergedClassAvailabletimeLocation removeMergedClassAvailabletime(MergedClassAvailabletimeLocation mergedClassAvailabletime) {
		getMergedClassAvailabletimes().remove(mergedClassAvailabletime);
		mergedClassAvailabletime.setMergedClass(null);

		return mergedClassAvailabletime;
	}

	public Class getClazz1() {
		return clazz1;
	}

	public void setClazz1(Class clazz1) {
		this.clazz1 = clazz1;
	}

	public Class getClazz2() {
		return clazz2;
	}

	public void setClazz2(Class clazz2) {
		this.clazz2 = clazz2;
	}

	public Integer getMerTurPrgId() {
		return merTurPrgId;
	}

	public void setMerTurPrgId(Integer merTurPrgId) {
		this.merTurPrgId = merTurPrgId;
	}

	public List<RecommendTutor> getRecommendTutors() {
		return recommendTutors;
	}

	public void setRecommendTutors(List<RecommendTutor> recommendTutors) {
		this.recommendTutors = recommendTutors;
	}

	public RecommendTutor addRecommendTutor(RecommendTutor recommendTutor) {
		getRecommendTutors().add(recommendTutor);
		recommendTutor.setMergedClass(this);

		return recommendTutor;
	}

	public RecommendTutor removeRecommendTutor(RecommendTutor recommendTutor) {
		getRecommendTutors().remove(recommendTutor);
		recommendTutor.setMergedClass(null);

		return recommendTutor;
	}

	public Integer getNumberOfAvaDays() {
		return numberOfAvaDays;
	}

	public void setNumberOfAvaDays(Integer numberOfAvaDays) {
		this.numberOfAvaDays = numberOfAvaDays;
	}

	public Set<Integer> getAvaDays() {
		return avaDays;
	}

	public void setAvaDays(Set<Integer> avaDays) {
		this.avaDays = avaDays;
	}

	public List<ClassTutorFaculty> getClassTutorFaculties() {
		return this.classTutorFaculties;
	}

	public void setClassTutorFaculties(List<ClassTutorFaculty> classTutorFaculties) {
		this.classTutorFaculties = classTutorFaculties;
	}

	public ClassTutorFaculty addClassTutorFaculties(ClassTutorFaculty classTutorFaculties) {
		getClassTutorFaculties().add(classTutorFaculties);
		classTutorFaculties.setMergedClass(this);

		return classTutorFaculties;
	}

	public ClassTutorFaculty removeClassTutorFaculties(ClassTutorFaculty classTutorFaculties) {
		getClassTutorFaculties().remove(classTutorFaculties);
		classTutorFaculties.setMergedClass(null);

		return classTutorFaculties;
	}

	public List<ClassTutorUniversity> getClassTutorUniversities() {
		return this.classTutorUniversities;
	}

	public void setClassTutorUniversities(List<ClassTutorUniversity> classTutorUniversities) {
		this.classTutorUniversities = classTutorUniversities;
	}

	public ClassTutorUniversity addClassTutorUniversities(ClassTutorUniversity classTutorUniversities) {
		getClassTutorUniversities().add(classTutorUniversities);
		classTutorUniversities.setMergedClass(this);

		return classTutorUniversities;
	}

	public ClassTutorUniversity removeClassTutorUniversities(ClassTutorUniversity classTutorUniversities) {
		getClassTutorUniversities().remove(classTutorUniversities);
		classTutorUniversities.setMergedClass(null);

		return classTutorUniversities;
	}

	public Integer getMacDays() {
		return macDays;
	}

	public void setMacDays(Integer macDays) {
		this.macDays = macDays;
	}

	@Override
	public String toString() {
		return "{" + clazz1 +
				", " + clazz2 +
				'}';
	}
}