package com.bsd.tutor.controller;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.bsd.tutor.dao.*;
import com.bsd.tutor.model.*;
import com.bsd.tutor.model.Class;
import com.bsd.tutor.service.ClassService;
import com.bsd.tutor.service.GoogleAPIMapServices;
import com.bsd.tutor.service.MergedClassService;
import com.bsd.tutor.service.RecommendTutorService;
import com.bsd.tutor.utils.ArrayListUtils;
import com.bsd.tutor.utils.Constants;
import com.bsd.tutor.utils.DateTimeUtils;
import com.bsd.tutor.utils.GoogleMapUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import java.util.logging.Logger;

public class App {
	public static final Logger LOGGER = Logger.getLogger(App.class.getName());;
	public static void main(String[] args) {

/*
		System.out.println("###########################################");
		Double[] time1 = DateTimeUtils.mergeClassByTime(8.00, 18.00, 12.00, 15.00,2.0,2.0);
		Double[] time2 = DateTimeUtils.mergeClassByTime(12.00, 15.00, 8.00, 18.00,2.0,2.0);
		System.out.println(" 1 : "+time1[0] + " / "+time1[1]);
		System.out.println(" 2 : "+time2[0] + " / "+time2[1]);
		System.out.println("###########################################");*/

		// Find classes which status are 'OPEN' or 'REOPEN'
		List<String> status = new ArrayList<>();
		status.add(Constants.CLASS_STATUS_OPEN);
		status.add(Constants.CLASS_STATUS_REOPEN);
		ClassDao classDao = new ClassDao();
		List<Class> classes = classDao.findByStatus(status);


		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("--- 1. Find classes which status are 'Open' or 'Reopen' ---");
		System.out.println("Size classes : " + classes.size());

		// Case 1 - Merge classes cause of same location
		// - 50 Metres distance
		// - Able to teach without time gap.
		// Step 1.1 : Check class spec that able to merge by tutor's sex
		System.out.println("--- 2. Merge classes ---");
		System.out.println("2.1 Merged Class : Check class spec that able to be merged by tutor's sex and create class merge");
		MergedClassService mergedClassService = new MergedClassService();
		List<MergedClass> mergedClasses = mergedClassService.getAllMergedClass(classes);

		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("2.2 Merged Class : Order merged class by specification : Same student, parent, subject and subject group, only subject, merged ratio (Max days/Total match days), min merged days");
		// Step 1.4 : Order merged class
		System.out.println("Before Order");
		for (MergedClass mergedClass : mergedClasses) {
			System.out.println("Class : "+mergedClass.getClazz1().getClsId() + " , " + mergedClass.getClazz2().getClsId());


		}
		mergedClasses.sort(Comparator.comparing(MergedClass::getMerSamestuFlag).reversed()
				.thenComparing(Comparator.comparing(MergedClass::getMerSameparentFlag).reversed()
						.thenComparing(Comparator.comparing(MergedClass::getMerSamesubgFlag).reversed()
								.thenComparing(Comparator.comparing(MergedClass::getMerSamesubFlag).reversed()
										.thenComparing(Comparator.comparing(MergedClass::getMerMergeratio)
												.thenComparing(Comparator.comparing(MergedClass::getMerMindayperweek).reversed()))))));
		System.out.println("After Order");
		for (MergedClass mergedClass : mergedClasses) {
			System.out.println("Class : "+mergedClass.getClazz1().getClsId() + " , " + mergedClass.getClazz2().getClsId());
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("2.3 Merged Class : Filter out merged classes which have the same class");
		// Step 2.3 : Select option which class not duplicate
		List<MergedClass> selectedMergedClasses = new ArrayList<>();
		if (mergedClasses.size() > 0) {
			selectedMergedClasses.add(mergedClasses.get(0));
		}
		System.out.println("Before filter out / Size : "+mergedClasses.size());
		for (MergedClass mergedClass : mergedClasses) {
			System.out.println("Class : "+mergedClass.getClazz1().getClsId() + " , " + mergedClass.getClazz2().getClsId());
			selectedMergedClasses = findDupList(selectedMergedClasses, mergedClass);

		}

		System.out.println("After filter out / Size : "+selectedMergedClasses.size());
		for (MergedClass mergedClass : selectedMergedClasses) {
			System.out.println("Class : "+mergedClass.getClazz1().getClsId() + " , " + mergedClass.getClazz2().getClsId());

		}

		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("2.4 Merged Class : Matching Tutor");
		// Step 1.6 : Matching tutor
		TutorDao tutorDao = new TutorDao();
		StudentAvailableTimelocationDao sTimelocationDao = new StudentAvailableTimelocationDao();
		List<MergedClass> cancelledMergedClasses = new ArrayList<>();
		for (MergedClass tmpMergedClass : selectedMergedClasses) {
			// - case full merge
			System.out.println("-----------------------------------------");
			System.out.println("Merged Class : "+tmpMergedClass.toString());
			System.out.println("-----------------------------------------");
			Integer subjectId1 = tmpMergedClass.getClazz1().getSubject().getSubjId();
			Integer groupDetailId1 = tmpMergedClass.getClazz1().getSubjectGroupDetail().getSubjectGroup().getGrpId();
			Integer subjectId2 = tmpMergedClass.getClazz2().getSubject().getSubjId();
			Integer groupDetailId2 = tmpMergedClass.getClazz2().getSubjectGroupDetail().getSubjectGroup().getGrpId();
			List<Tutor> tutors1 = tutorDao.findBySubjectAndProgram(subjectId1, groupDetailId1,tmpMergedClass.getMerTurPrgId());
			List<Tutor> tutors2 = tutorDao.findBySubjectAndProgram(subjectId2, groupDetailId2,tmpMergedClass.getMerTurPrgId());
			List<Tutor> intersectTutors = ArrayListUtils.intersection(tutors1,tutors2);
			System.out.println("2.4.1 Match subject and subject group - Match tutors : "+intersectTutors.size());
			for (Tutor tutor : intersectTutors) {
				System.out.println("- Tutor ID : "+tutor.getTurId());
				for (TutorAvailableTimeloacation tAva : tutor.getTutorAvailableTimeloacations())
				System.out.println("Tutor available / Day : "+tAva.getTavDayId() + " Time : " + tAva.getTavStartTime() + " - " + tAva.getTavEndTime());

			}
			System.out.println("-------------------------------------");
			System.out.println("2.4.2 Match with tutor available days");
			List<RecommendTutor> recommendTutors = new ArrayList<>();
			tmpMergedClass.setRecommendTutors(recommendTutors);
			if (tmpMergedClass.getMerMergeratio() >= 1) {
				// Case full merged
				for (Tutor tutor : intersectTutors) {
					System.out.println("- Tutor ID : "+tutor.getTurId());
					// Filter by tutor available days
					RecommendTutor recTutor = mergedClassService.filterTutorsByAvailableDayMerged(tmpMergedClass.getMerMindayperweek(), tmpMergedClass.getClazz1().getClsDuration() + tmpMergedClass.getClazz2().getClsDuration(), tmpMergedClass.getMergedClassAvailabletimes(), tutor);
					if (recTutor != null) {
						tmpMergedClass.addRecommendTutor(recTutor);
					}
				}
				System.out.println("-------------------------------------");
				System.out.println("Tutor recommend size : "+tmpMergedClass.getRecommendTutors().size());
				System.out.println("-------------------------------------");

			}/*
			// - case partial merge
			else {
				//
				// For class 1
				boolean isCls1Pass = true;
				boolean isCls2Pass = true;
				boolean isMergedPass = true;
				List<RecommendTutor> tmpRecommendClass1 = new ArrayList<>();
				List<RecommendTutor> tmpRecommendClass2 = new ArrayList<>();
				List<RecommendTutor> tmpRecommendMerged = new ArrayList<>();

				if (tmpMergedClass.getClazz1().getClsDayperweek() > tmpMergedClass.getNumberOfAvaDays()) {
					// Find available days
					Integer needDays = tmpMergedClass.getClazz1().getClsDayperweek() - tmpMergedClass.getNumberOfAvaDays();
					// Get all days of class except the match days.
					List<StudentAvailableTimelocation> sTimeLocations = sTimelocationDao.findByExceptSpecificDays(tmpMergedClass.getAvaDays());
					if (sTimeLocations.size() >= needDays) {
						for (Tutor tutor : intersectTutors) {
							System.out.println("- Tutor ID : "+tutor.getTurId());
							// Filter by tutor available days
							RecommendTutor recTutor = filterTutorsByAvailableDay(needDays, tmpMergedClass.getClazz1().getClsDuration(), sTimeLocations, tutor);
							if (recTutor != null) {
								tmpRecommendClass1.add(recTutor);
							}
						}
						if (tmpRecommendClass1.isEmpty()) {
							isCls1Pass = false;
						}
					}
				}
				// For class 2
				if (tmpMergedClass.getClazz2().getClsDayperweek() > tmpMergedClass.getNumberOfAvaDays()) {
					// Find available days
					Integer needDays = tmpMergedClass.getClazz2().getClsDayperweek() - tmpMergedClass.getNumberOfAvaDays();
					// Get all days of class except the match days.
					List<StudentAvailableTimelocation> sTimeLocations = sTimelocationDao.findByExceptSpecificDays(tmpMergedClass.getAvaDays());
					if (sTimeLocations.size() >= needDays) {
						for (Tutor tutor : intersectTutors) {
							System.out.println("- Tutor ID : "+tutor.getTurId());
							// Filter by tutor available days
							RecommendTutor recTutor = filterTutorsByAvailableDay(needDays, tmpMergedClass.getClazz2().getClsDuration(), sTimeLocations, tutor);
							if (recTutor != null) {
								tmpRecommendClass2.add(recTutor);
							}
						}
						if (tmpRecommendClass2.isEmpty()) {
							isCls2Pass = false;
						}
					}
				}
				// For merged class

				for (Tutor tutor : intersectTutors) {
					System.out.println("- Tutor ID : "+tutor.getTurId());
					// Filter by tutor available days
					RecommendTutor recTutor = filterTutorsByAvailableDayMerged(tmpMergedClass.getMerMindayperweek(), tmpMergedClass.getClazz1().getClsDuration() + tmpMergedClass.getClazz2().getClsDuration(), tmpMergedClass.getMergedClassAvailabletimes(), tutor);
					if (recTutor != null) {
						tmpRecommendMerged.add(recTutor);
					}
				}
				if (tmpRecommendMerged.isEmpty()) {
					isMergedPass = false;
				}
				// Intersect
				if (isCls1Pass && isCls2Pass && isMergedPass) {
					if (!tmpRecommendClass1.isEmpty()) {
						tmpRecommendMerged.retainAll(tmpRecommendClass1);
					}
					if (!tmpRecommendClass1.isEmpty()) {
						tmpRecommendMerged.retainAll(tmpRecommendClass2);
					}
					tmpMergedClass.setRecommendTutors(tmpRecommendMerged);
				}


			}*/
			// If cannot find recommend tutor, pass class to normal case.
			if (tmpMergedClass.getRecommendTutors().isEmpty()) {
				System.out.println("* Cannot Found Class : "+tmpMergedClass.toString() + " -> Move classes to normal case");
				cancelledMergedClasses.add(tmpMergedClass);
			} else {
				/////////////////////// Remove ////////////////////////////
				classes.remove(tmpMergedClass.getClazz1());
				classes.remove(tmpMergedClass.getClazz2());
				System.out.println("* Found Class : "+tmpMergedClass.toString());

			}
		}
		// remove merged class if cannot find recommend tutor
		selectedMergedClasses.removeAll(cancelledMergedClasses);
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("2.5 Merged Class - Order Tutor by merged class specification");
		TutorMatchingDao tutorMatchingDao = new TutorMatchingDao();
		List<TutorMatching> tutorMatchings = tutorMatchingDao.list();
		for (MergedClass tmpMergedClass : selectedMergedClasses) {
			System.out.println("------------ Class " + tmpMergedClass.toString() + " ------------");
			for (RecommendTutor recTutor : tmpMergedClass.getRecommendTutors()) {
				System.out.println("Tutor Name : " + recTutor.getTutor().getTurFirstname());
				RecommendTutor tmpRecTutor = mergedClassService.tutorSpecificationMerged(tmpMergedClass, recTutor);
				System.out.println(recTutor.getRecTutorSexFlag() + recTutor.getRecTutorFacultyFlag() + recTutor.getRecTutorUniversityFlag() + recTutor.getRecTutorExpFlag());
				if (tmpMergedClass.getMerTurExp() != null) {
					System.out.println(recTutor.getRecTutorYearExp());
				}
				TutorMatching matching = tutorMatchings.stream().filter(it -> tmpRecTutor.getRecTutorSexFlag().equals(it.getMatTutorSexFlag())
						&& tmpRecTutor.getRecTutorFacultyFlag().equals(it.getMatTutorFacultyFlag())
						&& tmpRecTutor.getRecTutorUniversityFlag().equals(it.getMatTutorUniversityFlag())
						&& tmpRecTutor.getRecTutorExpFlag().equals(it.getMatTutorExpFlag()))
						.findAny()
						.orElse(null);
				tmpRecTutor.setRecTutorOrder(matching.getMatOrder());
				for (RecommendTimelocation timeLoc : tmpRecTutor.getRecommendTimelocations()) {
					System.out.println("Day : " + timeLoc.getRecdDayId() + " Time : " + timeLoc.getRecdStart() + " - " + timeLoc.getRecdEnd() + " Reserved Time : "+ timeLoc.getRecdStart() + " - " + timeLoc.getRecdTravelEnd());
				}
			}

			// Order Recommend Tutor
			if (tmpMergedClass.getMerTurExp() != Constants.FLAG_YES) {
				tmpMergedClass.getRecommendTutors().sort(Comparator.comparing(RecommendTutor::getMinTravelTime));
				tmpMergedClass.getRecommendTutors().sort(Comparator.comparing(RecommendTutor::getRecTutorOrder));
			} else {
				tmpMergedClass.getRecommendTutors().sort(Comparator.comparing(RecommendTutor::getMinTravelTime));
				tmpMergedClass.getRecommendTutors().sort(Comparator.comparing(RecommendTutor::getRecTutorYearExp));
				tmpMergedClass.getRecommendTutors().sort(Comparator.comparing(RecommendTutor::getRecTutorOrder));
			}
			for (int i = 0 ; i < tmpMergedClass.getRecommendTutors().size() ; i++) {
				tmpMergedClass.getRecommendTutors().get(i).setRecOrder(i + 1);
			}

		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		for (MergedClass tmpMergedClass : selectedMergedClasses) {
			System.out.println("------------ Class " + tmpMergedClass.toString() + " ------------");
			for (RecommendTutor recTutor : tmpMergedClass.getRecommendTutors()) {
				System.out.println("Tutor Name : " + recTutor.getTutor().getTurFirstname() + " Student ID : "+recTutor.getMergedClass().getClazz1().getStudent().getStuId()+ ","+recTutor.getMergedClass().getClazz2().getStudent().getStuId());

				for (RecommendTimelocation timeLoc : recTutor.getRecommendTimelocations()) {
					System.out.println("Day : " + timeLoc.getRecdDayId() + " Time : " + timeLoc.getRecdStart() + " - " + timeLoc.getRecdEnd() + " Reserved Time : "+ timeLoc.getRecdStart() + " - " + timeLoc.getRecdTravelEnd());
				}
			}

		}

		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("2.6 Merged Class -  Check time overlap of student");
		// Get the first recommend tutors
		RecommendTutorService recTutorService = new RecommendTutorService();
		List<RecommendTutor> mergedTutors = selectedMergedClasses.stream().filter(mergedClass -> !mergedClass.getRecommendTutors().isEmpty()).map(mergedClass -> mergedClass.getRecommendTutors().get(0)).collect(Collectors.toList());
		Map<Integer,List<RecommendTutor>> groupMergedClassByStudent = groupByStudentMerged(mergedTutors);
		Map<Integer,List<RecommendTutor>> notOverlapMergedStudent = selectTutor(groupMergedClassByStudent, Constants.FLAG_STUDENT);

		System.out.println("\r\n** Class that can match student **");
		List<Class> matchMergedClass = new ArrayList<>();
		List<RecommendTutor> recTutors = new ArrayList<>();
		for (Map.Entry<Integer,List<RecommendTutor>> entry : notOverlapMergedStudent.entrySet())
		{
			recTutors.addAll(entry.getValue());
			System.out.println("Student ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				System.out.println("- Class ID : " + rec.getMergedClass().toString() + " / Tutor ID : "+rec.getTutor().getTurId() + " / Days : "+rec.getMergedClass().getMerMindayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()){
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd());
				}
			}
		}

		List<RecommendTutor> recTutorsConvertFromMergedStuMap = recTutorService.convertMapToRecTutors(notOverlapMergedStudent);

		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("2.7 Merged Class -  Check time overlap of tutor");

		Map<Integer,List<RecommendTutor>> groupMergedClassByTutor = groupByTutor(recTutorsConvertFromMergedStuMap);
		Map<Integer,List<RecommendTutor>> notOverlapMergedTutor = selectTutor(groupMergedClassByTutor, Constants.FLAG_TUTOR);

		System.out.println("\r\n** Class that can match tutor **");
		Set<Class> matchClasses = new HashSet<>();
		List<MergedClass> finalMergedClasses = new ArrayList<>();
		for (Map.Entry<Integer,List<RecommendTutor>> entry : notOverlapMergedTutor.entrySet())
		{

			System.out.println("Tutor ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				finalMergedClasses.add(rec.getMergedClass());
				System.out.println("- Class ID : " + rec.getMergedClass().toString() + " / Student ID : "+rec.getMergedClass().getClazz1().getStudent().getStuId()+ ","+rec.getMergedClass().getClazz2().getStudent().getStuId() + " / Days : "+rec.getMergedClass().getMerMindayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				matchMergedClass.add(rec.getClazz());
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()){
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd());
				}
			}
		}

