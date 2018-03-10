package com.bsd.tutor.model;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the CLASS_TUTOR_FACULTY database table.
 * 
 */
@Entity
@Table(name="CLASS_TUTOR_FACULTY")
@NamedQuery(name="ClassTutorFaculty.findAll", query="SELECT c FROM ClassTutorFaculty c")
public class ClassTutorFaculty implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TUF_ID")
	private Integer tufId;

	//bi-directional many-to-one association to Class
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TUF_CLS_ID")
	private Class clazz;

	//bi-directional many-to-one association to Class
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TUF_MER_ID")
	private MergedClass mergedClass;

	//bi-directional many-to-one association to Faculty
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TUF_FAC_ID")
	private Faculty faculty;


	public ClassTutorFaculty() {
	}

	public Integer getTufId() {
		return tufId;
	}

	public void setTufId(Integer tufId) {
		this.tufId = tufId;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public MergedClass getMergedClass() {
		return mergedClass;
	}

	public void setMergedClass(MergedClass mergedClass) {
		this.mergedClass = mergedClass;
	}

	public Faculty getFaculty() {
		return faculty;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClassTutorFaculty)) return false;

		ClassTutorFaculty that = (ClassTutorFaculty) o;

		return faculty != null ? faculty.equals(that.faculty) : that.faculty == null;

	}

	@Override
	public int hashCode() {
		return faculty != null ? faculty.hashCode() : 0;
	}
}