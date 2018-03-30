package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the CLASS_TUTOR_UNIVERSITY database table.
 * 
 */
@Entity
@Table(name="CLASS_TUTOR_UNIVERSITY")
@NamedQuery(name="ClassTutorUniversity.findAll", query="SELECT c FROM ClassTutorUniversity c")
public class ClassTutorUniversity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TUU_ID")
	private Long tuuId;

	//bi-directional many-to-one association to Class
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TUU_CLS_ID")
	private Class clazz;

	//bi-directional many-to-one association to Class
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TUU_MER_ID")
	private MergedClass mergedClass;


	//bi-directional many-to-one association to University
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="TUU_UNI_ID")
	private University university;

	public ClassTutorUniversity() {
	}

	public Long getTuuId() {
		return tuuId;
	}

	public void setTuuId(Long tuuId) {
		this.tuuId = tuuId;
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

	public University getUniversity() {
		return university;
	}

	public void setUniversity(University university) {
		this.university = university;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClassTutorUniversity)) return false;

		ClassTutorUniversity that = (ClassTutorUniversity) o;

		return university.equals(that.university);

	}

	@Override
	public int hashCode() {
		return university.hashCode();
	}
}