		// Convert map to rec tutor list
		List<RecommendTutor> finalMergedRecTutors = recTutorService.convertMapToRecTutors(notOverlapMergedTutor);


		System.out.println("\r\n** Class that not match any tutors **");
		classes.removeAll(matchMergedClass);
		for (Class cls : classes) {
			System.out.println("- Class ID : " + cls.getClsId());
		}
		//////////////////// Save Dao //////////////////////
		// Save merge class to DB
		/*
		MergedClassDao mergedClassDao = new MergedClassDao();
		for (MergedClass finalMerged :finalMergedClasses) {
			mergedClassDao.create(finalMerged);

		}*/
		//////////////////// Save Dao //////////////////////

		// Case 2 - Normal case
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("+++++++++++++ NORMAL CASE ++++++++++++++++");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("3.1 Normal Class : Matching Tutor");
		System.out.println("3.1.1 Matching Tutor by subject subject group and available time");
		System.out.println("Class size : "+classes.size());
		ClassService classService = new ClassService();

		for (Class cls : classes) {
			List<RecommendTutor> recommendTutors = new ArrayList<>();
			cls.setRecommendTutors(recommendTutors);
			Integer subjectID = cls.getSubject().getSubjId();
			System.out.println("Class ID : " + cls.getClsId() + " Subject : " + cls.getSubject().getSubjName() + " : " + cls.getSubjectGroupDetail().getGrpdName() + " Class days per week : "+cls.getClsDayperweek());
			// find tutor by subject
			List<Tutor> tutors = tutorDao.findBySubjectAndProgram(subjectID, cls.getSubjectGroupDetail().getSubjectGroup().getGrpId(), cls.getClsPrgId());
			for (Tutor tutor : tutors) {
				System.out.println("- Tutor ID : "+tutor.getTurId());
				for (TutorAvailableTimeloacation tAva : tutor.getTutorAvailableTimeloacations())
					System.out.println("Tutor available / Day : "+tAva.getTavDayId() + " Time : " + tAva.getTavStartTime() + " - " + tAva.getTavEndTime());

			}
			System.out.println("------------------------------");
			for (Tutor tutor : tutors) {
				System.out.println("- Tutor : " + tutor.getTurId());
				// Filter by tutor available days
				RecommendTutor recTutor = classService.filterTutorsByAvailableDay(cls.getClsDayperweek(), cls.getClsDuration(), cls.getStudent().getStudentAvailableTimelocations(), tutor);
				if (recTutor != null) {
					recTutor.setClazz(cls);
					cls.addRecommendTutor(recTutor);
					System.out.println("Rec Tutor / Class : " +recTutor.getClazz().getClsId() +"/ Tutor : " + recTutor.getTutor().getTurId() +"/ Dayset : "+recTutor.getAvailableDays());
				}
			}
			System.out.println("-------------------------------------");
			System.out.println("Tutor recommend size : "+cls.getRecommendTutors().size());
			System.out.println("-------------------------------------");
		}

		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("3.1.2 Matching Tutor by tutor spec");
		// lookup in Tutor Matching table and assign order no


