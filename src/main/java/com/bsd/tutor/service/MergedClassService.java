package com.bsd.tutor.service;

import com.bsd.tutor.model.*;
import com.bsd.tutor.model.Class;
import com.bsd.tutor.utils.Constants;
import com.bsd.tutor.utils.DateTimeUtils;
import com.bsd.tutor.utils.GoogleMapUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kewalins on 3/10/2018 AD.
 */
@Service
public class MergedClassService {

    public  List<MergedClass> getAllMergedClass(List<Class> classes){
        List<MergedClass> allMergedClasses = new ArrayList<>();
        for (int i = 0 ; i < classes.size() ; i++) {
            Class cls1 = classes.get(i);
            for (int j = i + 1 ; j < classes.size() ; j++) {
                Class cls2 = classes.get(j);
                //System.out.println("Class : "+cls1.getClsId() + " , " + cls2.getClsId());

                MergedClass tmpMergedClass = new MergedClass(cls1,cls2);
                tmpMergedClass.setClassTutorUniversities(new ArrayList<>());
                tmpMergedClass.setClassTutorFaculties(new ArrayList<>());
                // Tutor's sex
                // Add to list
                boolean canMerged = true;
                // Required both
                if (cls1.getClsTutorSex() != null && cls2.getClsTutorSex() != null ) {
                    if (cls1.getClsTutorSex().equals(cls2.getClsTutorSex())) {
                        tmpMergedClass.setMerTurSex(cls1.getClsTutorSex());
                    } else {
                        canMerged = false;
                    }
                    // Only class2 required
                } else if (cls1.getClsTutorSex() == null && cls2.getClsTutorSex() != null ) {
                    tmpMergedClass.setMerTurSex(cls2.getClsTutorSex());
                    // Only class1 required
                } else if (cls1.getClsTutorSex() != null && cls2.getClsTutorSex() == null) {
                    tmpMergedClass.setMerTurSex(cls1.getClsTutorSex());
                }
                //System.out.println("1. canMerged : "+canMerged);
                // Faculty

                if (!cls1.getClassTutorFaculties().isEmpty() && !cls2.getClassTutorFaculties().isEmpty()) {
                    List<ClassTutorFaculty> faculties1 = new ArrayList<>(cls1.getClassTutorFaculties());
                    List<ClassTutorFaculty> faculties2 = new ArrayList<>(cls2.getClassTutorFaculties());
                    faculties1.retainAll(cls2.getClassTutorFaculties());
                    faculties2.retainAll(cls1.getClassTutorFaculties());
                    if (!faculties1.isEmpty()) {
                        tmpMergedClass.setClassTutorFaculties(faculties1);
                    } else if (!faculties2.isEmpty()) {
                        tmpMergedClass.setClassTutorFaculties(faculties2);
                    } else{
                        canMerged = false;
                    }
                } else if (cls1.getClassTutorFaculties().isEmpty() && !cls2.getClassTutorFaculties().isEmpty()) {
                    tmpMergedClass.setClassTutorFaculties(cls2.getClassTutorFaculties());
                } else if (!cls1.getClassTutorFaculties().isEmpty() && cls2.getClassTutorFaculties().isEmpty()) {
                    tmpMergedClass.setClassTutorFaculties(cls1.getClassTutorFaculties());
                }
                // University
                if (!cls1.getClassTutorUniversities().isEmpty() && !cls2.getClassTutorUniversities().isEmpty()) {
                    List<ClassTutorUniversity> universities1 = new ArrayList<>(cls1.getClassTutorUniversities());
                    List<ClassTutorUniversity> universities2 = new ArrayList<>(cls2.getClassTutorUniversities());
                    universities1.retainAll(cls2.getClassTutorUniversities());
                    universities2.retainAll(cls1.getClassTutorUniversities());
                    if (!universities1.isEmpty()) {
                        tmpMergedClass.setClassTutorUniversities(universities1);
                    } else if (!universities2.isEmpty()) {
                        tmpMergedClass.setClassTutorUniversities(universities2);
                    } else{
                        canMerged = false;
                    }
                } else if (cls1.getClassTutorUniversities().isEmpty() && !cls2.getClassTutorUniversities().isEmpty()) {
                    tmpMergedClass.setClassTutorUniversities(cls2.getClassTutorUniversities());
                } else if (!cls1.getClassTutorUniversities().isEmpty() && cls2.getClassTutorUniversities().isEmpty()) {
                    tmpMergedClass.setClassTutorUniversities(cls1.getClassTutorUniversities());
                }
                if (canMerged) {
                    tmpMergedClass = assignedNewTimeLoc(tmpMergedClass);
                    if (tmpMergedClass != null) {
                        tmpMergedClass = assignedNewProperites(tmpMergedClass);
                        if (tmpMergedClass != null) {
                            allMergedClasses.add(tmpMergedClass);
                        }
                    }
                   // System.out.println("Class : "+tmpMergedClass.getClazz1().getClsId() + " , " + tmpMergedClass.getClazz2().getClsId());

                }
                //System.out.println("2. canMerged : "+canMerged);
            }
        }
        return allMergedClasses;
    }

