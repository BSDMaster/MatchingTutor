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

	private Long optionId;

	private List<RecommendTutor> recommendTutors;

	private RecommendTutor nextRecommendTutor;

	private int sumTutorSpec;

	private  Double sumTravelTime;

	private int recommendSize;

	private int noProblemSize;

	private  Long sumClassId;

	private Long diffRecTutorOrder;

	private Double averageDistance;

	public RecommendTutorOption() {
	}

	public RecommendTutorOption( List<RecommendTutor> recommendTutors, RecommendTutor nextRecommendTutor) {
		this.recommendTutors = recommendTutors;
		this.nextRecommendTutor = nextRecommendTutor;
		this.recommendSize = (nextRecommendTutor != null) ? recommendTutors.size() + 1 : recommendTutors.size() ;
	}


	public Long getOptionId() {
		return optionId;
	}

	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}

	public List<RecommendTutor> getRecommendTutors() {
		return recommendTutors;
	}

	public void setRecommendTutors(List<RecommendTutor> recommendTutors) {
		this.recommendTutors = recommendTutors;
	}

	public int getSumTutorSpec() {
		return sumTutorSpec;
	}

	public void setSumTutorSpec(int sumTutorSpec) {
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

	public int getRecommendSize() {
		return recommendSize;
	}

	public void setRecommendSize(int recommendSize) {
		this.recommendSize = recommendSize;
	}

	public int getNoProblemSize() {
		return noProblemSize;
	}

	public void setNoProblemSize(int noProblemSize) {
		this.noProblemSize = noProblemSize;
	}

	public Long getSumClassId() {
		return sumClassId;
	}

	public Long getDiffRecTutorOrder() {
		return diffRecTutorOrder;
	}

	public void setDiffRecTutorOrder(Long diffRecTutorOrder) {
		this.diffRecTutorOrder = diffRecTutorOrder;
	}

	public void setSumClassId(Long sumClassId) {
		this.sumClassId = sumClassId;
	}

	public Double getAverageDistance() {
		return averageDistance;
	}

	public void setAverageDistance(Double averageDistance) {
		this.averageDistance = averageDistance;
	}
}