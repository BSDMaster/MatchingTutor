package com.bsd.tutor.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the CLASS database table.
 * 
 */
public class MixClass implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<RecommendTutor> recTutorsNormal = new ArrayList<>();
	private List<RecommendTutor> recTutorsMerged = new ArrayList<>();

	public MixClass(List<RecommendTutor> recTutorsNormal, List<RecommendTutor> recTutorsMerged) {
		this.recTutorsNormal = recTutorsNormal;
		this.recTutorsMerged = recTutorsMerged;
	}

	public List<RecommendTutor> getRecTutorsNormal() {
		return recTutorsNormal;
	}

	public void setRecTutorsNormal(List<RecommendTutor> recTutorsNormal) {
		this.recTutorsNormal = recTutorsNormal;
	}

	public List<RecommendTutor> getRecTutorsMerged() {
		return recTutorsMerged;
	}

	public void setRecTutorsMerged(List<RecommendTutor> recTutorsMerged) {
		this.recTutorsMerged = recTutorsMerged;
	}


}