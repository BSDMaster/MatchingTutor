package com.bsd.tutor.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the CLASS database table.
 * 
 */
@Entity
@NamedQuery(name="Class.findAll", query="SELECT c FROM Class c")
public class Class implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="CLS_ID")
	private Long clsId;

	@Column(name="CLS_DAYPERWEEK")
	private Long clsDayperweek;

	@Column(name="CLS_DURATION")
	private Double clsDuration;

	@Column(name="CLS_IS_MERGE")
	private String clsIsMerge;

	@Column(name="CLS_NUMOFSTU")
	private Long clsNumofstu;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CLS_PAIDDATE")
	private Date clsPaiddate;

	@Column(name="CLS_PRICE")
	private Double clsPrice;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CLS_STARTDATE")
	private Date clsStartdate;

	@Column(name="CLS_TOTALHOURS")
	private Long clsTotalhours;


	@Column(name="CLS_TUTOR_EXP")
	private String clsTutorExp;

	@Column(name="CLS_TUTOR_SEX")
	private String clsTutorSex;

	@Column(name="CLS_PRG_ID")
	private Long clsPrgId;

	//bi-directional many-to-one association to ClassStatus
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="CLS_STATUS_ID")
	private ClassStatus classStatus;

	//bi-directional many-to-one association to Student
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="CLS_STU_ID")
	private Student student;

	//bi-directional many-to-one association to Program
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="CLS_SUBJ_ID")
	private Subject subject;

	//bi-directional many-to-one association to SubjectGroupDetail
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="CLS_GRPD_ID")
	private SubjectGroupDetail subjectGroupDetail;

	//bi-directional many-to-one association to ClassTutorStudy
	@OneToMany(mappedBy="clazz" ,fetch=FetchType.EAGER)//, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
	private List<ClassTutorFaculty> classTutorFaculties;

	//bi-directional many-to-one association to ClassTutorStudy
	@OneToMany(mappedBy="clazz" ,fetch=FetchType.EAGER)//, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<ClassTutorUniversity> classTutorUniversities;

	//bi-directional many-to-one association to CourseCalendar
	@OneToMany(mappedBy="clazz",fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<CourseCalendar> courseCalendars;

	//bi-directional many-to-one association to RecommendTutor
	@OneToMany(mappedBy="clazz",fetch=FetchType.EAGER)//, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
	private List<RecommendTutor> recommendTutors;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="CLS_TUR_ID")
	private Tutor tutor;

	@Transient
	private boolean tmpFLag = false;

	public Class() {
	}

	public Long getClsId() {
		return this.clsId;
	}

	public void setClsId(Long clsId) {
		this.clsId = clsId;
	}

	public Long getClsDayperweek() {
		return this.clsDayperweek;
	}

	public void setClsDayperweek(Long clsDayperweek) {
		this.clsDayperweek = clsDayperweek;
	}

	public Double getClsDuration() {
		return this.clsDuration;
	}

	public void setClsDuration(Double clsDuration) {
		this.clsDuration = clsDuration;
	}

	public String getClsIsMerge() {
		return this.clsIsMerge;
	}

	public void setClsIsMerge(String clsIsMerge) {
		this.clsIsMerge = clsIsMerge;
	}

	public Long getClsNumofstu() {
		return this.clsNumofstu;
	}

	public void setClsNumofstu(Long clsNumofstu) {
		this.clsNumofstu = clsNumofstu;
	}

	public Date getClsPaiddate() {
		return this.clsPaiddate;
	}

	public void setClsPaiddate(Date clsPaiddate) {
		this.clsPaiddate = clsPaiddate;
	}

	public Double getClsPrice() {
		return this.clsPrice;
	}

	public void setClsPrice(Double clsPrice) {
		this.clsPrice = clsPrice;
	}

	public Date getClsStartdate() {
		return this.clsStartdate;
	}

	public void setClsStartdate(Date clsStartdate) {
		this.clsStartdate = clsStartdate;
	}

	public Long getClsTotalhours() {
		return this.clsTotalhours;
	}

	public void setClsTotalhours(Long clsTotalhours) {
		this.clsTotalhours = clsTotalhours;
	}

	public String getClsTutorExp() {
		return this.clsTutorExp;
	}

	public void setClsTutorExp(String clsTutorExp) {
		this.clsTutorExp = clsTutorExp;
	}

	public String getClsTutorSex() {
		return this.clsTutorSex;
	}

	public void setClsTutorSex(String clsTutorSex) {
		this.clsTutorSex = clsTutorSex;
	}

	public ClassStatus getClassStatus() {
		return this.classStatus;
	}

	public void setClassStatus(ClassStatus classStatus) {
		this.classStatus = classStatus;
	}

	public Student getStudent() {
		return this.student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Subject getSubject() {
		return this.subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public SubjectGroupDetail getSubjectGroupDetail() {
		return this.subjectGroupDetail;
	}

	public void setSubjectGroupDetail(SubjectGroupDetail subjectGroupDetail) {
		this.subjectGroupDetail = subjectGroupDetail;
	}

	public List<ClassTutorFaculty> getClassTutorFaculties() {
		return this.classTutorFaculties;
	}

	public void setClassTutorFaculties(List<ClassTutorFaculty> classTutorFaculties) {
		this.classTutorFaculties = classTutorFaculties;
	}

	public ClassTutorFaculty addClassTutorFaculties(ClassTutorFaculty classTutorFaculties) {
		getClassTutorFaculties().add(classTutorFaculties);
		classTutorFaculties.setClazz(this);

		return classTutorFaculties;
	}

	public ClassTutorFaculty removeClassTutorFaculties(ClassTutorFaculty classTutorFaculties) {
		getClassTutorFaculties().remove(classTutorFaculties);
		classTutorFaculties.setClazz(null);

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
		classTutorUniversities.setClazz(this);

		return classTutorUniversities;
	}

	public ClassTutorUniversity removeClassTutorUniversities(ClassTutorUniversity classTutorUniversities) {
		getClassTutorUniversities().remove(classTutorUniversities);
		classTutorUniversities.setClazz(null);

		return classTutorUniversities;
	}
/*
	public List<CourseCalendar> getCourseCalendars() {
		return this.courseCalendars;
	}

	public void setCourseCalendars(List<CourseCalendar> courseCalendars) {
		this.courseCalendars = courseCalendars;
	}

	public CourseCalendar addCourseCalendar(CourseCalendar courseCalendar) {
		getCourseCalendars().add(courseCalendar);
		courseCalendar.setClazz(this);

		return courseCalendar;
	}

	public CourseCalendar removeCourseCalendar(CourseCalendar courseCalendar) {
		getCourseCalendars().remove(courseCalendar);
		courseCalendar.setClazz(null);

		return courseCalendar;
	}
*/
	public List<RecommendTutor> getRecommendTutors() {
		return this.recommendTutors;
	}

	public void setRecommendTutors(List<RecommendTutor> recommendTutors) {
		this.recommendTutors = recommendTutors;
	}

	public RecommendTutor addRecommendTutor(RecommendTutor recommendTutor) {
		getRecommendTutors().add(recommendTutor);
		recommendTutor.setClazz(this);

		return recommendTutor;
	}

	public RecommendTutor removeRecommendTutor(RecommendTutor recommendTutor) {
		getRecommendTutors().remove(recommendTutor);
		recommendTutor.setClazz(null);

		return recommendTutor;
	}


	public Long getClsPrgId() {
		return clsPrgId;
	}

	public void setClsPrgId(Long clsPrgId) {
		this.clsPrgId = clsPrgId;
	}


	public List<CourseCalendar> getCourseCalendars() {
		return courseCalendars;
	}

	public void setCourseCalendars(List<CourseCalendar> courseCalendars) {
		this.courseCalendars = courseCalendars;
	}

	public Tutor getTutor() {
		return tutor;
	}

	public void setTutor(Tutor tutor) {
		this.tutor = tutor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Class)) return false;

		Class aClass = (Class) o;

		return clsId.equals(aClass.clsId);

	}

	@Override
	public int hashCode() {
		return clsId.hashCode();
	}

	@Override
	public String toString() {
		return clsId.toString();
	}

	public boolean isTmpFLag() {
		return tmpFLag;
	}

	public void setTmpFLag(boolean tmpFLag) {
		this.tmpFLag = tmpFLag;
	}
}