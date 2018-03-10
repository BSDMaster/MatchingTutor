package com.bsd.tutor.model;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the TUTOR_MATCHING database table.
 * 
 */
@Entity
@Table(name="TUTOR_MATCHING")
@NamedQuery(name="TutorMatching.findAll", query="SELECT t FROM TutorMatching t")
public class TutorMatching implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="MAT_ID")
    private Integer matId;

	@Column(name="MAT_TUTOR_SEX_FLAG")
	private String matTutorSexFlag;

    @Column(name="MAT_TUTOR_FACULTY_FLAG")
    private String matTutorFacultyFlag;

    @Column(name="MAT_TUTOR_UNIVERSITY_FLAG")
    private String matTutorUniversityFlag;

    @Column(name="MAT_TUTOR_EXP_FLAG")
    private String matTutorExpFlag;

    @Column(name="MAT_ORDER")
    private Integer matOrder;

	public TutorMatching() {
	}

    public Integer getMatId() {
        return matId;
    }

    public void setMatId(Integer matId) {
        this.matId = matId;
    }


    public String getMatTutorSexFlag() {
        return matTutorSexFlag;
    }

    public void setMatTutorSexFlag(String matTutorSexFlag) {
        this.matTutorSexFlag = matTutorSexFlag;
    }

    public String getMatTutorFacultyFlag() {
        return matTutorFacultyFlag;
    }

    public void setMatTutorFacultyFlag(String matTutorFacultyFlag) {
        this.matTutorFacultyFlag = matTutorFacultyFlag;
    }

    public String getMatTutorUniversityFlag() {
        return matTutorUniversityFlag;
    }

    public void setMatTutorUniversityFlag(String matTutorUniversityFlag) {
        this.matTutorUniversityFlag = matTutorUniversityFlag;
    }

    public String getMatTutorExpFlag() {
        return matTutorExpFlag;
    }

    public void setMatTutorExpFlag(String matTutorExpFlag) {
        this.matTutorExpFlag = matTutorExpFlag;
    }

    public Integer getMatOrder() {
        return matOrder;
    }

    public void setMatOrder(Integer matOrder) {
        this.matOrder = matOrder;
    }

}