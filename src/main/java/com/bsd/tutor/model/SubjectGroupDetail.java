package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the SUBJECT_GROUP_DETAIL database table.
 * 
 */
@Entity
@Table(name="SUBJECT_GROUP_DETAIL")
@NamedQuery(name="SubjectGroupDetail.findAll", query="SELECT s FROM SubjectGroupDetail s")
public class SubjectGroupDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="GRPD_ID")
	private Integer grpdId;

	@Column(name="GRPD_ACTIVE_FLAG")
	private String grpdActiveFlag;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="GRPD_CREATED_DATE")
	private Date grpdCreatedDate;

	@Column(name="GRPD_NAME")
	private String grpdName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="GRPD_UPDATED_DATE")
	private Date grpdUpdatedDate;

	//bi-directional many-to-one association to Class
	@OneToMany(mappedBy="subjectGroupDetail")
	private List<Class> clazzs;

	//bi-directional many-to-one association to SubjectGroup
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="GRPD_GRP_ID")
	private SubjectGroup subjectGroup;

	public SubjectGroupDetail() {
	}

	public Integer getGrpdId() {
		return this.grpdId;
	}

	public void setGrpdId(Integer grpdId) {
		this.grpdId = grpdId;
	}

	public String getGrpdActiveFlag() {
		return this.grpdActiveFlag;
	}

	public void setGrpdActiveFlag(String grpdActiveFlag) {
		this.grpdActiveFlag = grpdActiveFlag;
	}

	public Date getGrpdCreatedDate() {
		return this.grpdCreatedDate;
	}

	public void setGrpdCreatedDate(Date grpdCreatedDate) {
		this.grpdCreatedDate = grpdCreatedDate;
	}

	public String getGrpdName() {
		return this.grpdName;
	}

	public void setGrpdName(String grpdName) {
		this.grpdName = grpdName;
	}

	public Date getGrpdUpdatedDate() {
		return this.grpdUpdatedDate;
	}

	public void setGrpdUpdatedDate(Date grpdUpdatedDate) {
		this.grpdUpdatedDate = grpdUpdatedDate;
	}

	public List<Class> getClazzs() {
		return this.clazzs;
	}

	public void setClazzs(List<Class> clazzs) {
		this.clazzs = clazzs;
	}

	public Class addClazz(Class clazz) {
		getClazzs().add(clazz);
		clazz.setSubjectGroupDetail(this);

		return clazz;
	}

	public Class removeClazz(Class clazz) {
		getClazzs().remove(clazz);
		clazz.setSubjectGroupDetail(null);

		return clazz;
	}

	public SubjectGroup getSubjectGroup() {
		return this.subjectGroup;
	}

	public void setSubjectGroup(SubjectGroup subjectGroup) {
		this.subjectGroup = subjectGroup;
	}

}