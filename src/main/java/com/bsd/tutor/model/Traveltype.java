package com.bsd.tutor.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TRAVELTYPE database table.
 * 
 */
@Entity
@NamedQuery(name="Traveltype.findAll", query="SELECT t FROM Traveltype t")
public class Traveltype implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TRA_ID")
	private Long traId;

	@Column(name="TRA_NAME")
	private String traName;

	@Column(name="TRA_TYPE")
	private String traType;

	public Traveltype() {
	}

	public Long getTraId() {
		return this.traId;
	}

	public void setTraId(Long traId) {
		this.traId = traId;
	}

	public String getTraName() {
		return this.traName;
	}

	public void setTraName(String traName) {
		this.traName = traName;
	}

	public String getTraType() {
		return this.traType;
	}

	public void setTraType(String traType) {
		this.traType = traType;
	}

}