    public MergedClass assignedNewTimeLoc(MergedClass tmpMergedClass){
        Class cls1 = tmpMergedClass.getClazz1();
        Class cls2 = tmpMergedClass.getClazz2();
        Set<Integer> numOfDays = new HashSet<>();
        List<MergedClassAvailabletimeLocation> mergedTimes = new ArrayList<>();
        // Check available days for class 1 only
        for(StudentAvailableTimelocation dayCls1 : cls1.getStudent().getStudentAvailableTimelocations()){
            for(StudentAvailableTimelocation dayCls2 : cls2.getStudent().getStudentAvailableTimelocations()){

                if (dayCls1.getSavDayId().equals(dayCls2.getSavDayId()) &&  GoogleMapUtils.distFrom(dayCls1.getSavLat(), dayCls1.getSavLong(), dayCls2.getSavLat(), dayCls2.getSavLong()) < Constants.DIFF_DISTANCE) {
                    Double[] time1 = DateTimeUtils.mergeClassByTime(dayCls1.getSavStartTime(), dayCls1.getSavEndTime(),dayCls2.getSavStartTime(), dayCls2.getSavEndTime(),cls1.getClsDuration(),cls2.getClsDuration());
                    Double[] time2 = DateTimeUtils.mergeClassByTime(dayCls2.getSavStartTime(), dayCls2.getSavEndTime(),dayCls1.getSavStartTime(), dayCls1.getSavEndTime(),cls2.getClsDuration(),cls1.getClsDuration());
                    if (time1[0] != null && time1[1] != null) {
                        MergedClassAvailabletimeLocation mergedTime = new MergedClassAvailabletimeLocation(tmpMergedClass, dayCls1.getSavDayId());
                        mergedTime.setMervStartTime(time1[0]);
                        mergedTime.setMeavEndTime(time1[1]);
                        mergedTime.setMervClsStart(1);
                        mergedTime.setMervLat(dayCls1.getSavLat());
                        mergedTime.setMervLong(dayCls1.getSavLong());
                        mergedTime.setMervLocation(dayCls1.getSavLocation());
                        mergedTime.setMeavDayId(dayCls1.getSavDayId());
                        mergedTimes.add(mergedTime);
                        numOfDays.add(dayCls1.getSavDayId());
                    }
                    else if (time2[0] != null && time2[1] != null) {
                        MergedClassAvailabletimeLocation mergedTime = new MergedClassAvailabletimeLocation(tmpMergedClass, dayCls1.getSavDayId());
                        mergedTime.setMervStartTime(time2[0]);
                        mergedTime.setMeavEndTime(time2[1]);
                        mergedTime.setMervClsStart(2);
                        mergedTime.setMervLat(dayCls2.getSavLat());
                        mergedTime.setMervLong(dayCls2.getSavLong());
                        mergedTime.setMervLocation(dayCls2.getSavLocation());
                        mergedTime.setMeavDayId(dayCls2.getSavDayId());
                        mergedTimes.add(mergedTime);
                        numOfDays.add(dayCls2.getSavDayId());
                    }
                }

            }
        }

        if (!numOfDays.isEmpty()) {
            System.out.println("-----------------------------------------");
            System.out.println("Merged Class : "+tmpMergedClass.toString()+ " / Number of match days : "+mergedTimes.size());
            System.out.println("-----------------------------------------");
            Integer maxDay = Math.max(cls1.getClsDayperweek(),cls2.getClsDayperweek());
            tmpMergedClass.setMacDays(maxDay);
            tmpMergedClass.setMerMergeratio(numOfDays.size()/maxDay);
            Integer minDays  = Math.min(cls1.getClsDayperweek(),cls2.getClsDayperweek());
            tmpMergedClass.setMerMindayperweek(minDays);
            tmpMergedClass.setAvaDays(numOfDays);
            tmpMergedClass.setNumberOfAvaDays(numOfDays.size());
            tmpMergedClass.setMergedClassAvailabletimes(mergedTimes);
            return tmpMergedClass;
/*
				if (numOfDays.size() >= maxDay) {
					mergedClass.setMerMergeratio(1D);
				} else {
					mergedClass.setMerMergeratio(mergedClass.getNumberOfAvaDays()/maxDay);
				}
*/				// Min days per week (Number of days that able to be merged such as Class1 has 2 days and Class2 has 1 days. Merged class should be 1 days and another one for Class1)


        }
        return null;

    }

