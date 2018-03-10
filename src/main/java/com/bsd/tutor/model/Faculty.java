package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the FACULTY database table.
 * 
 */
@Entity
@NamedQuery(name="Faculty.findAll", query="SELECT f FROM Faculty f")
public class Faculty implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="FAC_ID")
	private Integer facId;

	@Column(name="FAC_NAME")
	private String facName;

	//bi-directional many-to-one association to ClassTutorFaculties
	@OneToMany(mappedBy="faculty")
	private List<ClassTutorFaculty> classTutorFaculties;

	//bi-directional many-to-one association to FacultyOfUniversity
	@OneToMany(mappedBy="faculty")
	private List<FacultyOfUniversity> facultyOfUniversities;

	public Faculty() {
	}

	public Integer getFacId() {
		return this.facId;
	}

	public void setFacId(Integer facId) {
		this.facId = facId;
	}

	public String getFacName() {
		return this.facName;
	}

	public void setFacName(String facName) {
		this.facName = facName;
	}

	public List<ClassTutorFaculty> getClassTutorFaculties() {
		return this.classTutorFaculties;
	}

	public void setClassTutorFaculties(List<ClassTutorFaculty> classTutorFaculties) {
		this.classTutorFaculties = classTutorFaculties;
	}

	public ClassTutorFaculty addClassTutorFaculties(ClassTutorFaculty classTutorFaculties) {
		getClassTutorFaculties().add(classTutorFaculties);
		classTutorFaculties.setFaculty(this);

		return classTutorFaculties;
	}

	public ClassTutorFaculty removeClassTutorFaculties(ClassTutorFaculty classTutorFaculties) {
		getClassTutorFaculties().remove(classTutorFaculties);
		classTutorFaculties.setFaculty(null);

		return classTutorFaculties;
	}

	public List<FacultyOfUniversity> getFacultyOfUniversities() {
		return this.facultyOfUniversities;
	}

	public void setFacultyOfUniversities(List<FacultyOfUniversity> facultyOfUniversities) {
		this.facultyOfUniversities = facultyOfUniversities;
	}

	public FacultyOfUniversity addFacultyOfUniversity(FacultyOfUniversity facultyOfUniversity) {
		getFacultyOfUniversities().add(facultyOfUniversity);
		facultyOfUniversity.setFaculty(this);

		return facultyOfUniversity;
	}

	public FacultyOfUniversity removeFacultyOfUniversity(FacultyOfUniversity facultyOfUniversity) {
		getFacultyOfUniversities().remove(facultyOfUniversity);
		facultyOfUniversity.setFaculty(null);

		return facultyOfUniversity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Faculty)) return false;

		Faculty faculty = (Faculty) o;

		return facId.equals(faculty.facId);

	}

	@Override
	public int hashCode() {
		return facId.hashCode();
	}
}