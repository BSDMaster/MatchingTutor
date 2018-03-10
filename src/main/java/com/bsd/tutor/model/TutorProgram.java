package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TUTOR_PROGRAM database table.
 * 
 */
@Entity
@Table(name="TUTOR_PROGRAM")
@NamedQuery(name="TutorProgram.findAll", query="SELECT t FROM TutorProgram t")
public class TutorProgram implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TTP_ID")
	private Integer ttpId;


    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="TTP_TUR_ID")
    private Tutor tutor;

    @Column(name="TTP_PRG_ID")
    private Integer ttpPrgId;

	public TutorProgram() {
	}

	public Integer getTtpId() {
		return this.ttpId;
	}

	public void setTtpId(Integer ttpId) {
		this.ttpId = ttpId;
	}

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Integer getTtpPrgId() {
        return ttpPrgId;
    }

    public void setTtpPrgId(Integer ttpPrgId) {
        this.ttpPrgId = ttpPrgId;
    }


}