package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the SUBJECT database table.
 * 
 */

@Entity
@NamedQuery(name="Subject.findAll", query="SELECT s FROM Subject s")
public class Subject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SUBJ_ID")
	private Long subjId;

	@Column(name="SUBJ_ACTIVE_FLAG")
	private String subjActiveFlag;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SUBJ_CREATED_DATE")
	private Date subjCreatedDate;

	@Column(name="SUBJ_NAME")
	private String subjName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SUBJ_UPDATED_DATE")
	private Date subjUpdatedDate;

	//bi-directional many-to-one association to Class
	@OneToMany(mappedBy="subject")
	private List<Class> clazzs;

	//bi-directional many-to-one association to SubjectDetail
	@OneToMany(mappedBy="subject")
	private List<SubjectDetail> subjectDetails;

	public Subject() {
	}

	public Long getSubjId() {
		return this.subjId;
	}

	public void setSubjId(Long subjId) {
		this.subjId = subjId;
	}

	public String getSubjActiveFlag() {
		return this.subjActiveFlag;
	}

	public void setSubjActiveFlag(String subjActiveFlag) {
		this.subjActiveFlag = subjActiveFlag;
	}

	public Date getSubjCreatedDate() {
		return this.subjCreatedDate;
	}

	public void setSubjCreatedDate(Date subjCreatedDate) {
		this.subjCreatedDate = subjCreatedDate;
	}

	public String getSubjName() {
		return this.subjName;
	}

	public void setSubjName(String subjName) {
		this.subjName = subjName;
	}

	public Date getSubjUpdatedDate() {
		return this.subjUpdatedDate;
	}

	public void setSubjUpdatedDate(Date subjUpdatedDate) {
		this.subjUpdatedDate = subjUpdatedDate;
	}

	public List<Class> getClazzs() {
		return this.clazzs;
	}

	public void setClazzs(List<Class> clazzs) {
		this.clazzs = clazzs;
	}

	public Class addClazz(Class clazz) {
		getClazzs().add(clazz);
		clazz.setSubject(this);

		return clazz;
	}

	public Class removeClazz(Class clazz) {
		getClazzs().remove(clazz);
		clazz.setSubject(null);

		return clazz;
	}

	public List<SubjectDetail> getSubjectDetails() {
		return this.subjectDetails;
	}

	public void setSubjectDetails(List<SubjectDetail> subjectDetails) {
		this.subjectDetails = subjectDetails;
	}

	public SubjectDetail addSubjectDetail(SubjectDetail subjectDetail) {
		getSubjectDetails().add(subjectDetail);
		subjectDetail.setSubject(this);

		return subjectDetail;
	}

	public SubjectDetail removeSubjectDetail(SubjectDetail subjectDetail) {
		getSubjectDetails().remove(subjectDetail);
		subjectDetail.setSubject(null);

		return subjectDetail;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Subject)) return false;

		Subject subject = (Subject) o;

		return subjId.equals(subject.subjId);

	}

	@Override
	public int hashCode() {
		return subjId.hashCode();
	}
}