    public MergedClass assignedNewProperites(MergedClass tmpMergedClass){
        Class cls1 = tmpMergedClass.getClazz1();
        Class cls2 = tmpMergedClass.getClazz2();
        // Check student
        if (cls1.getStudent().getStuId().equals(cls2.getStudent().getStuId())) {
            tmpMergedClass.setMerSamestuFlag(Constants.FLAG_YES);
        } else {
            tmpMergedClass.setMerSamestuFlag(Constants.FLAG_NO);
        }
        // Check parent
        if (cls1.getStudent().getParent() != null && cls2.getStudent().getParent() != null ) {
            if (cls1.getStudent().getParent().getParId().equals(cls2.getStudent().getParent().getParId())) {
                tmpMergedClass.setMerSameparentFlag(Constants.FLAG_YES);
            } else {
                tmpMergedClass.setMerSameparentFlag(Constants.FLAG_NO);
            }
        } else {
            tmpMergedClass.setMerSameparentFlag(Constants.FLAG_NO);
        }
        // Check subject group
        if (cls1.getSubjectGroupDetail().getSubjectGroup().equals(cls2.getSubjectGroupDetail().getSubjectGroup())
                && cls1.getSubject().equals(cls2.getSubject())) {
            tmpMergedClass.setMerSamesubgFlag(Constants.FLAG_YES);
        } else {
            tmpMergedClass.setMerSamesubgFlag(Constants.FLAG_NO);
        }
        // Check subject
        if (cls1.getSubject().equals(cls2.getSubject())) {
            tmpMergedClass.setMerSamesubFlag(Constants.FLAG_YES);
        } else {
            tmpMergedClass.setMerSamesubFlag(Constants.FLAG_NO);
        }
        // Tutor's Program
        tmpMergedClass.setMerTurPrgId(Math.max(cls1.getClsPrgId(), cls2.getClsPrgId()));
        // Tutor's Exp
        if (cls1.getClsTutorExp().equals(Constants.FLAG_YES) || cls2.getClsTutorExp().equals(Constants.FLAG_YES)) {
            tmpMergedClass.setMerTurExp(Constants.FLAG_YES);
        }else {
            tmpMergedClass.setMerTurExp(Constants.FLAG_NO);
        }
        System.out.println("Tutor Spec");
        if (tmpMergedClass.getMerTurSex() != null) {
            System.out.println("- Sex : " + tmpMergedClass.getMerTurSex());
        }
        for (ClassTutorFaculty faculty : tmpMergedClass.getClassTutorFaculties()) {
            System.out.println("- Faculty : "+faculty.getFaculty().getFacName());
        }
        for (ClassTutorUniversity university : tmpMergedClass.getClassTutorUniversities()) {
            System.out.println("- University : "+university.getUniversity().getUniName());
        }
        System.out.println("- Exp : "+tmpMergedClass.getMerTurExp());

        System.out.println("Merged Spec");
        System.out.println("- Subject1 : "+tmpMergedClass.getClazz1().getSubject().getSubjName()+ " "+tmpMergedClass.getClazz1().getSubjectGroupDetail().getGrpdName());
        System.out.println("- Subject2 : "+tmpMergedClass.getClazz2().getSubject().getSubjName()+ " "+tmpMergedClass.getClazz2().getSubjectGroupDetail().getGrpdName());
        System.out.println("- Same student : "+tmpMergedClass.getMerSamestuFlag());
        System.out.println("- Same parent : "+tmpMergedClass.getMerSameparentFlag());
        System.out.println("- Same subject group : "+tmpMergedClass.getMerSamesubgFlag());
        System.out.println("- Same subject : "+tmpMergedClass.getMerSamesubFlag());
        System.out.println("- Ratio days : "+tmpMergedClass.getMerMergeratio());
        System.out.println("- Min days : "+tmpMergedClass.getMerMindayperweek());
        System.out.println("- Available days : "+tmpMergedClass.getAvaDays());
//        System.out.println("- Max days : "+tmpMergedClass.getMacDays());
        System.out.println("Time Spec");
        for (MergedClassAvailabletimeLocation mergedTimeLoc : tmpMergedClass.getMergedClassAvailabletimes()) {
            System.out.println("- Class start : "+mergedTimeLoc.getMervClsStart()+ " / Day : "+mergedTimeLoc.getMeavDayId()+ " / Time : "+mergedTimeLoc.getMervStartTime() + " - "+mergedTimeLoc.getMeavEndTime());
        }
        return tmpMergedClass;
    }

