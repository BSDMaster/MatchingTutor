package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the PROGRAM database table.
 * 
 */
@Entity
@NamedQuery(name="Program.findAll", query="SELECT p FROM Program p")
public class Program implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PRG_ID")
	private Integer prgId;

	@Column(name="PRG_DESC")
	private String prgDesc;

	//bi-directional many-to-one association to TutorProgram
	@OneToMany(mappedBy="program")
	private List<TutorProgram> tutorPrograms;

	public Program() {
	}

	public Integer getPrgId() {
		return this.prgId;
	}

	public void setPrgId(Integer prgId) {
		this.prgId = prgId;
	}

	public String getPrgDesc() {
		return this.prgDesc;
	}

	public void setPrgDesc(String prgDesc) {
		this.prgDesc = prgDesc;
	}

	public List<TutorProgram> getTutorPrograms() {
		return this.tutorPrograms;
	}

	public void setTutorPrograms(List<TutorProgram> tutorPrograms) {
		this.tutorPrograms = tutorPrograms;
	}

	public TutorProgram addTutorProgram(TutorProgram tutorProgram) {
		getTutorPrograms().add(tutorProgram);

		return tutorProgram;
	}

	public TutorProgram removeTutorProgram(TutorProgram tutorProgram) {
		getTutorPrograms().remove(tutorProgram);

		return tutorProgram;
	}

}