		for (Class cls : classes) {
			System.out.println("------------ Class " + cls.getClsId() + " ------------");
			for (RecommendTutor recTutor : cls.getRecommendTutors()) {
				System.out.println("Tutor Name : " + recTutor.getTutor().getTurFirstname());

				RecommendTutor tmpRecTutor = classService.tutorSpecification(cls, recTutor);
				System.out.println(recTutor.getRecTutorSexFlag() + recTutor.getRecTutorFacultyFlag() + recTutor.getRecTutorUniversityFlag() + recTutor.getRecTutorExpFlag());

				TutorMatching matching = tutorMatchings.stream().filter(it -> tmpRecTutor.getRecTutorSexFlag().equals(it.getMatTutorSexFlag())
					&& tmpRecTutor.getRecTutorFacultyFlag().equals(it.getMatTutorFacultyFlag())
					&& tmpRecTutor.getRecTutorUniversityFlag().equals(it.getMatTutorUniversityFlag())
					&& tmpRecTutor.getRecTutorExpFlag().equals(it.getMatTutorExpFlag()))
						.findAny()
						.orElse(null);
				recTutor.setRecTutorOrder(matching.getMatOrder());
				for (RecommendTimelocation timeLoc : tmpRecTutor.getRecommendTimelocations()) {
					System.out.println("Day : " + timeLoc.getRecdDayId() + " Time : " + timeLoc.getRecdStart() + " - " + timeLoc.getRecdEnd());
				}
			}

			// Order Recommend Tutor
			if (cls.getClsTutorExp().equals(Constants.FLAG_YES)) {
				cls.getRecommendTutors().sort(Comparator.comparing(RecommendTutor::getRecTutorOrder)
						.thenComparing(Comparator.comparing(RecommendTutor::getRecTutorYearExp)).reversed()
						.thenComparing(Comparator.comparing(RecommendTutor::getMinTravelTime)));
			} else {
				cls.getRecommendTutors().sort(Comparator.comparing(RecommendTutor::getRecTutorOrder)
						.thenComparing(Comparator.comparing(RecommendTutor::getMinTravelTime)));
			}

			for (int i = 0 ; i < cls.getRecommendTutors().size() ; i++) {
				cls.getRecommendTutors().get(i).setRecOrder(i + 1);
			}

		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("3.3 Order recommend tutors");
		for (Class cls : classes) {
			System.out.println("------------ Class " + cls.getClsId() + " : "+ cls.getClsDayperweek()+ " days per week ------------");
			for (RecommendTutor recTutor : cls.getRecommendTutors()) {
				System.out.println("Tutor Name : " + recTutor.getTutor().getTurFirstname());
				System.out.println("Tutor Spec Order : " + recTutor.getRecTutorOrder());
				System.out.println("Tutor Order : " + recTutor.getRecOrder());
				System.out.println("Tutor Year Exp : " + recTutor.getRecTutorYearExp());
				System.out.println("Tutor Min Travel Time : " + recTutor.getMinTravelTime());
				for (RecommendTimelocation timeLoc : recTutor.getRecommendTimelocations()) {
					System.out.println("Day : " + timeLoc.getRecdDayId() + " Time : " + timeLoc.getRecdStart() + " - " + timeLoc.getRecdEnd());
				}
				System.out.println("--------------------------------");
			}
		}
		// Get the first recommend tutors
		recTutors = new ArrayList<>();
		List<RecommendTutor> fistRecTutors = new ArrayList<>();
		for (Class cls : classes) {
			if (cls.getRecommendTutors().size() > 0) {
				fistRecTutors.add(cls.getRecommendTutors().get(0));
				recTutors.add(cls.getRecommendTutors().get(0));
			}
		}
		Map<Integer,List<RecommendTutor>> groupClassByTutor = groupByTutor(fistRecTutors);
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("3.5 Select the first recommend tutor and check time overlap by tutor");
		Map<Integer,List<RecommendTutor>> notOverlap = selectTutor(groupClassByTutor, Constants.FLAG_TUTOR);

		System.out.println("\r\n** Class that can match tutor **");
		for (Map.Entry<Integer,List<RecommendTutor>> entry : notOverlap.entrySet())
		{

			System.out.println("Tutor ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Student ID : "+rec.getClazz().getStudent().getStuId() + " / Days : "+rec.getClazz().getClsDayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				matchClasses.add(rec.getClazz());
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()){
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd());
				}
			}
		}
		System.out.println("\r\n** Class that not match any tutors **");
		classes.removeAll(matchClasses);
		for (Class cls : classes) {
			System.out.println("- Class ID : " + cls.getClsId());
		}
		// Convert map to rec tutor list
		List<RecommendTutor> recTutorsConvertFromTutorMap = recTutorService.convertMapToRecTutors(notOverlap);

		Map<Integer,List<RecommendTutor>> groupClassByStudent = groupByStudent(recTutorsConvertFromTutorMap);
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("3.6 Select the first recommend tutor and check time overlap by student");
		Map<Integer,List<RecommendTutor>> notOverlapStudent = selectTutor(groupClassByStudent, Constants.FLAG_STUDENT);

		System.out.println("\r\n** Class that can match tutor **");
		List<Class> matchClassStudent = new ArrayList<>();
		for (Map.Entry<Integer,List<RecommendTutor>> entry : notOverlapStudent.entrySet())
		{

			System.out.println("Student ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Tutor ID : "+rec.getTutor().getTurId() + " / Days : "+rec.getClazz().getClsDayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				matchClassStudent.add(rec.getClazz());
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()){
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd());
				}
			}
		}
		System.out.println("\r\n** Class that not match any tutors **");
		classes.removeAll(matchClassStudent);
		for (Class cls : classes) {
			System.out.println("- Class ID : " + cls.getClsId());
		}



		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("4. Check overlap merged class and class");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		// Find Recommend Tutors duplicated time slot for mix classes case (Student)
		//////////////// REMOVE //////////////////
	//	mergedRecTutorsStep19 = new ArrayList<>();
		// Convert map to rec tutors list
		List<RecommendTutor> recTutorsConvertFromStuMap = recTutorService.convertMapToRecTutors(notOverlapStudent);

		System.out.println("Normal : "+recTutorsConvertFromStuMap.size());
		System.out.println("Merged : "+finalMergedRecTutors.size());
		Map<Integer,List<RecommendTutor>> groupMergedClassByStudentStep4Map = groupByStudentMerged(finalMergedRecTutors);
		Map<Integer,List<RecommendTutor>> groupClassByStudentStep4Map = groupByStudent(recTutorsConvertFromStuMap);
		Map<Integer,List<RecommendTutor>> allByStudentMap = selectTutorForMix(groupMergedClassByStudentStep4Map, groupClassByStudentStep4Map, Constants.FLAG_STUDENT);
		recTutors = new ArrayList<>();
		for (Map.Entry<Integer,List<RecommendTutor>> entry : allByStudentMap.entrySet())
		{
			System.out.println("Student ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				recTutors.add(rec);
				if (rec.getMergedClass() != null) {
					System.out.println("- Class ID : " + rec.getMergedClass().toString() + " / Tutor ID : " + rec.getTutor().getTurId() + " / Days : "+rec.getMergedClass().getMerMindayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				} else {
					System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Tutor ID : " + rec.getTutor().getTurId()  + " / Days : " + rec.getClazz().getClsDayperweek() + " / Rec Order : " + rec.getRecOrder() + " / Score : " + rec.getRecTutorOrder());
				}
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()){
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd());
				}
			}
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		// Find Recommend Tutors duplicated time slot for mix classes case (Tutor)
		// Convert map to rec tutors list
		List<RecommendTutor> recTutorsConvertFromMixStuMap = recTutorService.convertMapToRecTutors(allByStudentMap);

		MixClass recTutorsMixClass = splitMergedAndNormalClass(recTutorsConvertFromMixStuMap);
		List<RecommendTutor> rectutorsNormal = recTutorsMixClass.getRecTutorsNormal();
		List<RecommendTutor> rectutorsMerged = recTutorsMixClass.getRecTutorsMerged();
		System.out.println("Normal : "+rectutorsNormal.size());
		System.out.println("Merged : "+rectutorsMerged.size());
		Map<Integer,List<RecommendTutor>> groupMergedClassByTutorStep4Map = groupByTutor(rectutorsMerged);
		Map<Integer,List<RecommendTutor>> groupClassByTutorStep4Map = groupByTutor(rectutorsNormal);
		Map<Integer,List<RecommendTutor>> allByTutorMap = selectTutorForMix(groupMergedClassByTutorStep4Map, groupClassByTutorStep4Map, Constants.FLAG_TUTOR);
		for (Map.Entry<Integer,List<RecommendTutor>> entry : allByTutorMap.entrySet())
		{

			System.out.println("Tutor ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				if (rec.getMergedClass() != null) {
					System.out.println("- Class ID : " + rec.getMergedClass().toString() + " / Student ID : " + rec.getMergedClass().getClazz1().getStudent().getStuId() +","+rec.getMergedClass().getClazz2().getStudent().getStuId()+ " / Days : "+rec.getMergedClass().getMerMindayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				} else {
					System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Student ID : " + rec.getClazz().getStudent().getStuId() + " / Days : " + rec.getClazz().getClsDayperweek() + " / Rec Order : " + rec.getRecOrder() + " / Score : " + rec.getRecTutorOrder());
				}
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()){
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd());
				}
			}
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		// Create recommend tutors
		List<RecommendTutor> finalRecTutors = recTutorService.convertMapToRecTutors(allByTutorMap);
		MixClass finalMixClass = splitMergedAndNormalClass(finalRecTutors);
		List<RecommendTutor> finalRecTutorsMergedClass = finalMixClass.getRecTutorsMerged();
		List<RecommendTutor> finalRecTutorsClass = finalMixClass.getRecTutorsNormal();
		List<MergedClass> finalMeredClasses = mergedClassService.convertRecTutorToMergedClass(finalRecTutorsMergedClass);
		RecommendTutorDao recommendTutorDao = new RecommendTutorDao();
		recommendTutorDao.createList(finalRecTutorsClass);
		MergedClassDao mergedClassDao = new MergedClassDao();
		mergedClassDao.createList(finalMeredClasses);


		System.exit(0);
	}
	public static MixClass splitMergedAndNormalClass(List<RecommendTutor> recTutors){

		List<RecommendTutor> recTutorsNormal = new ArrayList<>();
		List<RecommendTutor> recTutorsMerged = new ArrayList<>();

		for (RecommendTutor rectutor : recTutors) {
			if (rectutor.getMergedClass() != null) {
				recTutorsMerged.add(rectutor);
			} else {
				recTutorsNormal.add(rectutor);
			}
		}

		MixClass mixClass = new MixClass(recTutorsNormal, recTutorsMerged);

		return mixClass;
	}

	public static Double calculateRatio(RecommendTutor recTutor){
		if (recTutor.getClazz()!=null) {
			return recTutor.getAvailableDays()/recTutor.getClazz().getClsDayperweek();
		}
		return recTutor.getAvailableDays()/recTutor.getMergedClass().getMerMindayperweek();

	}
	public static RecommendTutor checkMergedClassAvaiableTime(List<RecommendTutor> mergedRecommendTutors, List<RecommendTimelocation> tmpRecTimeLocs) {
		RecommendTutor problemRecTutor = null;
		for (RecommendTutor mergedRecTutor : mergedRecommendTutors) {
			int i = 0;
			for (RecommendTimelocation recTimeLoc : mergedRecTutor.getRecommendTimelocations()) {
				// Filter
				List<RecommendTimelocation> sameDayAndTime = tmpRecTimeLocs
						.stream()
						.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
				if (sameDayAndTime.isEmpty()) {
					i++;
				}
			}
			if (i < mergedRecTutor.getMergedClass().getMerMindayperweek()) {
				problemRecTutor = mergedRecTutor;
			}
		}
		return problemRecTutor;
	}
	public static Map<Integer,List<RecommendTutor>> selectTutorForMix(Map<Integer,List<RecommendTutor>> groupMergedClassByStudentStep4Map, Map<Integer,List<RecommendTutor>> groupClassByStudentStep4Map, String flag){
		System.out.println("Flag : "+flag);
		Map<Integer,List<RecommendTutor>> newGroupClassByStudentStep4Map = new HashMap<Integer, List<RecommendTutor>>(groupClassByStudentStep4Map);
		for (Map.Entry<Integer,List<RecommendTutor>> entry : groupMergedClassByStudentStep4Map.entrySet()) {
			System.out.println("Key : "+entry.getKey());
			List<RecommendTutor> recTutorsStep4 = groupClassByStudentStep4Map.get(entry.getKey());
			if (recTutorsStep4 != null) {
				System.out.println("= Exist");
				List<RecommendTutor> passRecTutors = isOverlapTimeForMixClass(recTutorsStep4, entry.getValue());
				System.out.println("passRecTutors.size() : "+passRecTutors.size()+ " recTutorsStep4.size() : "+recTutorsStep4.size() + " entry.getValue().size() : "+entry.getValue().size());
				if (passRecTutors.size() != recTutorsStep4.size() + entry.getValue().size()) {
					// Find option and add to new map
					List<RecommendTutor> problemRecTutors = new ArrayList(recTutorsStep4);
					problemRecTutors.removeAll(passRecTutors);
					newGroupClassByStudentStep4Map.remove(entry.getKey());
					newGroupClassByStudentStep4Map.put(entry.getKey(), passRecTutors);
					List<RecommendTutorOption> options = createOption(problemRecTutors, flag);
					if (!options.isEmpty()) {
						// Get the first option
						RecommendTutorOption firstOption = options.get(0);
						List<RecommendTutor> newRecTutors = firstOption.getRecommendTutors();
						if (firstOption.getNextRecommendTutor()!=null) {
							newRecTutors.add(firstOption.getNextRecommendTutor());
						}
						for (RecommendTutor recTutor : newRecTutors) {
							Integer id = null;
							if (flag.equals(Constants.FLAG_TUTOR)) {
								id = recTutor.getTutor().getTurId();
								if (newGroupClassByStudentStep4Map.containsKey(id)) {
									newGroupClassByStudentStep4Map.get(id).add(recTutor);
								} else {
									List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
									recTutorByTutorId.add(recTutor);
									newGroupClassByStudentStep4Map.put(id, recTutorByTutorId);
								}
							} else {
								if (recTutor.getMergedClass() != null) {
									int id1 = recTutor.getMergedClass().getClazz1().getStudent().getStuId();
									int id2 = recTutor.getMergedClass().getClazz2().getStudent().getStuId();
									if (id1 == id2) {
										if (newGroupClassByStudentStep4Map.containsKey(id1)) {
											newGroupClassByStudentStep4Map.get(id1).add(recTutor);
										} else {
											List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
											recTutorByTutorId.add(recTutor);
											newGroupClassByStudentStep4Map.put(id1, recTutorByTutorId);
										}
									} else {
										if (newGroupClassByStudentStep4Map.containsKey(id1)) {
											newGroupClassByStudentStep4Map.get(id1).add(recTutor);
										} else {
											List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
											recTutorByTutorId.add(recTutor);
											newGroupClassByStudentStep4Map.put(id1, recTutorByTutorId);
										}
										if (newGroupClassByStudentStep4Map.containsKey(id2)) {
											newGroupClassByStudentStep4Map.get(id2).add(recTutor);
										} else {
											List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
											recTutorByTutorId.add(recTutor);
											newGroupClassByStudentStep4Map.put(id2, recTutorByTutorId);
										}
									}
								} else {
									id = recTutor.getClazz().getStudent().getStuId();
									if (newGroupClassByStudentStep4Map.containsKey(id)) {
										newGroupClassByStudentStep4Map.get(id).add(recTutor);
									} else {
										List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
										recTutorByTutorId.add(recTutor);
										newGroupClassByStudentStep4Map.put(id, recTutorByTutorId);
									}
								}
							}

						}

					}
					return selectTutorForMix(groupMergedClassByStudentStep4Map, newGroupClassByStudentStep4Map, flag);
				}
			}
		}

		Map<Integer, List<RecommendTutor>> allRecTutorsMap = new HashMap<>();
		for (Map.Entry<Integer, List<RecommendTutor>> entry : groupMergedClassByStudentStep4Map.entrySet()) {
			allRecTutorsMap.put(entry.getKey(), entry.getValue());

		}
		for (Map.Entry<Integer, List<RecommendTutor>> entry : newGroupClassByStudentStep4Map.entrySet()) {
			if (allRecTutorsMap.containsKey(entry.getKey())) {
				allRecTutorsMap.get(entry.getKey()).addAll(entry.getValue());
			} else {
				allRecTutorsMap.put(entry.getKey(), entry.getValue());
			}
		}
		return allRecTutorsMap;
	}

	public static List<RecommendTutor> isOverlapTimeForMixClass(List<RecommendTutor> recommendTutors, List<RecommendTutor> mergedRecommendTutors){
		System.out.println("--- isOverlapTimeForMixClass");
		List<RecommendTutor> tmpRecTutors = new ArrayList<>(recommendTutors);
		List<RecommendTutor> tmpMergedRecTutors = new ArrayList<>(mergedRecommendTutors);
		// Find duplicated days per tutors or student
		Map<Integer, Integer> numOfClsDup = findNumberOfDuplicatedByDay(recommendTutors);
		Map<Integer, Integer> numOfMergedClsDup = findNumberOfDuplicatedByDay(mergedRecommendTutors);
		List<RecommendTimelocation> tmpRecTimeLocs =  setScheduleForMixClass(new ArrayList<>(), tmpRecTutors, tmpMergedRecTutors, numOfClsDup, numOfMergedClsDup);
		for (RecommendTimelocation recTime : tmpRecTimeLocs) {
			System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd());
		}
		// Convert RecommendTimelocation to RecommendTutor
		List<RecommendTutor> newRecommends = getRecommentTutorByRecLoc(tmpRecTimeLocs);
		// Remove old recommend time location and replace with setSchedule method suggestion.
		for (RecommendTutor recommendTutor : newRecommends) {
			//////////////////// PRINT /////////////////////
			if (recommendTutor.getMergedClass() != null) {
				System.out.println("Rec Tutor / Class : " + recommendTutor.getMergedClass().toString() + "/ Tutor : " + recommendTutor.getTutor().getTurId() + "/ Dayset : " + recommendTutor.getAvailableDays() + "/ Ratio : " + recommendTutor.getAvailableRatio());
			} else {
				System.out.println("Rec Tutor / Class : " + recommendTutor.getClazz().getClsId() + "/ Tutor : " + recommendTutor.getTutor().getTurId() + "/ Dayset : " + recommendTutor.getAvailableDays() + "/ Ratio : " + recommendTutor.getAvailableRatio());

			}
			recommendTutor.setRecommendTimelocations(new ArrayList<>());
		}
		for (RecommendTimelocation recTimeloacation : tmpRecTimeLocs) {
			RecommendTutor recommendTutor = recTimeloacation.getRecommendTutor();
			recommendTutor.addRecommendTimelocation(recTimeloacation);
		}
		System.out.println("Can pass recommendTutors size : "+newRecommends.size());
		return newRecommends;
	}
	public static List<RecommendTimelocation> setScheduleForMixClass(List<RecommendTimelocation> recTimeLocs, List<RecommendTutor> recommendTutors, List<RecommendTutor> mergedRecommendTutors, Map<Integer, Integer> numOfClsDup, Map<Integer, Integer> numOfMergedClsDup){
		// Start
		System.out.println("recommendTutors size : "+recommendTutors.size());
		if (!recommendTutors.isEmpty()) {
			System.out.println("!recommendTutors.isEmpty()");
			List<RecommendTimelocation> tmpRecTimeLocs = new ArrayList<>(recTimeLocs);
			recommendTutors.sort(Comparator.comparing(RecommendTutor::getAvailableRatio)
					.thenComparing(Comparator.comparing(RecommendTutor::getAvailableDays)).reversed());
			RecommendTutor recTutor = recommendTutors.get(0);
			//////////////////// PRINT /////////////////////
			if (recTutor.getMergedClass() != null) {
				System.out.println("Rec Tutor / Class : " + recTutor.getMergedClass().toString() + "/ Tutor : " + recTutor.getTutor().getTurId() + "/ Dayset : " + recTutor.getAvailableDays() + "/ Ratio : " + recTutor.getAvailableRatio());
			} else {
				System.out.println("Rec Tutor / Class : " + recTutor.getClazz().getClsId() + "/ Tutor : " + recTutor.getTutor().getTurId() + "/ Dayset : " + recTutor.getAvailableDays() + "/ Ratio : " + recTutor.getAvailableRatio());

			}
			////////////////////////////////////////////////
			if (recTutor.getAvailableRatio().equals(1D)) {
				System.out.println("Ratio = 1");
				for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()) {

					List<RecommendTimelocation> sameDayAndTime = tmpRecTimeLocs
							.stream()
							.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
					if (!sameDayAndTime.isEmpty()) {
						// Remove the existing match cause of have another class also
						for (RecommendTimelocation recLoc : sameDayAndTime) {
							tmpRecTimeLocs.removeAll(recLoc.getRecommendTutor().getRecommendTimelocations());
						}
						System.out.println("Have problem class");
					} else {
						tmpRecTimeLocs.add(recTimeLoc);

					}
				}
			}else{
				// temp not overlap
				System.out.println("Ratio > 1");
				Map<Integer, Integer> numOfCurClsDup = new HashMap<>();
				List<RecommendTimelocation> tmpNotOverlap = new ArrayList<>();
				// Get order for the specific class
				for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()) {

					List<RecommendTimelocation> sameDayAndTime = tmpRecTimeLocs
							.stream()
							.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
					if (sameDayAndTime.isEmpty()) {
						tmpNotOverlap.add(recTimeLoc);
						Integer dayCount = numOfClsDup.get(recTimeLoc.getRecdDayId());
						numOfCurClsDup.put(recTimeLoc.getRecdDayId(), dayCount);
					}
				}

				Map<Integer, Integer> sortedNumOfCurClsDup = numOfCurClsDup.entrySet().stream()
						.sorted(Map.Entry.comparingByValue())
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
								(oldValue, newValue) -> oldValue, LinkedHashMap::new));
				Set<Integer> sortedDaysCurClsDup = sortedNumOfCurClsDup.keySet();

				// Remove days which dup with merged class and append into list.
				Set<Integer> daysMergedClsDup = numOfMergedClsDup.keySet();
				Set<Integer> intersectDays = new HashSet<>(sortedDaysCurClsDup);
				System.out.println("daysMergedClsDup "+daysMergedClsDup);
				System.out.println("sortedDaysCurClsDup "+intersectDays);
				intersectDays.retainAll(daysMergedClsDup);
				System.out.println("intersectDays "+intersectDays);
				sortedDaysCurClsDup.removeAll(daysMergedClsDup);
				Set<Integer> sortedDaysCurClsDupWoIntersect = new HashSet<>(sortedDaysCurClsDup);
				sortedDaysCurClsDupWoIntersect.addAll(intersectDays);


				// Selected day to set schedule
				int daysPerWeek = 0;
				if (recTutor.getMergedClass() != null) {
					daysPerWeek = recTutor.getMergedClass().getMerMindayperweek();
				} else {
					daysPerWeek = recTutor.getClazz().getClsDayperweek();
				}
				System.out.println("daysPerWeek : "+daysPerWeek);
				int i = 0;
				for (Integer sortedDay : sortedDaysCurClsDupWoIntersect) {
					System.out.println("i : "+i);
					if (i < daysPerWeek) {
						List<RecommendTimelocation> sameDayAndTime = recTutor.getRecommendTimelocations()
								.stream()
								.filter(it -> it.getRecdDayId() == sortedDay).collect(Collectors.toList());
						tmpRecTimeLocs.addAll(sameDayAndTime);
						System.out.println("cloneTmpRecTimeLocs size  : "+tmpRecTimeLocs.size());
					}else {
						break;
					}
					i++;
				}


			}
			// Check if dup with merged class, it cannot be assigned
			RecommendTutor problemRecTutor = checkMergedClassAvaiableTime(mergedRecommendTutors, tmpRecTimeLocs);

			if (problemRecTutor == null) {
				recTimeLocs = tmpRecTimeLocs;
			}
			// Remove checking recommend tutor.
			List<RecommendTutor> remainRecTutors = new ArrayList<>(recommendTutors);
			remainRecTutors.remove(recTutor);
			System.out.println("1. remainRecTutors : "+remainRecTutors.size());
			// set new available ratio
			List<RecommendTutor> problemsRecTutors = new ArrayList<>();
			for (RecommendTutor tmpRectutor : remainRecTutors) {

				// Find same days
				Integer daysCount = 0;
				for (RecommendTimelocation recTimeLoc : tmpRectutor.getRecommendTimelocations()) {
					List<RecommendTimelocation> sameDayAndTime = recTimeLocs
							.stream()
							.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
					if (sameDayAndTime.isEmpty()) {
						daysCount++;
					}
				}
				//System.out.println("Merged Class : "+tmpRectutor.getMergedClass().toString()+ " Day count : "+daysCount + " Days : "+recTutor.getMergedClass().getMerMindayperweek());
				Double ratio = 0D;
				if (tmpRectutor.getMergedClass() != null) {
					ratio = daysCount.doubleValue() / tmpRectutor.getMergedClass().getMerMindayperweek();
				} else {
					ratio = daysCount.doubleValue() / tmpRectutor.getClazz().getClsDayperweek().doubleValue();
				}
				if (ratio < 1D) {
					problemsRecTutors.add(tmpRectutor);
					// Remove duplicated day from temp cause the duplicated one also the problem.
				} else {
					tmpRectutor.setAvailableRatio(ratio);
				}
			}
			// Skip recommend tutor which ratio < 1 or cannot find days match.
			for (RecommendTutor problemTutor : problemsRecTutors) {
				for (RecommendTimelocation recTimeLoc : problemTutor.getRecommendTimelocations()) {
					recTimeLocs = recTimeLocs
							.stream()
							.filter(it -> it.getRecdDayId() != recTimeLoc.getRecdDayId()).collect(Collectors.toList());
				}
			}
			remainRecTutors.removeAll(problemsRecTutors);
			System.out.println("2. remainRecTutors : "+remainRecTutors.size());
			return setScheduleForMixClass(recTimeLocs, remainRecTutors, mergedRecommendTutors, numOfClsDup, numOfMergedClsDup);
		}
		System.out.println("--- END --- ");
		System.out.println("recTimeLocs size : "+recTimeLocs.size());
		for (RecommendTutor recTutor : mergedRecommendTutors) {
			for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()) {

				List<RecommendTimelocation> sameDayAndTime = recTimeLocs
						.stream()
						.filter(it -> it.getRecdDayId() == recTimeLoc.getRecdDayId()).collect(Collectors.toList());
				if (sameDayAndTime.isEmpty()) {
					System.out.println("Day : " + recTimeLoc.getRecdDayId() + " Time : " + recTimeLoc.getRecdStart() + " - " + recTimeLoc.getRecdEnd() + " Reserved Time : "+ recTimeLoc.getRecdStart() + " - " + recTimeLoc.getRecdTravelEnd());

					recTimeLocs.add(recTimeLoc);
				}
			}
		}
		System.out.println("LOOP EXIT recTimeLocs size : "+recTimeLocs.size());
		return recTimeLocs;
	}
	public static boolean isDupTimeByStudentMerged(List<RecommendTutor> recTutors){
		Map<Integer,List<RecommendTutor>> studentMap =  groupByStudentMerged(recTutors);
		List<RecommendTutor> recTutorByStudent = null;
		for (Map.Entry<Integer,List<RecommendTutor>> entry : studentMap.entrySet()) {
			recTutorByStudent = isOverlapTime(entry.getValue());
			if (entry.getValue().size() != recTutorByStudent.size()) {
				return false;
			}
		}
		return true;
	}
	public static boolean isDupTimeByStudent(List<RecommendTutor> recTutors){
		Map<Integer,List<RecommendTutor>> studentMap =  groupByStudent(recTutors);
		List<RecommendTutor> recTutorByStudent = null;
		for (Map.Entry<Integer,List<RecommendTutor>> entry : studentMap.entrySet()) {
			recTutorByStudent = isOverlapTime(entry.getValue());
			if (entry.getValue().size() != recTutorByStudent.size()) {
				return false;
			}
		}
		return true;
	}
	public static boolean isDupTimeByTutor(List<RecommendTutor> recTutors){
		Map<Integer,List<RecommendTutor>> tutorMap =  groupByTutor(recTutors);
		List<RecommendTutor> recTutorByTutor = null;
		for (Map.Entry<Integer,List<RecommendTutor>> entry : tutorMap.entrySet()) {
			recTutorByTutor = isOverlapTime(entry.getValue());
			if (entry.getValue().size() != recTutorByTutor.size()) {
				return false;
			}
		}
		return true;
	}
	public static List<MergedClass> findDupList(List<MergedClass> selectedMergedClass, MergedClass mergedClass){

		boolean isDup = false;
		for (MergedClass selectedClass : selectedMergedClass) {
			if (mergedClass.getClazz1().equals(selectedClass.getClazz1()) ||
					mergedClass.getClazz1().equals(selectedClass.getClazz2()) ||
					mergedClass.getClazz2().equals(selectedClass.getClazz1()) ||
					mergedClass.getClazz2().equals(selectedClass.getClazz2())) {
				isDup = true;
			}
		}
		if (!isDup) {
			List<MergedClass> newMer = new ArrayList<>(selectedMergedClass);
			newMer.add(mergedClass);
			return newMer;
		}

		return selectedMergedClass;
	}
	public static Map<Integer,List<RecommendTutor>> groupByStudentMerged(List<RecommendTutor> recTutors){
		Map<Integer,List<RecommendTutor>> recommendByStudents = new HashMap<>();

		for (RecommendTutor recTutor : recTutors) {
			int studentId1 = recTutor.getMergedClass().getClazz1().getStudent().getStuId();
			int studentId2 = recTutor.getMergedClass().getClazz2().getStudent().getStuId();
			if (studentId1 == studentId2) {
				if (recommendByStudents.containsKey(studentId1)) {
					((List<RecommendTutor>) recommendByStudents.get(studentId1)).add(recTutor);
				}else{
					List<RecommendTutor> recTutorList = new ArrayList<>();
					recTutorList.add(recTutor);
					recommendByStudents.put(studentId1, recTutorList);
				}
			} else {
				// Student 1
				if (recommendByStudents.containsKey(studentId1)) {
					((List<RecommendTutor>) recommendByStudents.get(studentId1)).add(recTutor);
				}else{
					List<RecommendTutor> recTutorList = new ArrayList<>();
					recTutorList.add(recTutor);
					recommendByStudents.put(studentId1, recTutorList);
				}
				// Student 2
				if (recommendByStudents.containsKey(studentId2)) {
					((List<RecommendTutor>) recommendByStudents.get(studentId2)).add(recTutor);
				}else{
					List<RecommendTutor> recTutorList = new ArrayList<>();
					recTutorList.add(recTutor);
					recommendByStudents.put(studentId2, recTutorList);
				}
			}

		}
		return recommendByStudents;

	}
	public static Map<Integer,List<RecommendTutor>> groupByStudent(List<RecommendTutor> recTutors){
		Map<Integer,List<RecommendTutor>> recommendByStudents = new HashMap<>();

		for (RecommendTutor recTutor : recTutors) {
			int studentId = recTutor.getClazz().getStudent().getStuId();
			if (recommendByStudents.containsKey(studentId)) {
				((List<RecommendTutor>) recommendByStudents.get(studentId)).add(recTutor);
			}else{
				List<RecommendTutor> recTutorList = new ArrayList<>();
				recTutorList.add(recTutor);
				recommendByStudents.put(studentId, recTutorList);
			}
		}
		return recommendByStudents;

	}
	public static Map<Integer,List<RecommendTutor>> groupByTutor(List<RecommendTutor> recTutors){
		Map<Integer,List<RecommendTutor>> selectedTutor = new HashMap<>();

		for (RecommendTutor recTutor : recTutors) {
			Integer tutorId = recTutor.getTutor().getTurId();
			if (selectedTutor.containsKey(tutorId)) {
				((List<RecommendTutor>) selectedTutor.get(tutorId)).add(recTutor);
			}else{
				List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
				recTutorByTutorId.add(recTutor);
				selectedTutor.put(tutorId, recTutorByTutorId);
			}
		}
		return selectedTutor;

	}
	public static Map<Integer, Integer> findNumberOfDuplicatedByDay(List<RecommendTutor> recommendTutors){
		Map<Integer, Integer> numberOfDuplcated = new HashMap<>();
		for (RecommendTutor recTutor : recommendTutors){
			for (RecommendTimelocation recTimeLocs : recTutor.getRecommendTimelocations()) {
				if (numberOfDuplcated.containsKey(recTimeLocs.getRecdDayId())) {
					Integer num = numberOfDuplcated.get(recTimeLocs.getRecdDayId());
					numberOfDuplcated.put(recTimeLocs.getRecdDayId(),num+1);
				} else {
					numberOfDuplcated.put(recTimeLocs.getRecdDayId(),1);
				}

			}
		}
		return numberOfDuplcated;
	}
	public static Map<Integer,List<RecommendTutor>> selectTutor(Map<Integer,List<RecommendTutor>> selectedTutor, String flag){

		int classCnt = 0;
		Map<Integer,List<RecommendTutor>> newMap = null;
		for (Map.Entry<Integer,List<RecommendTutor>> entry : selectedTutor.entrySet())
		{
			List<RecommendTutor> recommendTutors = entry.getValue();
			//		System.out.println("Tutor ID : " +entry.getKey() + " / Size : " + entry.getValue().size());
			if (recommendTutors.size() > 1) {
				// All recommend tutors per tutor
				List<RecommendTutor> tmpRecommendTutors = new ArrayList<>(recommendTutors);
				System.out.println("Find Overlap ");
				recommendTutors = isOverlapTime(recommendTutors);
				System.out.println("recommendTutors "+recommendTutors.size() + " tmpRecommendTutors "+tmpRecommendTutors.size());
				// Means that have problem with this tutors
				if (tmpRecommendTutors.size() != recommendTutors.size()) {
					List<RecommendTutor> problemRecTutors = new ArrayList<>(tmpRecommendTutors);
					problemRecTutors.removeAll(recommendTutors);

					List<RecommendTutorOption> options = createOption(problemRecTutors, flag);
					newMap = new HashMap<Integer, List<RecommendTutor>>(selectedTutor);
					newMap.remove(entry.getKey());
					newMap.put(entry.getKey(), recommendTutors);
					// Update new map
					if (!options.isEmpty()) {
						// Get the first option
						RecommendTutorOption firstOption = options.get(0);
						List<RecommendTutor> newRecTutors = firstOption.getRecommendTutors();
						if (firstOption.getNextRecommendTutor() != null) {
							newRecTutors.add(firstOption.getNextRecommendTutor());
						}
						for (RecommendTutor recTutor : newRecTutors) {
							System.out.println("Class "+recTutor.getClazz()+" Rec Tutor : "+recTutor.getTutor().getTurId());
							Integer id = null;
							if (flag.equals(Constants.FLAG_TUTOR)) {
								id = recTutor.getTutor().getTurId();
								if (newMap.containsKey(id)) {
									newMap.get(id).add(recTutor);
								} else {
									List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
									recTutorByTutorId.add(recTutor);
									newMap.put(id, recTutorByTutorId);
								}
							} else {
								if (recTutor.getMergedClass() != null) {
									int id1 = recTutor.getMergedClass().getClazz1().getStudent().getStuId();
									int id2 = recTutor.getMergedClass().getClazz2().getStudent().getStuId();
									if (id1 == id2) {
										if (newMap.containsKey(id1)) {
											newMap.get(id1).add(recTutor);
										} else {
											List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
											recTutorByTutorId.add(recTutor);
											newMap.put(id1, recTutorByTutorId);
										}
									} else {
										if (newMap.containsKey(id1)) {
											newMap.get(id1).add(recTutor);
										} else {
											List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
											recTutorByTutorId.add(recTutor);
											newMap.put(id1, recTutorByTutorId);
										}
										if (newMap.containsKey(id2)) {
											newMap.get(id2).add(recTutor);
										} else {
											List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
											recTutorByTutorId.add(recTutor);
											newMap.put(id2, recTutorByTutorId);
										}
									}
								} else {
									id = recTutor.getClazz().getStudent().getStuId();
									if (newMap.containsKey(id)) {
										newMap.get(id).add(recTutor);
									} else {
										List<RecommendTutor> recTutorByTutorId = new ArrayList<>();
										recTutorByTutorId.add(recTutor);
										newMap.put(id, recTutorByTutorId);
									}
								}
							}

						}
						return selectTutor(newMap, flag);
					}
				}

			}
		}
		return selectedTutor;
	}
	// Same tutor check time overlap or not?
	public static List<RecommendTutor> isOverlapTime(List<RecommendTutor> recommendTutors){

		List<RecommendTutor> checkingRecTutors = new ArrayList<>(recommendTutors);
		// Find duplicated days per tutors or student
		Map<Integer, Integer> numberOfDuplcated = findNumberOfDuplicatedByDay(recommendTutors);
		List<RecommendTimelocation> tmpRecTimeLocs =  setSchedule(new ArrayList<>(),new ArrayList<>(), checkingRecTutors, numberOfDuplcated);

		// Convert RecommendTimelocation to RecommendTutor
		List<RecommendTutor> newRecommends = getRecommentTutorByRecLoc(tmpRecTimeLocs);
		// Remove old recommend time location and replace with setSchedule method suggestion.
		for (RecommendTutor recommendTutor : newRecommends) {
			recommendTutor.setRecommendTimelocations(new ArrayList<>());
		}
		for (RecommendTimelocation recTimeloacation : tmpRecTimeLocs) {
			RecommendTutor recommendTutor = recTimeloacation.getRecommendTutor();
			recommendTutor.addRecommendTimelocation(recTimeloacation);
		}
		//System.out.println("Can pass recommendTutors size : "+newRecommends.size());
		return newRecommends;
	}
	public static List<RecommendTimelocation> setSchedule(List<RecommendTimelocation> tmpRecTimeLocs, List<RecommendTimelocation> problemRecTimeLocs, List<RecommendTutor> recommendTutors, Map<Integer, Integer> numberOfDuplcated){
		// Start
		if (!recommendTutors.isEmpty()) {

			recommendTutors.sort(Comparator.comparing(RecommendTutor::getAvailableRatio)
					.thenComparing(Comparator.comparing(RecommendTutor::getNumOfDays)).reversed());
			RecommendTutor recTutor = recommendTutors.get(0);

			if (recTutor.getAvailableRatio().equals(1D)) {
				// Get only not intersect days

				List<RecommendTimelocation> newRecTimeLocs = new ArrayList<>();

				for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()){
					List<RecommendTimelocation> sameDayAndTime1 = tmpRecTimeLocs
							.stream()
							.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
					List<RecommendTimelocation> sameDayAndTime2 = problemRecTimeLocs
							.stream()
							.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
					if (sameDayAndTime1.isEmpty() && sameDayAndTime2.isEmpty()) {
						newRecTimeLocs.add(recTimeLoc);
					}
				}
				if (newRecTimeLocs.size() >= recTutor.getNumOfDays()) {
					for (RecommendTimelocation recTimeLoc : newRecTimeLocs) {
						tmpRecTimeLocs.add(recTimeLoc);
					}
				} else {
					for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()){
						List<RecommendTimelocation> sameDayAndTime = tmpRecTimeLocs
								.stream()
								.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());

						for (RecommendTimelocation recLoc : sameDayAndTime) {
							problemRecTimeLocs.addAll(recLoc.getRecommendTutor().getRecommendTimelocations());
							tmpRecTimeLocs.removeAll(recLoc.getRecommendTutor().getRecommendTimelocations());
						}
					}

				}
			}else{
				// temp not overlap
				Map<Integer, Integer> numberOfDuplcatedByRecTutor = new HashMap<>();
				List<RecommendTimelocation> tmpNotOverlap = new ArrayList<>();
				for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()) {
					List<RecommendTimelocation> sameDayAndTime = tmpRecTimeLocs
							.stream()
							.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
					if (sameDayAndTime.isEmpty()) {
						tmpNotOverlap.add(recTimeLoc);
						Integer dayCount = numberOfDuplcated.get(recTimeLoc.getRecdDayId());
						numberOfDuplcatedByRecTutor.put(recTimeLoc.getRecdDayId(), dayCount);
					}
				}
				Map<Integer, Integer> sorted = numberOfDuplcatedByRecTutor.entrySet().stream()
						.sorted(Map.Entry.comparingByValue())
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
								(oldValue, newValue) -> oldValue, LinkedHashMap::new));
				int daysPerWeek = 0;
				if (recTutor.getMergedClass() != null) {
					daysPerWeek = recTutor.getMergedClass().getMerMindayperweek();
				} else {
					daysPerWeek = recTutor.getClazz().getClsDayperweek();
				}
				int i = 0;

				for (Map.Entry<Integer, Integer> entry : sorted.entrySet()) {
					if (i < daysPerWeek) {
						List<RecommendTimelocation> sameDayAndTime = recTutor.getRecommendTimelocations()
								.stream()
								.filter(it -> it.getRecdDayId() == entry.getKey()).collect(Collectors.toList());
						tmpRecTimeLocs.addAll(sameDayAndTime);
					}else {
						break;
					}
					i++;
				}
			}
			// Remove checking recommend tutor.
			List<RecommendTutor> remainRecTutors = new ArrayList<>(recommendTutors);
			remainRecTutors.remove(recTutor);
			// set new available ratio
			List<RecommendTutor> problemsRecTutors = new ArrayList<>();
			for (RecommendTutor tmpRectutor : remainRecTutors) {

				// Find same days
				Integer daysCount = 0;
				for (RecommendTimelocation recTimeLoc : tmpRectutor.getRecommendTimelocations()) {
					List<RecommendTimelocation> sameDayAndTime1 = tmpRecTimeLocs
							.stream()
							.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
					List<RecommendTimelocation> sameDayAndTime2 = problemRecTimeLocs
							.stream()
							.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
					if (sameDayAndTime1.isEmpty() && sameDayAndTime2.isEmpty()) {
						daysCount++;
					}
				}
				Double ratio = 0D;
				if (recTutor.getMergedClass() != null) {
					ratio = daysCount.doubleValue() / tmpRectutor.getMergedClass().getMerMindayperweek();
				} else {
					ratio = daysCount.doubleValue() / tmpRectutor.getClazz().getClsDayperweek().doubleValue();
				}
				if (ratio < 1D) {
					problemsRecTutors.add(tmpRectutor);
					// Remove duplicated day from temp cause the duplicated one also the problem.
					for (RecommendTimelocation recTimeLoc : tmpRectutor.getRecommendTimelocations()) {
						List<RecommendTimelocation> sameDayAndTime = tmpRecTimeLocs
								.stream()
								.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
						for (RecommendTimelocation recLoc : sameDayAndTime) {
							problemRecTimeLocs.addAll(recLoc.getRecommendTutor().getRecommendTimelocations());
							tmpRecTimeLocs.removeAll(recLoc.getRecommendTutor().getRecommendTimelocations());
						}
					}
				} else {
					tmpRectutor.setAvailableRatio(ratio);

				}
			}
			// Skip recommend tutor which ratio < 1 or cannot find days match.
			for (RecommendTutor problemTutor : problemsRecTutors) {
				for (RecommendTimelocation recTimeLoc : problemTutor.getRecommendTimelocations()) {
					tmpRecTimeLocs = tmpRecTimeLocs
							.stream()
							.filter(it -> it.getRecdDayId() != recTimeLoc.getRecdDayId()).collect(Collectors.toList());
				}
			}
			remainRecTutors.removeAll(problemsRecTutors);
			return setSchedule(tmpRecTimeLocs, problemRecTimeLocs,remainRecTutors, numberOfDuplcated);
		}
		return tmpRecTimeLocs;

	}
	public static List<RecommendTutor> getRecommentTutorByRecLoc(List<RecommendTimelocation> recommendTimelocations){
		Set<RecommendTutor> recTutors = recommendTimelocations.stream().map(RecommendTimelocation::getRecommendTutor).collect(Collectors.toSet());
		return new ArrayList<>(recTutors);
	}
	public static List<RecommendTutorOption> createOption(List<RecommendTutor> problemRecTutors, String flag){
		// Create possible option
		System.out.println("List Size : "+problemRecTutors.size());
		// set ratio
		for (RecommendTutor tmpRectutor : problemRecTutors) {
			Double ratio = 0D;
			if (tmpRectutor.getMergedClass() != null) {
				ratio = tmpRectutor.getAvailableDays() / tmpRectutor.getMergedClass().getMerMindayperweek();
			} else {
				ratio = tmpRectutor.getAvailableDays() / tmpRectutor.getClazz().getClsDayperweek().doubleValue();
			}
			tmpRectutor.setAvailableRatio(ratio);
		}

		List<RecommendTutorOption> recOptions = new ArrayList<>();
		List<RecommendTutor> others = new ArrayList<>(problemRecTutors);

		for (RecommendTutor problem : others) {
			List<RecommendTutor> recommendTutors = new ArrayList<>();
			RecommendTutor nextTutor = null;
			// Loop others
			for (RecommendTutor otherRecTutor : others) {
				if (problem.getClazz() != null && problem.getClazz().getClsId() == otherRecTutor.getClazz().getClsId()) {

					int nextRecOrder = problem.getRecOrder()+1;
					nextTutor = problem.getClazz().getRecommendTutors().stream().filter(it -> it.getRecOrder().intValue() == nextRecOrder)
							.findAny()
							.orElse(null);
				}else if (problem.getMergedClass() != null && problem.getMergedClass().getClazz1().getClsId() == otherRecTutor.getMergedClass().getClazz1().getClsId()
						&& problem.getMergedClass().getClazz2().getClsId() == otherRecTutor.getMergedClass().getClazz2().getClsId()) {
					nextTutor = problem.getMergedClass().getRecommendTutors().stream().filter(it -> it.getRecOrder().equals(problem.getRecOrder()+1))
							.findAny()
							.orElse(null);
				}
				else {
					recommendTutors.add(otherRecTutor);
				}
			}
			RecommendTutorOption recOption = null;
			if (flag.equals(Constants.FLAG_TUTOR)) {
				recOption = new RecommendTutorOption(recommendTutors, nextTutor);
			}else {
				recOption = new RecommendTutorOption(recommendTutors, null);
			}
			recOptions.add(recOption);

		}
		int i = 1;
		for (RecommendTutorOption option : recOptions){
			List<RecommendTutor> tmpList = option.getRecommendTutors();

			Integer sumTutorSpec = 0;
			Double sumTravelTime = 0D;
			Integer sumClsId = 0;
			for (RecommendTutor rec : tmpList) {
				sumTutorSpec += rec.getRecTutorOrder();
				sumTravelTime += rec.getMinTravelTime();
				if (rec.getMergedClass() != null) {
					sumClsId += rec.getMergedClass().getClazz1().getClsId() + rec.getMergedClass().getClazz2().getClsId();
				} else {
					sumClsId += rec.getClazz().getClsId();
				}

			}
			if (option.getNextRecommendTutor() != null) {
				sumTutorSpec += option.getNextRecommendTutor().getRecTutorOrder();
				sumTravelTime += option.getNextRecommendTutor().getMinTravelTime();
				if (option.getNextRecommendTutor().getMergedClass() != null) {
					sumClsId += option.getNextRecommendTutor().getMergedClass().getClazz1().getClsId() + option.getNextRecommendTutor().getMergedClass().getClazz2().getClsId();
				} else {
					sumClsId += option.getNextRecommendTutor().getClazz().getClsId();
				}
			}
			List<RecommendTutor> noProblemTutor = isOverlapTime(tmpList);
			option.setSumTutorSpec(sumTutorSpec);
			option.setSumTravelTime(sumTravelTime);
			option.setNoProblemSize(noProblemTutor.size());
			option.setSumClassId(sumClsId);
			System.out.println("Option "+i+" / Recommend size : "+option.getRecommendSize()+" No problem size : "+option.getNoProblemSize()+" Sum tutor spec : "+option.getSumTutorSpec()+" Sum travel time : "+option.getSumTravelTime());

			for (RecommendTutor recTutor : tmpList) {
				System.out.println("Class : "+recTutor.getClazz().getClsId() + " Tutor ID : "+recTutor.getTutor().getTurId());
				for (RecommendTimelocation recLoc : recTutor.getRecommendTimelocations()) {
					System.out.println("- Days : "+recLoc.getRecdDayId());
				}
			}
			if (option.getNextRecommendTutor() != null) {
				System.out.println("Class : "+option.getNextRecommendTutor().getClazz().getClsId() + " Tutor ID : "+option.getNextRecommendTutor().getTutor().getTurId());
			}
			i++;
		}

		// Select the best option
		/*
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getRecommendSize).reversed()
				.thenComparing(Comparator.comparing(RecommendTutorOption::getNoProblemSize)).reversed()
				.thenComparing(Comparator.comparing(RecommendTutorOption::getSumTutorSpec))
				.thenComparing(Comparator.comparing(RecommendTutorOption::getSumTravelTime)));*/
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getSumTravelTime));
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getSumTutorSpec));
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getNoProblemSize).reversed());
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getRecommendSize).reversed());



		System.out.println("** SELECT ***");
		RecommendTutorOption selected = recOptions.get(0);
		for (RecommendTutor recTutor : selected.getRecommendTutors()) {
			System.out.println("Class : "+recTutor.getClazz().getClsId() + " Tutor ID : "+recTutor.getTutor().getTurId());
			for (RecommendTimelocation recLoc : recTutor.getRecommendTimelocations()) {
				System.out.println("- Days : "+recLoc.getRecdDayId());
			}
		}
		if (selected.getNextRecommendTutor() != null) {
			System.out.println("Class : "+selected.getNextRecommendTutor().getClazz().getClsId() + " Tutor ID : "+selected.getNextRecommendTutor().getTutor().getTurId());
		}
		return recOptions;
	}


}

