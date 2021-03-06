package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TUTOR_SUBJECT database table.
 * 
 */
@Entity
@Table(name="TUTOR_SUBJECT")
@NamedQuery(name="TutorSubject.findAll", query="SELECT t FROM TutorSubject t")
public class TutorSubject implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TSBJ_ID")
	private Long tsbjId;

	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="TSBJ_TUR_ID")
	private Tutor tutor;

	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="TSBJ_SUBD_ID")
	private SubjectDetail subjectDetail;


	@Column(name="TSBJ_EXP")
	private Long tsbjExp;

	public TutorSubject() {
	}


	public Long getTsbjExp() {
		return this.tsbjExp;
	}

	public void setTsbjExp(Long tsbjExp) {
		this.tsbjExp = tsbjExp;
	}

	public Long getTsbjId() {
		return tsbjId;
	}

	public void setTsbjId(Long tsbjId) {
		this.tsbjId = tsbjId;
	}

	public Tutor getTutor() {
		return tutor;
	}

	public void setTutor(Tutor tutor) {
		this.tutor = tutor;
	}

	public SubjectDetail getSubjectDetail() {
		return subjectDetail;
	}

	public void setSubjectDetail(SubjectDetail subjectDetail) {
		this.subjectDetail = subjectDetail;
	}
}