    public RecommendTutor tutorSpecificationMerged(MergedClass mergedCls, RecommendTutor recTutor) {
        // Tutor’s sex
        if (mergedCls.getMerTurSex() == null) {
            recTutor.setRecTutorSexFlag(Constants.FLAG_YES);
        } else if ((mergedCls.getMerTurSex() != null && mergedCls.getMerTurSex().equals(recTutor.getTutor().getTurSex()))) {
            recTutor.setRecTutorSexFlag(Constants.FLAG_YES);
        } else {
            recTutor.setRecTutorSexFlag(Constants.FLAG_NO);
        }

        // Tutor’s faculty and university
        //List resultFac = mergedCls.getClassTutorFaculties().stream().filter(it -> it.getFaculty() == null).collect(Collectors.toList());
        if (mergedCls.getClassTutorFaculties() != null) {
            if (mergedCls.getClassTutorFaculties().isEmpty()) {
                recTutor.setRecTutorFacultyFlag(Constants.FLAG_YES);
            } else {
                List resultFac = mergedCls.getClassTutorFaculties().stream().filter(it -> it.getFaculty().equals(recTutor.getTutor().getFacultyOfUniversity().getFaculty())).collect(Collectors.toList());
                if (resultFac.isEmpty()) {
                    recTutor.setRecTutorFacultyFlag(Constants.FLAG_YES);
                } else {
                    recTutor.setRecTutorFacultyFlag(Constants.FLAG_NO);
                }
            }

        }
		/*
		if (mergedCls.getClassTutorFaculties() != null && mergedCls.getClassTutorFaculties().isEmpty()) {
			recTutor.setRecTutorFacultyFlag(Constants.FLAG_YES);
		}else {
			recTutor.setRecTutorFacultyFlag(Constants.FLAG_NO);
		}
		List resultFac = mergedCls.getClassTutorFaculties().stream().filter(it -> it.getFaculty().equals(recTutor.getTutor().getFacultyOfUniversity().getFaculty())).collect(Collectors.toList());
		if (resultFac.size() == 0) {
			recTutor.setRecTutorFacultyFlag(Constants.FLAG_YES);
		}else {
			recTutor.setRecTutorFacultyFlag(Constants.FLAG_NO);
		}
		*/
        //List resultUni = mergedCls.getClassTutorUniversities().stream().filter(it -> it.getUniversity() == null).collect(Collectors.toList());
        if (mergedCls.getClassTutorUniversities() != null) {
            if (mergedCls.getClassTutorUniversities().isEmpty()) {
                recTutor.setRecTutorUniversityFlag(Constants.FLAG_YES);
            } else {
                List resultUni = mergedCls.getClassTutorUniversities().stream().filter(it -> it.getUniversity().equals(recTutor.getTutor().getFacultyOfUniversity().getUniversity())).collect(Collectors.toList());
                if (resultUni.isEmpty()) {
                    recTutor.setRecTutorUniversityFlag(Constants.FLAG_YES);
                } else {
                    recTutor.setRecTutorUniversityFlag(Constants.FLAG_NO);
                }
            }

        }
		/*
		if (mergedCls.getClassTutorUniversities().isEmpty()) {
			recTutor.setRecTutorUniversityFlag(Constants.FLAG_YES);
		} else {
			recTutor.setRecTutorUniversityFlag(Constants.FLAG_NO);
		}
		List resultUni = mergedCls.getClassTutorUniversities().stream().filter(it -> it.getUniversity().equals(recTutor.getTutor().getFacultyOfUniversity().getUniversity())).collect(Collectors.toList());
		if (resultUni.size() == 0) {
			recTutor.setRecTutorUniversityFlag(Constants.FLAG_YES);
		} else {
			recTutor.setRecTutorUniversityFlag(Constants.FLAG_NO);
		}*/
		/*
		for (ClassTutorStudy classTutorStudy : cls.getClassTutorStudies()) {
			if ((classTutorStudy.getFaculty() != null && classTutorStudy.getFaculty().getFacId() == recTutor.getTutor().getFacultyOfUniversity().getFaculty().getFacId())
					|| classTutorStudy.getFaculty() == null) {
				recTutor.setRecTutorFacultyFlag(Constants.FLAG_YES);

			}
			if ((classTutorStudy.getUniversity() != null && classTutorStudy.getUniversity().getUniId() == recTutor.getTutor().getFacultyOfUniversity().getUniversity().getUniId())
					|| classTutorStudy.getUniversity() == null) {
				recTutor.setRecTutorUniversityFlag(Constants.FLAG_YES);

			}
		}*/
        if (recTutor.getRecTutorFacultyFlag() == null) {
            recTutor.setRecTutorFacultyFlag(Constants.FLAG_NO);
        }


        if (recTutor.getRecTutorUniversityFlag() == null) {
            recTutor.setRecTutorUniversityFlag(Constants.FLAG_NO);
        }

        // Tutor’s year experience
        recTutor.setRecTutorYearExp(0);
        if (mergedCls.getClazz1().getClsTutorExp() == null && mergedCls.getClazz2().getClsTutorExp() == null) {
            recTutor.setRecTutorExpFlag(Constants.FLAG_YES);
        } else {
            Integer expCls1 = 0;
            Integer expCls2 = 0;
            for (TutorSubject tutorSubject : recTutor.getTutor().getTutorSubject()) {
                if (tutorSubject.getSubjectDetail().getSubject().getSubjId() == mergedCls.getClazz1().getSubject().getSubjId()
                        && tutorSubject.getSubjectDetail().getSubjectGroup().getGrpId() == mergedCls.getClazz1().getSubjectGroupDetail().getSubjectGroup().getGrpId()
                        && tutorSubject.getTsbjExp() > 0) {
                    expCls1 = tutorSubject.getTsbjExp();
					/*
					recTutor.setRecTutorExpFlag(Constants.FLAG_YES);
					recTutor.setRecTutorYearExp(tutorSubject.getTsbjExp());*/
                }
                if (tutorSubject.getSubjectDetail().getSubject().getSubjId() == mergedCls.getClazz2().getSubject().getSubjId()
                        && tutorSubject.getSubjectDetail().getSubjectGroup().getGrpId() == mergedCls.getClazz2().getSubjectGroupDetail().getSubjectGroup().getGrpId()
                        && tutorSubject.getTsbjExp() > 0) {
                    expCls2 = tutorSubject.getTsbjExp();
					/*
					recTutor.setRecTutorExpFlag(Constants.FLAG_YES);
					recTutor.setRecTutorYearExp(tutorSubject.getTsbjExp());*/
                }
                if (expCls1 > 0 && expCls2 > 0) {
                    recTutor.setRecTutorExpFlag(Constants.FLAG_YES);
                    recTutor.setRecTutorYearExp(expCls1 + expCls2);
                }
            }
        }
        if (recTutor.getRecTutorExpFlag() == null) {
            recTutor.setRecTutorExpFlag(Constants.FLAG_NO);
        }
        return recTutor;
    }

