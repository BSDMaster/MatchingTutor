package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the CLASS_STATUS database table.
 * 
 */
@Entity
@Table(name="CLASS_STATUS")
@NamedQuery(name="ClassStatus.findAll", query="SELECT c FROM ClassStatus c")
public class ClassStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="COS_STATUS_ID")
	private Long cosStatusId;

	@Column(name="COS_STATUS_NAME")
	private String cosStatusName;

	//bi-directional many-to-one association to Class
	@OneToMany(mappedBy="classStatus")
	private List<Class> clazzs;

	public ClassStatus() {
	}

	public Long getCosStatusId() {
		return this.cosStatusId;
	}

	public void setCosStatusId(Long cosStatusId) {
		this.cosStatusId = cosStatusId;
	}

	public String getCosStatusName() {
		return this.cosStatusName;
	}

	public void setCosStatusName(String cosStatusName) {
		this.cosStatusName = cosStatusName;
	}

	public List<Class> getClazzs() {
		return this.clazzs;
	}

	public void setClazzs(List<Class> clazzs) {
		this.clazzs = clazzs;
	}

	public Class addClazz(Class clazz) {
		getClazzs().add(clazz);
		clazz.setClassStatus(this);

		return clazz;
	}

	public Class removeClazz(Class clazz) {
		getClazzs().remove(clazz);
		clazz.setClassStatus(null);

		return clazz;
	}

}