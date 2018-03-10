package com.bsd.tutor.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the TUTOR database table.
 * 
 */
@Entity
@NamedQuery(name="Tutor.findAll", query="SELECT t FROM Tutor t")
public class Tutor implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TUR_ID")
	private Integer turId;

	@Column(name="TUR_ACTIVE_FLAG")
	private String turActiveFlag;

	@Column(name="TUR_ADDRESS")
	private String turAddress;

	@Temporal(TemporalType.DATE)
	@Column(name="TUR_BIRTHDATE")
	private Date turBirthdate;

	@Column(name="TUR_BLACKLIST_FLAG")
	private String turBlacklistFlag;

	@Column(name="TUR_CLIP")
	private String turClip;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TUR_CREATED_DATE")
	private Date turCreatedDate;

	@Column(name="TUR_EMAIL")
	private String turEmail;

	@Column(name="TUR_FILE")
	private String turFile;

	@Column(name="TUR_FIRSTNAME")
	private String turFirstname;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TUR_LASTLOGIN_DATETIME")
	private Date turLastloginDatetime;

	@Column(name="TUR_LASTNAME")
	private String turLastname;

	@Column(name="TUR_NICKNAME")
	private String turNickname;

	@Column(name="TUR_PASS")
	private String turPass;

	@Column(name="TUR_PROFILEPIC")
	private String turProfilepic;

	@Column(name="TUR_SEX")
	private String turSex;

	@Column(name="TUR_STATUS")
	private String turStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TUR_UPDATED_FLAG")
	private Date turUpdatedFlag;

	@Column(name="TUR_VERIFIED_FLAG")
	private String turVerifiedFlag;

	//bi-directional many-to-one association to FacultyOfUniversity
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TUR_FUN_ID")
	private FacultyOfUniversity facultyOfUniversity;

	//bi-directional many-to-one association to TutorAvailableTimeloacation
	@OneToMany(mappedBy="tutor",fetch=FetchType.EAGER)//, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<TutorAvailableTimeloacation> tutorAvailableTimeloacations;

	//bi-directional many-to-one association to TutorProgram
	@OneToMany(mappedBy="tutor",fetch=FetchType.EAGER)//, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<TutorProgram> tutorPrograms;

	//bi-directional many-to-one association to TutorSubject
	@OneToMany(mappedBy="tutor",fetch=FetchType.EAGER)//, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<TutorSubject> tutorSubject;

	public Tutor() {
	}

	public Integer getTurId() {
		return this.turId;
	}

	public void setTurId(Integer turId) {
		this.turId = turId;
	}

	public String getTurActiveFlag() {
		return this.turActiveFlag;
	}

	public void setTurActiveFlag(String turActiveFlag) {
		this.turActiveFlag = turActiveFlag;
	}

	public String getTurAddress() {
		return this.turAddress;
	}

	public void setTurAddress(String turAddress) {
		this.turAddress = turAddress;
	}

	public Date getTurBirthdate() {
		return this.turBirthdate;
	}

	public void setTurBirthdate(Date turBirthdate) {
		this.turBirthdate = turBirthdate;
	}

	public String getTurBlacklistFlag() {
		return this.turBlacklistFlag;
	}

	public void setTurBlacklistFlag(String turBlacklistFlag) {
		this.turBlacklistFlag = turBlacklistFlag;
	}

	public String getTurClip() {
		return this.turClip;
	}

	public void setTurClip(String turClip) {
		this.turClip = turClip;
	}

	public Date getTurCreatedDate() {
		return this.turCreatedDate;
	}

	public void setTurCreatedDate(Date turCreatedDate) {
		this.turCreatedDate = turCreatedDate;
	}

	public String getTurEmail() {
		return this.turEmail;
	}

	public void setTurEmail(String turEmail) {
		this.turEmail = turEmail;
	}

	public String getTurFile() {
		return this.turFile;
	}

	public void setTurFile(String turFile) {
		this.turFile = turFile;
	}

	public String getTurFirstname() {
		return this.turFirstname;
	}

	public void setTurFirstname(String turFirstname) {
		this.turFirstname = turFirstname;
	}

	public Date getTurLastloginDatetime() {
		return this.turLastloginDatetime;
	}

	public void setTurLastloginDatetime(Date turLastloginDatetime) {
		this.turLastloginDatetime = turLastloginDatetime;
	}

	public String getTurLastname() {
		return this.turLastname;
	}

	public void setTurLastname(String turLastname) {
		this.turLastname = turLastname;
	}

	public String getTurNickname() {
		return this.turNickname;
	}

	public void setTurNickname(String turNickname) {
		this.turNickname = turNickname;
	}

	public String getTurPass() {
		return this.turPass;
	}

	public void setTurPass(String turPass) {
		this.turPass = turPass;
	}

	public String getTurProfilepic() {
		return this.turProfilepic;
	}

	public void setTurProfilepic(String turProfilepic) {
		this.turProfilepic = turProfilepic;
	}

	public String getTurSex() {
		return this.turSex;
	}

	public void setTurSex(String turSex) {
		this.turSex = turSex;
	}

	public String getTurStatus() {
		return this.turStatus;
	}

	public void setTurStatus(String turStatus) {
		this.turStatus = turStatus;
	}

	public Date getTurUpdatedFlag() {
		return this.turUpdatedFlag;
	}

	public void setTurUpdatedFlag(Date turUpdatedFlag) {
		this.turUpdatedFlag = turUpdatedFlag;
	}

	public String getTurVerifiedFlag() {
		return this.turVerifiedFlag;
	}

	public void setTurVerifiedFlag(String turVerifiedFlag) {
		this.turVerifiedFlag = turVerifiedFlag;
	}

	public FacultyOfUniversity getFacultyOfUniversity() {
		return this.facultyOfUniversity;
	}

	public void setFacultyOfUniversity(FacultyOfUniversity facultyOfUniversity) {
		this.facultyOfUniversity = facultyOfUniversity;
	}

	public List<TutorAvailableTimeloacation> getTutorAvailableTimeloacations() {
		return this.tutorAvailableTimeloacations;
	}

	public void setTutorAvailableTimeloacations(List<TutorAvailableTimeloacation> tutorAvailableTimeloacations) {
		this.tutorAvailableTimeloacations = tutorAvailableTimeloacations;
	}

	public TutorAvailableTimeloacation addTutorAvailableTimeloacation(TutorAvailableTimeloacation tutorAvailableTimeloacation) {
		getTutorAvailableTimeloacations().add(tutorAvailableTimeloacation);
		tutorAvailableTimeloacation.setTutor(this);

		return tutorAvailableTimeloacation;
	}

	public TutorAvailableTimeloacation removeTutorAvailableTimeloacation(TutorAvailableTimeloacation tutorAvailableTimeloacation) {
		getTutorAvailableTimeloacations().remove(tutorAvailableTimeloacation);
		tutorAvailableTimeloacation.setTutor(null);

		return tutorAvailableTimeloacation;
	}

	public List<TutorProgram> getTutorPrograms() {
		return tutorPrograms;
	}

	public void setTutorPrograms(List<TutorProgram> tutorPrograms) {
		this.tutorPrograms = tutorPrograms;
	}

	public List<TutorSubject> getTutorSubject() {
		return tutorSubject;
	}

	public void setTutorSubject(List<TutorSubject> tutorSubject) {
		this.tutorSubject = tutorSubject;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tutor)) return false;

		Tutor tutor = (Tutor) o;

		return turId.equals(tutor.turId);

	}

	@Override
	public int hashCode() {
		return turId.hashCode();
	}
}