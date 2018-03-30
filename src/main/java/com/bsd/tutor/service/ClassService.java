package com.bsd.tutor.service;

import com.bsd.tutor.dao.ClassDao;
import com.bsd.tutor.model.*;
import com.bsd.tutor.model.Class;
import com.bsd.tutor.utils.Constants;
import com.bsd.tutor.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kewalins on 2/26/2018 AD.
 */
@Service
public class ClassService {

    public RecommendTutor tutorSpecification(Class cls, RecommendTutor recTutor) {
        // Tutor’s sex
        if(cls.getClsTutorSex() == null) {
            recTutor.setRecTutorSexFlag(Constants.FLAG_YES);
        }else if ((cls.getClsTutorSex() != null && cls.getClsTutorSex().equals(recTutor.getTutor().getTurSex()))) {
            recTutor.setRecTutorSexFlag(Constants.FLAG_YES);
        } else {
            recTutor.setRecTutorSexFlag(Constants.FLAG_NO);
        }

        // Tutor’s faculty and university
        List resultFac = cls.getClassTutorFaculties().stream().filter(it -> it.getFaculty() == null || it.getFaculty().getFacId() == recTutor.getTutor().getFacultyOfUniversity().getFaculty().getFacId()).collect(Collectors.toList());
        if (resultFac.size() == 0) {
            recTutor.setRecTutorFacultyFlag(Constants.FLAG_YES);
        } else {
            recTutor.setRecTutorFacultyFlag(Constants.FLAG_NO);
        }
        List resultUni = cls.getClassTutorUniversities().stream().filter(it -> it.getUniversity() == null || it.getUniversity().getUniId() == recTutor.getTutor().getFacultyOfUniversity().getUniversity().getUniId()).collect(Collectors.toList());
        if (resultUni.size() == 0) {
            recTutor.setRecTutorUniversityFlag(Constants.FLAG_YES);
        } else {
            recTutor.setRecTutorUniversityFlag(Constants.FLAG_NO);
        }
		/*
		for (ClassTutorStudy classTutorStudy : cls.getClassTutorFaculties()) {
			if ((classTutorStudy.getFaculty() != null && classTutorStudy.getFaculty().getFacId() == recTutor.getTutor().getFacultyOfUniversity().getFaculty().getFacId())
					|| classTutorStudy.getFaculty() == null) {
				recTutor.setRecTutorFacultyFlag(Constants.FLAG_YES);

			}
			if ((classTutorStudy.getUniversity() != null && classTutorStudy.getUniversity().getUniId() == recTutor.getTutor().getFacultyOfUniversity().getUniversity().getUniId())
					|| classTutorStudy.getUniversity() == null) {
				recTutor.setRecTutorUniversityFlag(Constants.FLAG_YES);

			}
		}*/
        if(recTutor.getRecTutorFacultyFlag() == null) {
            recTutor.setRecTutorFacultyFlag(Constants.FLAG_NO);
        }


        if(recTutor.getRecTutorUniversityFlag() == null) {
            recTutor.setRecTutorUniversityFlag(Constants.FLAG_NO);
        }

        // Tutor’s year experience
        recTutor.setRecTutorYearExp(0L);
        if (cls.getClsTutorExp() == null) {
            recTutor.setRecTutorExpFlag(Constants.FLAG_YES);
        }else {
            for (TutorSubject tutorSubject : recTutor.getTutor().getTutorSubject()) {
                if (tutorSubject.getSubjectDetail().getSubject().getSubjId() == cls.getSubject().getSubjId()
                        && tutorSubject.getSubjectDetail().getSubjectGroup().getGrpId() == cls.getSubjectGroupDetail().getSubjectGroup().getGrpId()) {
                    if  (tutorSubject.getTsbjExp() > 0){
                        recTutor.setRecTutorExpFlag(Constants.FLAG_YES);
                        recTutor.setRecTutorYearExp(tutorSubject.getTsbjExp());
                    }
                }
            }
        }
        if(recTutor.getRecTutorExpFlag() == null) {
            recTutor.setRecTutorExpFlag(Constants.FLAG_NO);
        }

        return recTutor;
    }