    public static RecommendTutor filterTutorsByAvailableDayMerged(Integer days, Double classDuration, List<MergedClassAvailabletimeLocation> mAvas, Tutor tutor) {
        GoogleAPIMapServices googleService = new GoogleAPIMapServices();
        System.out.println("Class day per week : "+days + " / Duration : "+classDuration + " / Num of tutor available days : "+mAvas.size());
        GoogleAPIMapServices googleMapService = new GoogleAPIMapServices();
        List<RecommendTimelocation> recTimeLoc = new ArrayList<>();
        RecommendTutor recTutor = new RecommendTutor();
        recTutor.setTutor(tutor);
        recTutor.setNumOfDays(days);
        recTutor.setRecommendTimelocations(recTimeLoc);
        Set<Integer> daySet = new HashSet<>();

        for (MergedClassAvailabletimeLocation mAva : mAvas) {

            TutorAvailableTimeloacation daysMatch = tutor.getTutorAvailableTimeloacations()
                    .stream().filter(it -> mAva.getMeavDayId() == it.getTavDayId())
                    .findAny()
                    .orElse(null);
            if (daysMatch != null) {
                System.out.println("Tutor ID : " + tutor.getTurId() + " || Day : " + daysMatch.getTavDayId());
                System.out.println("From - To : " + daysMatch.getTavStartLocation() + " - " + mAva.getMervLocation());
                System.out.println("Merged class available : " + mAva.getMervStartTime() + " - " + mAva.getMeavEndTime());
                System.out.println("Tutor available : " + daysMatch.getTavStartTime() + " - " + daysMatch.getTavEndTime());
                Double overlapHours = DateTimeUtils.calculateOverlapTime(mAva.getMervStartTime(), daysMatch.getTavStartTime(), mAva.getMeavEndTime(), daysMatch.getTavEndTime());
                System.out.println("Overlap time : " + overlapHours);
                if (overlapHours >= classDuration) {
                    Double duration1 = DateTimeUtils.convertSecondsToHours(googleService.calculateTravelTime(daysMatch.getTavStartLat(), daysMatch.getTavStartLong(), mAva.getMervLat(), mAva.getMervLong(),
                            daysMatch.getTavTraDrive(), daysMatch.getTavTraTaxi(), daysMatch.getTavTraTrain(), daysMatch.getTavTraBus(), googleMapService));
                    Double duration2 = DateTimeUtils.convertSecondsToHours(googleService.calculateTravelTime(mAva.getMervLat(), mAva.getMervLong(), daysMatch.getTavEndLat(), daysMatch.getTavEndLong(),
                            daysMatch.getTavTraDrive(), daysMatch.getTavTraTaxi(), daysMatch.getTavTraTrain(), daysMatch.getTavTraBus(), googleMapService));
                    Double startTime = DateTimeUtils.getStartTime(mAva.getMervStartTime(), daysMatch.getTavStartTime());
                    Double endTime = DateTimeUtils.getEndTime(mAva.getMeavEndTime(), daysMatch.getTavEndTime());
                    System.out.println("Travel Time : "+DateTimeUtils.addTime(duration1,duration2));
                    // Tutor start time
                    if (!DateTimeUtils.doubleToDate(duration1 + duration2).after(DateTimeUtils.doubleToDate(Constants.MAX_TRAVELTIME))) {
                        if (daysMatch.getTavStartTime().equals(startTime)) {
                            Date expectDuration = DateTimeUtils.addTime(duration1, classDuration);
                            System.out.println("Expect overlap duration : " + DateTimeUtils.hourFormat(expectDuration));
                            Date expectStartClassTime = DateTimeUtils.addTime(daysMatch.getTavStartTime(), duration1);
                            System.out.println("Expect class start time : " + DateTimeUtils.hourFormat(expectStartClassTime));

                            Date expectFinishClassTime = DateTimeUtils.addTime(daysMatch.getTavStartTime(), duration1, classDuration);
                            System.out.println("Expect class end time : " + DateTimeUtils.hourFormat(expectFinishClassTime));
                            Date expectBackTime = DateTimeUtils.addTime(daysMatch.getTavStartTime(), duration1, classDuration, duration2);
                            System.out.println("Expect time back to tutor location : " + DateTimeUtils.hourFormat(expectBackTime));


                            if (!expectDuration.after(DateTimeUtils.doubleToDate(overlapHours)) && !expectFinishClassTime.after(DateTimeUtils.doubleToDate(daysMatch.getTavEndTime()))) {
                                System.out.println("* 1 Able to teach on the day");
                                RecommendTimelocation recTime = new RecommendTimelocation();
                                recTime.setRecdTraveltime1(duration1);
                                recTime.setRecdTraveltime2(duration2);
                                recTime.setRecdTraveltime(duration1 + duration2);
                                recTime.setRecdDayId(mAva.getMeavDayId());
                                recTime.setRecdStart(Double.parseDouble(DateTimeUtils.hourFormat(expectStartClassTime)));
                                recTime.setRecdEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectFinishClassTime)));
                                recTime.setRecdTravelStart(daysMatch.getTavStartTime());
                                recTime.setRecdTravelEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectBackTime)));
                                recTutor.addRecommendTimelocation(recTime);
                                daySet.add(mAva.getMeavDayId());
                            }
                            // Student start time
                        } else {
                            Date expectStartTravelTime = DateTimeUtils.subtractTime(startTime, duration1);
                            System.out.println("Expect time start from tutor location : " + DateTimeUtils.hourFormat(expectStartTravelTime));
                            Date expectStartClassTime = DateTimeUtils.addTime(mAva.getMervStartTime(), 0D);
                            System.out.println("Expect class start time : " + DateTimeUtils.hourFormat(expectStartClassTime));
                            Date expectFinishClassTime = DateTimeUtils.addTime(mAva.getMervStartTime(), classDuration);
                            System.out.println("Expect class finish time : " + DateTimeUtils.hourFormat(expectFinishClassTime));

                            Date expectBackTime = DateTimeUtils.addTime(startTime, classDuration, duration2);
                            System.out.println("Expect time back to tutor location : " + DateTimeUtils.hourFormat(expectBackTime));
                            if (!expectStartTravelTime.before(DateTimeUtils.doubleToDate(daysMatch.getTavStartTime())) && !expectBackTime.after(DateTimeUtils.doubleToDate(daysMatch.getTavEndTime()))) {
                                System.out.println("* 2 Able to teach on the day");
                                RecommendTimelocation recTime = new RecommendTimelocation();
                                recTime.setRecdTraveltime1(duration1);
                                recTime.setRecdTraveltime2(duration2);
                                recTime.setRecdTraveltime(duration1 + duration2);
                                recTime.setRecdDayId(mAva.getMeavDayId());
                                recTime.setRecdStart(mAva.getMervStartTime());
                                recTime.setRecdEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectFinishClassTime)));
                                recTime.setRecdTravelStart(Double.parseDouble(DateTimeUtils.hourFormat(expectStartTravelTime)));
                                recTime.setRecdTravelEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectBackTime)));
                                recTutor.addRecommendTimelocation(recTime);
                                daySet.add(mAva.getMeavDayId());
                            }

                        }
                    }
                }
                System.out.println("--------------------------------");
            }
        }


        if (daySet.size() >= days) {
            System.out.println("MATCH - Summary match days : " +daySet.size() + " From : "+days);
            System.out.println("--------------------------------");
            recTutor.setAvailableDays(Double.valueOf(daySet.size()));
            recTutor.setAvailableRatio(Double.valueOf(daySet.size())/days);
            recTutor.getRecommendTimelocations().sort(Comparator.comparing(RecommendTimelocation::getRecdTraveltime)
                    .reversed());
            recTutor.setMinTravelTime(recTutor.getRecommendTimelocations().get(0).getRecdTraveltime());
            return recTutor;
        } else {
            System.out.println("NOT MATCH - Summary match days : " +daySet.size() + " From : "+days);
            System.out.println("--------------------------------");
            return null;
        }

    }

    public List<MergedClass> convertRecTutorToMergedClass(List<RecommendTutor> recTutors){
        List<MergedClass> mergedClasses = new ArrayList<>();
        MergedClass tmpMergedClass = null;
        for (RecommendTutor recTutor : recTutors) {
            tmpMergedClass = recTutor.getMergedClass();
            tmpMergedClass.setRecommendTutors(new ArrayList<>());
            tmpMergedClass.addRecommendTutor(recTutor);
            mergedClasses.add(tmpMergedClass);
        }
        return mergedClasses;
    }

}

