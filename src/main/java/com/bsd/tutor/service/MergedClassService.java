package com.bsd.tutor.service;

import com.bsd.tutor.dao.TutorDao;
import com.bsd.tutor.model.*;
import com.bsd.tutor.model.Class;
import com.bsd.tutor.utils.ArrayListUtils;
import com.bsd.tutor.utils.Constants;
import com.bsd.tutor.utils.DateTimeUtils;
import com.bsd.tutor.utils.GoogleMapUtils;
import com.sun.scenario.effect.Merge;
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
        Set<Long> numOfDays = new HashSet<>();
        List<MergedClassAvailabletimeLocation> mergedTimes = new ArrayList<>();
        // Check available days for merged class both
        for(StudentAvailableTimelocation dayCls1 : cls1.getStudent().getStudentAvailableTimelocations()){
            for(StudentAvailableTimelocation dayCls2 : cls2.getStudent().getStudentAvailableTimelocations()){

                if (dayCls1.getSavDayId().equals(dayCls2.getSavDayId()) &&  GoogleMapUtils.distFrom(dayCls1.getSavLat(), dayCls1.getSavLong(), dayCls2.getSavLat(), dayCls2.getSavLong()) < Constants.DIFF_DISTANCE) {
                    Double[] time1 = DateTimeUtils.mergeClassByTime(dayCls1.getSavStartTime(), dayCls1.getSavEndTime(),dayCls2.getSavStartTime(), dayCls2.getSavEndTime(),cls1.getClsDuration(),cls2.getClsDuration());
                    Double[] time2 = DateTimeUtils.mergeClassByTime(dayCls2.getSavStartTime(), dayCls2.getSavEndTime(),dayCls1.getSavStartTime(), dayCls1.getSavEndTime(),cls2.getClsDuration(),cls1.getClsDuration());
                    if (time1[0] != null && time1[1] != null) {
                        MergedClassAvailabletimeLocation mergedTime = new MergedClassAvailabletimeLocation(tmpMergedClass, dayCls1.getSavDayId());
                        mergedTime.setMervStartTime(time1[0]);
                        mergedTime.setMeavEndTime(time1[1]);
                        mergedTime.setMervClsStart(1L);
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
                        mergedTime.setMervClsStart(2L);
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
            Long maxDay = Math.max(cls1.getClsDayperweek(),cls2.getClsDayperweek());
            tmpMergedClass.setMaxDays(maxDay.intValue());
            tmpMergedClass.setMerMergeratio(numOfDays.size()/maxDay);
            Long minDays  = Math.min(cls1.getClsDayperweek(),cls2.getClsDayperweek());
            tmpMergedClass.setMerMindayperweek(minDays);
            if (numOfDays.size() >= maxDay) {
                // for full merged
                tmpMergedClass.setMerType(Constants.MERGEDTYPE_FULL);

                tmpMergedClass.setAvaDays(numOfDays);
                tmpMergedClass.setNumberOfAvaDays(numOfDays.size());
                tmpMergedClass.setMergedClassAvailabletimes(mergedTimes);
            } else {
                tmpMergedClass.setMerType(Constants.MERGEDTYPE_PARTIAL);
                int remainDayCls1 = tmpMergedClass.getClazz1().getClsDayperweek().intValue() - numOfDays.size();
                int remainDayCls2 = tmpMergedClass.getClazz2().getClsDayperweek().intValue() - numOfDays.size();
                tmpMergedClass.setRemainDays1(remainDayCls1);
                tmpMergedClass.setRemainDays2(remainDayCls2);
                List<MergedClassAvailabletimeLocation> partialMergedTimes1 = new ArrayList<>();
                List<MergedClassAvailabletimeLocation> partialMergedTimes2 = new ArrayList<>();

                if (remainDayCls1 > 0) {
                    partialMergedTimes1 = assignedNewTimeLocForPartialMerged(tmpMergedClass.getClazz1(), tmpMergedClass, mergedTimes, Constants.TIMELOCTYPE_ONLYCLASS1);
                    tmpMergedClass.addAllMergedClassAvailabletime(partialMergedTimes1);
                }
                if (remainDayCls2 > 0) {
                    partialMergedTimes2 = assignedNewTimeLocForPartialMerged(tmpMergedClass.getClazz2(), tmpMergedClass, mergedTimes, Constants.TIMELOCTYPE_ONLYCLASS2);
                    tmpMergedClass.addAllMergedClassAvailabletime(partialMergedTimes2);
                }
                // find intersect days
                List<MergedClassAvailabletimeLocation> intersectPartialMergedTimes = new ArrayList<>();
                List<MergedClassAvailabletimeLocation> tmpPartialMergedTimes1 = new ArrayList<>(partialMergedTimes1);
                List<MergedClassAvailabletimeLocation> tmpPartialMergedTimes2 = new ArrayList<>(partialMergedTimes2);

                for (MergedClassAvailabletimeLocation mergedTimeLoc1 :tmpPartialMergedTimes1) {
                    List<MergedClassAvailabletimeLocation> intersectPartialMergedTimesEachDays = tmpPartialMergedTimes2.stream()
                            .filter(it -> mergedTimeLoc1.getMeavDayId() == it.getMeavDayId()).collect(Collectors.toList());
                    intersectPartialMergedTimes.addAll(intersectPartialMergedTimesEachDays);
                }
                tmpPartialMergedTimes1.removeAll(intersectPartialMergedTimes);
                tmpPartialMergedTimes2.removeAll(intersectPartialMergedTimes);
                if (remainDayCls1+remainDayCls2 > tmpPartialMergedTimes1.size() + tmpPartialMergedTimes2.size() + intersectPartialMergedTimes.size()) {
                    return null;
                }
            }

            return tmpMergedClass;
        }
        return null;

    }

    public List<MergedClassAvailabletimeLocation> assignedNewTimeLocForPartialMerged(Class cls, MergedClass tmpMergedClass, List<MergedClassAvailabletimeLocation> intersectMergedTimes, Long clsNo){
        List<MergedClassAvailabletimeLocation> partialMergedTimes = new ArrayList<>();
        List<StudentAvailableTimelocation> remainTimeLoc = cls.getStudent().getStudentAvailableTimelocations();
        for (MergedClassAvailabletimeLocation mergedTimeLoc : intersectMergedTimes) {
            List<StudentAvailableTimelocation> filterTimeLoc = remainTimeLoc
                    .stream()
                    .filter(it -> mergedTimeLoc.getMeavDayId() == it.getSavDayId()).collect(Collectors.toList());
            remainTimeLoc.removeAll(filterTimeLoc);
        }

        for (StudentAvailableTimelocation stuTimeLoc :remainTimeLoc) {
            MergedClassAvailabletimeLocation partialMergedTime = new MergedClassAvailabletimeLocation(tmpMergedClass, stuTimeLoc.getSavDayId());
            partialMergedTime.setMervStartTime(stuTimeLoc.getSavStartTime());
            partialMergedTime.setMeavEndTime(stuTimeLoc.getSavEndTime());
            partialMergedTime.setMervClsStart(clsNo);
            partialMergedTime.setMervLat(stuTimeLoc.getSavLat());
            partialMergedTime.setMervLong(stuTimeLoc.getSavLong());
            partialMergedTime.setMervLocation(stuTimeLoc.getSavLocation());
            partialMergedTime.setMeavDayId(stuTimeLoc.getSavDayId());
            partialMergedTimes.add(partialMergedTime);
        }

        return partialMergedTimes;
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
//        System.out.println("- Max days : "+tmpMergedClass.getMaxDays());
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
        recTutor.setRecTutorYearExp(0L);
        if (mergedCls.getClazz1().getClsTutorExp() == null && mergedCls.getClazz2().getClsTutorExp() == null) {
            recTutor.setRecTutorExpFlag(Constants.FLAG_YES);
        } else {
            long expCls1 = 0;
            long expCls2 = 0;
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

    public static MergedClass tutorMatchingForMergedClass(MergedClass tmpMergedClass){
            // - case full merge
            System.out.println("-----------------------------------------");
            System.out.println("Merged Class : "+tmpMergedClass.toString());
            System.out.println("-----------------------------------------");
            Long subjectId1 = tmpMergedClass.getClazz1().getSubject().getSubjId();
            Long groupDetailId1 = tmpMergedClass.getClazz1().getSubjectGroupDetail().getSubjectGroup().getGrpId();
            Long subjectId2 = tmpMergedClass.getClazz2().getSubject().getSubjId();
            Long groupDetailId2 = tmpMergedClass.getClazz2().getSubjectGroupDetail().getSubjectGroup().getGrpId();
            TutorDao tutorDao = new TutorDao();
            List<Tutor> tutors1 = tutorDao.findBySubjectAndProgram(subjectId1, groupDetailId1,tmpMergedClass.getMerTurPrgId());
            List<Tutor> tutors2 = tutorDao.findBySubjectAndProgram(subjectId2, groupDetailId2,tmpMergedClass.getMerTurPrgId());
            List<Tutor> intersectTutors = ArrayListUtils.intersection(tutors1,tutors2);
            System.out.println("2.4.1 Match subject and subject group - Match tutors : "+intersectTutors.size());
            for (Tutor tutor : intersectTutors) {
                System.out.println("- Tutor ID : "+tutor.getTurId());
                for (TutorAvailableTimeloacation tAva : tutor.getTutorAvailableTimeloacations()) {
                    System.out.println("Tutor available / Day : " + tAva.getTavDayId() + " Time : " + tAva.getTavStartTime() + " - " + tAva.getTavEndTime());
                }
            }
            System.out.println("-------------------------------------");
            System.out.println("2.4.2 Match with tutor available days");
            List<RecommendTutor> recommendTutors = new ArrayList<>();
            tmpMergedClass.setRecommendTutors(recommendTutors);
            if (tmpMergedClass.getMerType().equals(Constants.MERGEDTYPE_FULL)) {
                // Case full merged
                for (Tutor tutor : intersectTutors) {
                    System.out.println("- Tutor ID : "+tutor.getTurId());
                    // Filter by tutor available days
                    RecommendTutor recTutor = filterTutorsByAvailableDayMerged(tmpMergedClass.getMerMindayperweek(), tmpMergedClass.getClazz1().getClsDuration() + tmpMergedClass.getClazz2().getClsDuration(), tmpMergedClass.getMergedClassAvailabletimes(), tutor);
                    if (recTutor != null) {
                        tmpMergedClass.addRecommendTutor(recTutor);
                    }
                }
                System.out.println("-------------------------------------");
                System.out.println("Tutor recommend size : "+tmpMergedClass.getRecommendTutors().size());
                System.out.println("-------------------------------------");

            }
            // - case partial merge
            else {
                //
                boolean isCls1Pass = true;
                boolean isCls2Pass = true;
                List tmpTimeLocFull = tmpMergedClass.getMergedClassAvailabletimes().stream().filter(it -> (it.getMervClsStart().equals(1L) || it.getMervClsStart().equals(2L))).collect(Collectors.toList());
                List tmpTimeLocPart1 = tmpMergedClass.getMergedClassAvailabletimes().stream().filter(it -> it.getMervClsStart().equals(3L)).collect(Collectors.toList());
                List tmpTimeLocPart2 = tmpMergedClass.getMergedClassAvailabletimes().stream().filter(it -> it.getMervClsStart().equals(4L)).collect(Collectors.toList());

                for (Tutor tutor : intersectTutors) {
                    RecommendTutor recTutorFull = filterTutorsByAvailableDayMerged(Long.valueOf(tmpTimeLocFull.size()), tmpMergedClass.getClazz1().getClsDuration() + tmpMergedClass.getClazz2().getClsDuration(), tmpTimeLocFull, tutor);
                    RecommendTutor recTutorPart1 = null;
                    RecommendTutor recTutorPart2 = null;
                    if (tmpMergedClass.getRemainDays1() > 0) {
                        isCls1Pass = false;
                        recTutorPart1 = filterTutorsByAvailableDayMerged(Long.valueOf(tmpMergedClass.getRemainDays1()), tmpMergedClass.getClazz1().getClsDuration(), tmpTimeLocPart1, tutor);
                        if (recTutorPart1.getRecommendTimelocations().size() >= tmpMergedClass.getRemainDays1()) {
                            isCls1Pass = true;
                        }
                    }
                    if (tmpMergedClass.getRemainDays2() > 0) {
                        isCls2Pass = false;
                        recTutorPart2 = filterTutorsByAvailableDayMerged(Long.valueOf(tmpMergedClass.getRemainDays2()), tmpMergedClass.getClazz2().getClsDuration(), tmpTimeLocPart2, tutor);
                        if (recTutorPart2.getRecommendTimelocations().size() >= tmpMergedClass.getRemainDays2()) {
                            isCls2Pass = true;
                        }
                    }
                    if (recTutorFull != null && isCls1Pass && isCls2Pass) {
                        List<RecommendTimelocation> intersectPartials = new ArrayList<>();
                        List<RecommendTimelocation> tmpPartialMergedTimes1 = new ArrayList<>(recTutorPart1.getRecommendTimelocations());
                        List<RecommendTimelocation> tmpPartialMergedTimes2 = new ArrayList<>(recTutorPart2.getRecommendTimelocations());
                        for (RecommendTimelocation recTutorPart1TimeLoc :recTutorPart1.getRecommendTimelocations()) {
                            List<RecommendTimelocation> intersectPartialTimeLocEachDays =  recTutorPart2.getRecommendTimelocations().stream()
                                    .filter(it -> recTutorPart1TimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
                            intersectPartials.addAll(intersectPartialTimeLocEachDays);
                            tmpPartialMergedTimes1.removeAll(intersectPartialTimeLocEachDays);
                            tmpPartialMergedTimes2.removeAll(intersectPartialTimeLocEachDays);
                        }
                        if (tmpMergedClass.getRemainDays1()+tmpMergedClass.getRemainDays2() <= tmpPartialMergedTimes1.size() + tmpPartialMergedTimes2.size() + intersectPartials.size()) {
                            if (recTutorPart1 != null && !recTutorPart1.getRecommendTimelocations().isEmpty()) {
                                recTutorFull.getRecommendTimelocations().addAll(recTutorPart1.getRecommendTimelocations());
                            }
                            if (recTutorPart2 != null && !recTutorPart2.getRecommendTimelocations().isEmpty()) {
                                recTutorFull.getRecommendTimelocations().addAll(recTutorPart2.getRecommendTimelocations());
                            }
                            // Calculate ratio for partial merged
                            Double availableDays = recTutorFull.getAvailableDays() + tmpPartialMergedTimes1.size() + tmpPartialMergedTimes2.size() +  intersectPartials.size();
                            recTutorFull.setAvailableRatio(availableDays / (recTutorFull.getAvailableDays() + tmpMergedClass.getRemainDays1() + tmpMergedClass.getRemainDays2()));
                            tmpMergedClass.addRecommendTutor(recTutorFull);
                        }
                    }
                }
            }

           return tmpMergedClass;
    }

    public static RecommendTutor filterTutorsByAvailableDayMerged(Long days, Double classDuration, List<MergedClassAvailabletimeLocation> mAvas, Tutor tutor) {
        GoogleAPIMapServices googleService = new GoogleAPIMapServices();
        System.out.println("Class day per week : "+days + " / Duration : "+classDuration + " / Num of tutor available days : "+mAvas.size());
        GoogleAPIMapServices googleMapService = new GoogleAPIMapServices();
        List<RecommendTimelocation> recTimeLoc = new ArrayList<>();
        RecommendTutor recTutor = new RecommendTutor();
        recTutor.setTutor(tutor);
        recTutor.setNumOfDays(days);
        recTutor.setRecommendTimelocations(recTimeLoc);
        Set<Long> daySet = new HashSet<>();

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
                    System.out.println("HOUR : duration1 : "+duration1 + " duration2 : "+duration2);
                    Double startTime = DateTimeUtils.getStartTime(mAva.getMervStartTime(), daysMatch.getTavStartTime());
                    Double endTime = DateTimeUtils.getEndTime(mAva.getMeavEndTime(), daysMatch.getTavEndTime());
                    System.out.println("Travel Time : "+DateTimeUtils.hourFormat(DateTimeUtils.addTime(duration1,duration2)));
                    // Tutor start time
                    if (!DateTimeUtils.doubleToDate(duration1 + duration2).after(DateTimeUtils.doubleToDate(Constants.MAX_TRAVELTIME)) ||
                            DateTimeUtils.doubleToDate(duration1 + duration2).equals(DateTimeUtils.doubleToDate(Constants.MAX_TRAVELTIME))) {
                        if (daysMatch.getTavStartTime().equals(startTime)) {
                            Date expectDuration = DateTimeUtils.addTime(duration1, classDuration);
                            System.out.println("Expect overlap duration : " + DateTimeUtils.hourFormat(expectDuration));
                            Date expectStartClassTime = DateTimeUtils.addTime(daysMatch.getTavStartTime(), duration1);
                            System.out.println("Expect class start time : " + DateTimeUtils.hourFormat(expectStartClassTime));

                            Date expectFinishClassTime = DateTimeUtils.addTime(daysMatch.getTavStartTime(), duration1, classDuration);
                            System.out.println("Expect class end time : " + DateTimeUtils.hourFormat(expectFinishClassTime));
                            Date expectBackTime = DateTimeUtils.addTime(daysMatch.getTavStartTime(), duration1, classDuration, duration2);
                            System.out.println("Expect time back to tutor location : " + DateTimeUtils.hourFormat(expectBackTime));


                            if (!expectDuration.after(DateTimeUtils.doubleToDate(overlapHours)) && (!expectFinishClassTime.after(DateTimeUtils.doubleToDate(daysMatch.getTavEndTime()))
                            ||expectFinishClassTime.equals(DateTimeUtils.doubleToDate(daysMatch.getTavEndTime())))) {
                                System.out.println("* 1 Able to teach on the day");
                                RecommendTimelocation recTime = new RecommendTimelocation();
                                recTime.setRecdClassStart(mAva.getMervClsStart());
                                recTime.setRecdTraveltime1(duration1);
                                recTime.setRecdTraveltime2(duration2);
                                recTime.setRecdTraveltime(duration1 + duration2);
                                recTime.setRecdDayId(mAva.getMeavDayId());
                                recTime.setRecdStart(Double.parseDouble(DateTimeUtils.hourFormat(expectStartClassTime)));
                                recTime.setRecdEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectFinishClassTime)));
                                recTime.setRecdTravelStart(daysMatch.getTavStartTime());
                                recTime.setRecdTravelEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectBackTime)));
                                recTime.setRecdAvaStart(Double.parseDouble(DateTimeUtils.hourFormat(expectStartClassTime)));
                                recTime.setRecdAvaEnd(endTime);
                                recTime.setRecdLoc(mAva.getMervLocation());
                                recTime.setRecdLat(mAva.getMervLat());
                                recTime.setRecdLong(mAva.getMervLong());
                                recTime.setDuration(classDuration);
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
                            if (!expectStartTravelTime.before(DateTimeUtils.doubleToDate(daysMatch.getTavStartTime())) && (!expectBackTime.after(DateTimeUtils.doubleToDate(daysMatch.getTavEndTime()))
                            || expectBackTime.equals(DateTimeUtils.doubleToDate(daysMatch.getTavEndTime())))) {
                                System.out.println("* 2 Able to teach on the day");
                                RecommendTimelocation recTime = new RecommendTimelocation();
                                recTime.setRecdClassStart(mAva.getMervClsStart());
                                recTime.setRecdTraveltime1(duration1);
                                recTime.setRecdTraveltime2(duration2);
                                recTime.setRecdTraveltime(duration1 + duration2);
                                recTime.setRecdDayId(mAva.getMeavDayId());
                                recTime.setRecdStart(mAva.getMervStartTime());
                                recTime.setRecdEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectFinishClassTime)));
                                recTime.setRecdTravelStart(Double.parseDouble(DateTimeUtils.hourFormat(expectStartTravelTime)));
                                recTime.setRecdTravelEnd(Double.parseDouble(DateTimeUtils.hourFormat(expectBackTime)));
                                recTime.setRecdAvaStart(mAva.getMervStartTime());
                                recTime.setRecdAvaEnd(endTime);
                                recTime.setRecdLoc(mAva.getMervLocation());
                                recTime.setRecdLat(mAva.getMervLat());
                                recTime.setRecdLong(mAva.getMervLong());
                                recTime.setDuration(classDuration);
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

    public List<RecommendTutor> findRecTutorByClsId(List<RecommendTimelocation> tmpRecTimeLocs, RecommendTutor recTutor) {
        List<RecommendTutor> tmpRecTutors = new ArrayList<>();
        RecommendTutor recTutorMerged = null;
        if (recTutor.getClazz()!=null) {
            // find merged class
            for (RecommendTimelocation tmpRecTimeLoc : tmpRecTimeLocs) {
                if (tmpRecTimeLoc.getRecommendTutor().getMergedClass() != null) {
                    System.out.println("Merged1 : " + tmpRecTimeLoc.getRecommendTutor().getMergedClass().getClazz1().toString());
                    System.out.println("Merged2 : " + tmpRecTimeLoc.getRecommendTutor().getMergedClass().getClazz2().toString());
                    if (tmpRecTimeLoc.getRecommendTutor().getMergedClass().getClazz1().getClsId() == recTutor.getClazz().getClsId() ||
                            tmpRecTimeLoc.getRecommendTutor().getMergedClass().getClazz2().getClsId() == recTutor.getClazz().getClsId()) {
                        recTutorMerged = tmpRecTimeLoc.getRecommendTutor();
                        tmpRecTutors.add(recTutorMerged);
                        break;
                    }
                }
            }

        } else {
            recTutorMerged = recTutor;
        }
        if (recTutorMerged != null) {
            for (RecommendTimelocation tmpRecTimeLoc : tmpRecTimeLocs) {
                if (tmpRecTimeLoc.getRecommendTutor().getClazz() != null &&
                        (recTutorMerged.getMergedClass().getClazz1().getClsId() == tmpRecTimeLoc.getRecommendTutor().getClazz().getClsId()
                                || recTutorMerged.getMergedClass().getClazz2().getClsId() == tmpRecTimeLoc.getRecommendTutor().getClazz().getClsId() )) {
                    tmpRecTutors.add(tmpRecTimeLoc.getRecommendTutor());

                }
            }
        }

        return tmpRecTutors;
    }

}

