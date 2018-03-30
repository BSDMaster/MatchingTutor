package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the SUBJECT_GROUP database table.
 * 
 */
@Entity
@Table(name="SUBJECT_GROUP")
@NamedQuery(name="SubjectGroup.findAll", query="SELECT s FROM SubjectGroup s")
public class SubjectGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="GRP_ID")
	private Long grpId;

	@Column(name="GRP_ACTIVE_FLAG")
	private String grpActiveFlag;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="GRP_CREATED_DATE")
	private Date grpCreatedDate;

	@Column(name="GRP_NAME")
	private String grpName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="GRP_UPDATED_DATE")
	private Date grpUpdatedDate;

	//bi-directional many-to-one association to SubjectDetail
	@OneToMany(mappedBy="subjectGroup")
	private List<SubjectDetail> subjectDetails;

	//bi-directional many-to-one association to SubjectGroupDetail
	@OneToMany(mappedBy="subjectGroup")
	private List<SubjectGroupDetail> subjectGroupDetails;

	public SubjectGroup() {
	}

	public Long getGrpId() {
		return this.grpId;
	}

	public void setGrpId(Long grpId) {
		this.grpId = grpId;
	}

	public String getGrpActiveFlag() {
		return this.grpActiveFlag;
	}

	public void setGrpActiveFlag(String grpActiveFlag) {
		this.grpActiveFlag = grpActiveFlag;
	}

	public Date getGrpCreatedDate() {
		return this.grpCreatedDate;
	}

	public void setGrpCreatedDate(Date grpCreatedDate) {
		this.grpCreatedDate = grpCreatedDate;
	}

	public String getGrpName() {
		return this.grpName;
	}

	public void setGrpName(String grpName) {
		this.grpName = grpName;
	}

	public Date getGrpUpdatedDate() {
		return this.grpUpdatedDate;
	}

	public void setGrpUpdatedDate(Date grpUpdatedDate) {
		this.grpUpdatedDate = grpUpdatedDate;
	}

	public List<SubjectDetail> getSubjectDetails() {
		return this.subjectDetails;
	}

	public void setSubjectDetails(List<SubjectDetail> subjectDetails) {
		this.subjectDetails = subjectDetails;
	}

	public SubjectDetail addSubjectDetail(SubjectDetail subjectDetail) {
		getSubjectDetails().add(subjectDetail);
		subjectDetail.setSubjectGroup(this);

		return subjectDetail;
	}

	public SubjectDetail removeSubjectDetail(SubjectDetail subjectDetail) {
		getSubjectDetails().remove(subjectDetail);
		subjectDetail.setSubjectGroup(null);

		return subjectDetail;
	}

	public List<SubjectGroupDetail> getSubjectGroupDetails() {
		return this.subjectGroupDetails;
	}

	public void setSubjectGroupDetails(List<SubjectGroupDetail> subjectGroupDetails) {
		this.subjectGroupDetails = subjectGroupDetails;
	}

	public SubjectGroupDetail addSubjectGroupDetail(SubjectGroupDetail subjectGroupDetail) {
		getSubjectGroupDetails().add(subjectGroupDetail);
		subjectGroupDetail.setSubjectGroup(this);

		return subjectGroupDetail;
	}

	public SubjectGroupDetail removeSubjectGroupDetail(SubjectGroupDetail subjectGroupDetail) {
		getSubjectGroupDetails().remove(subjectGroupDetail);
		subjectGroupDetail.setSubjectGroup(null);

		return subjectGroupDetail;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SubjectGroup)) return false;

		SubjectGroup that = (SubjectGroup) o;

		return grpId.equals(that.grpId);

	}

	@Override
	public int hashCode() {
		return grpId.hashCode();
	}
}