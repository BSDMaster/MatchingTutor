package com.bsd.tutor.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


/**
 * New Class
 * 
 */
@Entity
public class RecommendTutorOption implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer optionId;

	private List<RecommendTutor> recommendTutors;

	private RecommendTutor nextRecommendTutor;

	private Integer sumTutorSpec;

	private  Double sumTravelTime;

	private Integer recommendSize;

	private Integer noProblemSize;

	private  Integer sumClassId;

	public RecommendTutorOption() {
	}

	public RecommendTutorOption( List<RecommendTutor> recommendTutors, RecommendTutor nextRecommendTutor) {
		this.recommendTutors = recommendTutors;
		this.nextRecommendTutor = nextRecommendTutor;
		this.recommendSize = (nextRecommendTutor != null) ? recommendTutors.size() + 1 : recommendTutors.size() ;
	}


	public Integer getOptionId() {
		return optionId;
	}

	public void setOptionId(Integer optionId) {
		this.optionId = optionId;
	}

	public List<RecommendTutor> getRecommendTutors() {
		return recommendTutors;
	}

	public void setRecommendTutors(List<RecommendTutor> recommendTutors) {
		this.recommendTutors = recommendTutors;
	}

	public Integer getSumTutorSpec() {
		return sumTutorSpec;
	}

	public void setSumTutorSpec(Integer sumTutorSpec) {
		this.sumTutorSpec = sumTutorSpec;
	}

	public Double getSumTravelTime() {
		return sumTravelTime;
	}

	public void setSumTravelTime(Double sumTravelTime) {
		this.sumTravelTime = sumTravelTime;
	}

	public RecommendTutor getNextRecommendTutor() {
		return nextRecommendTutor;
	}

	public void setNextRecommendTutor(RecommendTutor nextRecommendTutor) {
		this.nextRecommendTutor = nextRecommendTutor;
	}

	public Integer getRecommendSize() {
		return recommendSize;
	}

	public void setRecommendSize(Integer recommendSize) {
		this.recommendSize = recommendSize;
	}

	public Integer getNoProblemSize() {
		return noProblemSize;
	}

	public void setNoProblemSize(Integer noProblemSize) {
		this.noProblemSize = noProblemSize;
	}

	public Integer getSumClassId() {
		return sumClassId;
	}

	public void setSumClassId(Integer sumClassId) {
		this.sumClassId = sumClassId;
	}
}