    public static RecommendTutor filterTutorsByAvailableDay(Long days, Double classDuration, List<StudentAvailableTimelocation> sAvas, Tutor tutor) {
        GoogleAPIMapServices googleService = new GoogleAPIMapServices();

        //	System.out.println("Tutor ID : "+tutor.getTurId());
        GoogleAPIMapServices googleMapService = new GoogleAPIMapServices();
        List<RecommendTimelocation> recTimeLoc = new ArrayList<>();
        RecommendTutor recTutor = new RecommendTutor();

        recTutor.setTutor(tutor);
        recTutor.setNumOfDays(days);
        recTutor.setRecommendTimelocations(recTimeLoc);
        Set<Long> daySet = new HashSet<>();

        for (StudentAvailableTimelocation sAva : sAvas) {

            TutorAvailableTimeloacation daysMatch = tutor.getTutorAvailableTimeloacations()
                    .stream().filter(it -> sAva.getSavDayId() == it.getTavDayId())
                    .findAny()
                    .orElse(null);
            if (daysMatch != null) {
                System.out.println("Tutor ID : " + tutor.getTurId() + " || Day : " + daysMatch.getTavDayId());
                System.out.println("From - To : " + daysMatch.getTavStartLocation() + " - " + sAva.getSavLocation() );
                System.out.println("Student available : " + sAva.getSavStartTime() + " - " + sAva.getSavEndTime());
                System.out.println("Tutor available : " + daysMatch.getTavStartTime() + " - " + daysMatch.getTavEndTime());
                Double overlapHours = DateTimeUtils.calculateOverlapTime(sAva.getSavStartTime(), daysMatch.getTavStartTime(), sAva.getSavEndTime(), daysMatch.getTavEndTime());
                System.out.println("Overlap time : " + overlapHours);
                if (overlapHours >= classDuration) {
                    Double duration1 = DateTimeUtils.convertSecondsToHours(googleService.calculateTravelTime(daysMatch.getTavStartLat(), daysMatch.getTavStartLong(), sAva.getSavLat(), sAva.getSavLong(),
                            daysMatch.getTavTraDrive(), daysMatch.getTavTraTaxi(), daysMatch.getTavTraTrain(), daysMatch.getTavTraBus(), googleMapService));
                    Double duration2 = DateTimeUtils.convertSecondsToHours(googleService.calculateTravelTime(sAva.getSavLat(), sAva.getSavLong(), daysMatch.getTavEndLat(), daysMatch.getTavEndLong(),
                            daysMatch.getTavTraDrive(), daysMatch.getTavTraTaxi(), daysMatch.getTavTraTrain(), daysMatch.getTavTraBus(), googleMapService));
                    Double startTime = DateTimeUtils.getStartTime(sAva.getSavStartTime(), daysMatch.getTavStartTime());
                    Double endTime = DateTimeUtils.getEndTime(sAva.getSavEndTime(), daysMatch.getTavEndTime());

                    // Tutor start time
                    if (!DateTimeUtils.doubleToDate(duration1 + duration2).after(DateTimeUtils.doubleToDate(Constants.MAX_TRAVELTIME))) {
                        if (daysMatch.getTavStartTime().equals(startTime)) {
                            Date expectDuration = DateTimeUtils.addTime(duration1, classDuration);
                            System.out.println("Expect overlap duration : " + DateTimeUtils.hourFormat(expectDuration));
                            Date expectStartClassTime = DateTimeUtils.addTime(daysMatch.getTavStartTime(), duration1);
                            System.out.println("Expect class start time : " + DateTimeUtils.hourFormat(expectStartClassTime));
                            Date expectFinishClassTime = DateTimeUtils.addTime(daysMatch.getTavStartTime(), duration1, classDuration);
                            System.out.println("Expect class finish time : " + DateTimeUtils.hourFormat(expectFinishClassTime));

                            Date expectBackTime = DateTimeUtils.addTime(daysMatch.getTavStartTime(), duration1, classDuration, duration2);
                            System.out.println("Expect time back to tutor location : " + DateTimeUtils.hourFormat(expectBackTime));
                            if (!expectDuration.after(DateTimeUtils.doubleToDate(overlapHours)) && !expectBackTime.after(DateTimeUtils.doubleToDate(daysMatch.getTavEndTime()))) {
                                System.out.println("* 1 Able to teach on the day");
                                RecommendTimelocation recTime = new RecommendTimelocation();
                                recTime.setRecdClassStart(Constants.TIMELOCTYPE_NORMAL);
                                recTime.setRecdTraveltime1(duration1);
                                recTime.setRecdTraveltime2(duration2);
                                recTime.setRecdTraveltime(duration1 + duration2);
                                recTime.setRecdDayId(sAva.getSavDayId());
                                recTime.setRecdStart(Double.parseDouble(DateTimeUtils.hourFormat(expectStartClassTime)));
                                recTime.setRecdEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectFinishClassTime)));
                                recTime.setRecdTravelStart(daysMatch.getTavStartTime());
                                recTime.setRecdTravelEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectBackTime)));
                                recTime.setRecdAvaStart(Double.parseDouble(DateTimeUtils.hourFormat(expectStartClassTime)));
                                recTime.setRecdAvaEnd(endTime);
                                recTime.setRecdLoc(sAva.getSavLocation());
                                recTime.setRecdLat(sAva.getSavLat());
                                recTime.setRecdLong(sAva.getSavLong());
                                recTime.setDuration(classDuration);
                                recTutor.addRecommendTimelocation(recTime);
                                daySet.add(sAva.getSavDayId());
                            }
                            // Student start time
                        } else {
                            Date expectStartTravelTime = DateTimeUtils.subtractTime(startTime, duration1);
                            System.out.println("Expect time start from tutor location : " + DateTimeUtils.hourFormat(expectStartTravelTime));
                            Date expectStartClassTime = DateTimeUtils.addTime(sAva.getSavStartTime(), 0D);
                            System.out.println("Expect class start time : " + DateTimeUtils.hourFormat(expectStartClassTime));
                            Date expectFinishClassTime = DateTimeUtils.addTime(sAva.getSavStartTime(), classDuration);
                            System.out.println("Expect class finish time : " + DateTimeUtils.hourFormat(expectFinishClassTime));

                            Date expectBackTime = DateTimeUtils.addTime(startTime, classDuration, duration2);
                            System.out.println("Expect time back to tutor location : " + DateTimeUtils.hourFormat(expectBackTime));
                            if (!expectStartTravelTime.before(DateTimeUtils.doubleToDate(daysMatch.getTavStartTime())) && !expectBackTime.after(DateTimeUtils.doubleToDate(daysMatch.getTavEndTime()))) {
                                System.out.println("* 2 Able to teach on the day");
                                RecommendTimelocation recTime = new RecommendTimelocation();
                                recTime.setRecdClassStart(Constants.TIMELOCTYPE_NORMAL);
                                recTime.setRecdTraveltime1(duration1);
                                recTime.setRecdTraveltime2(duration2);
                                recTime.setRecdTraveltime(duration1 + duration2);
                                recTime.setRecdDayId(sAva.getSavDayId());
                                recTime.setRecdStart(sAva.getSavStartTime());
                                recTime.setRecdEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectFinishClassTime)));
                                recTime.setRecdTravelStart(Double.parseDouble(DateTimeUtils.hourFormat(expectStartTravelTime)));
                                recTime.setRecdTravelEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectBackTime)));
                                recTime.setRecdAvaStart(sAva.getSavStartTime());
                                recTime.setRecdAvaEnd(endTime);
                                recTime.setRecdLoc(sAva.getSavLocation());
                                recTime.setRecdLat(sAva.getSavLat());
                                recTime.setRecdLong(sAva.getSavLong());
                                recTime.setDuration(classDuration);
                                recTutor.addRecommendTimelocation(recTime);
                                daySet.add(sAva.getSavDayId());
                            }

                        }
                    }
                }
                System.out.println("--------------------------------");
            }
        }
        if (daySet.size() >= days) {
            recTutor.setAvailableDays(Double.valueOf(daySet.size()));
            recTutor.setAvailableRatio(Double.valueOf(daySet.size())/days);
            recTutor.getRecommendTimelocations().sort(Comparator.comparing(RecommendTimelocation::getRecdTraveltime)
                    .reversed());
            recTutor.setMinTravelTime(recTutor.getRecommendTimelocations().get(0).getRecdTraveltime());
            return recTutor;
        } else {
            return null;
        }
    }
}