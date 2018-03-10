package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the SUBJECT_DETAIL database table.
 * 
 */
@Entity
@Table(name="SUBJECT_DETAIL")
@NamedQuery(name="SubjectDetail.findAll", query="SELECT s FROM SubjectDetail s")
public class SubjectDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SUBD_ID")
	private Integer subdId;

	//bi-directional many-to-one association to Subject
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SUBD_SUBJ_ID")
	private Subject subject;

	//bi-directional many-to-one association to SubjectGroup
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SUBD_GRP_ID")
	private SubjectGroup subjectGroup;

	public SubjectDetail() {
	}

	public Integer getSubdId() {
		return this.subdId;
	}

	public void setSubdId(Integer subdId) {
		this.subdId = subdId;
	}

	public Subject getSubject() {
		return this.subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public SubjectGroup getSubjectGroup() {
		return this.subjectGroup;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SubjectDetail)) return false;

		SubjectDetail that = (SubjectDetail) o;

		return subdId.equals(that.subdId);

	}

	@Override
	public int hashCode() {
		return subdId.hashCode();
	}

	public void setSubjectGroup(SubjectGroup subjectGroup) {
		this.subjectGroup = subjectGroup;
	}



}