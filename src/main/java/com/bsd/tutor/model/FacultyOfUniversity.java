package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the FACULTY_OF_UNIVERSITY database table.
 * 
 */
@Entity
@Table(name="FACULTY_OF_UNIVERSITY")
@NamedQuery(name="FacultyOfUniversity.findAll", query="SELECT f FROM FacultyOfUniversity f")
public class FacultyOfUniversity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="FUN_ID")
	private Long funId;

	//bi-directional many-to-one association to Faculty
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="FUN_FAC_ID")
	private Faculty faculty;

	//bi-directional many-to-one association to University
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="FUN_UNI_ID")
	private University university;
/*
	//bi-directional many-to-one association to Tutor
	@OneToMany(mappedBy="facultyOfUniversity")
	private List<Tutor> tutors;
*/
	public FacultyOfUniversity() {
	}

	public Long getFunId() {
		return this.funId;
	}

	public void setFunId(Long funId) {
		this.funId = funId;
	}

	public Faculty getFaculty() {
		return this.faculty;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

	public University getUniversity() {
		return this.university;
	}

	public void setUniversity(University university) {
		this.university = university;
	}
/*
	public List<Tutor> getTutors() {
		return this.tutors;
	}

	public void setTutors(List<Tutor> tutors) {
		this.tutors = tutors;
	}

	public Tutor addTutor(Tutor tutor) {
		getTutors().add(tutor);
		tutor.setFacultyOfUniversity(this);

		return tutor;
	}

	public Tutor removeTutor(Tutor tutor) {
		getTutors().remove(tutor);
		tutor.setFacultyOfUniversity(null);

		return tutor;
	}
*/
}