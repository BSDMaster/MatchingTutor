package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the UNIVERSITY database table.
 * 
 */
@Entity
@NamedQuery(name="University.findAll", query="SELECT u FROM University u")
public class University implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="UNI_ID")
	private Long uniId;

	@Column(name="UNI_NAME")
	private String uniName;

	//bi-directional many-to-one association to ClassTutorUniversities
	@OneToMany(mappedBy="university")
	private List<ClassTutorUniversity> classTutorUniversities;

	//bi-directional many-to-one association to FacultyOfUniversity
	@OneToMany(mappedBy="university")
	private List<FacultyOfUniversity> facultyOfUniversities;

	public University() {
	}

	public Long getUniId() {
		return this.uniId;
	}

	public void setUniId(Long uniId) {
		this.uniId = uniId;
	}

	public String getUniName() {
		return this.uniName;
	}

	public void setUniName(String uniName) {
		this.uniName = uniName;
	}

	public List<ClassTutorUniversity> getClassTutorUniversities() {
		return this.classTutorUniversities;
	}

	public void setClassTutorStudies(List<ClassTutorUniversity> classTutorUniversities) {
		this.classTutorUniversities = classTutorUniversities;
	}

	public ClassTutorUniversity addClassTutorStudy(ClassTutorUniversity classTutorUniversities) {
		getClassTutorUniversities().add(classTutorUniversities);
		classTutorUniversities.setUniversity(this);

		return classTutorUniversities;
	}

	public ClassTutorUniversity removeClassTutorStudy(ClassTutorUniversity classTutorUniversities) {
		getClassTutorUniversities().remove(classTutorUniversities);
		classTutorUniversities.setUniversity(null);

		return classTutorUniversities;
	}

	public List<FacultyOfUniversity> getFacultyOfUniversities() {
		return this.facultyOfUniversities;
	}

	public void setFacultyOfUniversities(List<FacultyOfUniversity> facultyOfUniversities) {
		this.facultyOfUniversities = facultyOfUniversities;
	}

	public FacultyOfUniversity addFacultyOfUniversity(FacultyOfUniversity facultyOfUniversity) {
		getFacultyOfUniversities().add(facultyOfUniversity);
		facultyOfUniversity.setUniversity(this);

		return facultyOfUniversity;
	}

	public FacultyOfUniversity removeFacultyOfUniversity(FacultyOfUniversity facultyOfUniversity) {
		getFacultyOfUniversities().remove(facultyOfUniversity);
		facultyOfUniversity.setUniversity(null);

		return facultyOfUniversity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof University)) return false;

		University that = (University) o;

		return uniId.equals(that.uniId);

	}

	@Override
	public int hashCode() {
		return uniId.hashCode();
	}
}