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
import sun.jvm.hotspot.debugger.win32.coff.COFFLineNumber;
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
		System.out.println("Merged size : "+mergedClasses.size());
		System.out.println("Before Order");
		for (MergedClass mergedClass : mergedClasses) {
			System.out.println("Class : "+mergedClass.toString() + " Type : "+mergedClass.getMerType());
		}

		mergedClasses.sort(Comparator.comparing(MergedClass::getMerMindayperweek).reversed());
		mergedClasses.sort(Comparator.comparing(MergedClass::getMerMergeratio));
		mergedClasses.sort(Comparator.comparing(MergedClass::getMerSamesubFlag).reversed());
		mergedClasses.sort(Comparator.comparing(MergedClass::getMerSamesubgFlag).reversed());
		mergedClasses.sort(Comparator.comparing(MergedClass::getMerSameparentFlag).reversed());
		mergedClasses.sort(Comparator.comparing(MergedClass::getMerSamestuFlag).reversed());
		mergedClasses.sort(Comparator.comparing(MergedClass::getMerType).reversed());

		System.out.println("After Order");
		for (MergedClass mergedClass : mergedClasses) {
			System.out.println("Class : "+mergedClass.toString() + " Type : "+mergedClass.getMerType());
		}

		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("2.3 Merged Class : Matching Tutor");
		// Step 2.3 : Matching tutor
		List<MergedClass> selectedMergedClasses = new ArrayList<>();
		while(!mergedClasses.isEmpty()) {
			MergedClass tmpMergedClass = mergedClasses.get(0);

			// If cannot find recommend tutor, pass class to normal case.
			MergedClass retTmpMergedClass = mergedClassService.tutorMatchingForMergedClass(tmpMergedClass);
			if (retTmpMergedClass.getRecommendTutors().isEmpty()) {
				System.out.println("* Cannot Found Class : "+retTmpMergedClass.toString() + " -> Move classes to normal case");
				mergedClasses.remove(tmpMergedClass);
			} else {
				selectedMergedClasses.add(retTmpMergedClass);
				// remove class
				classes.remove(retTmpMergedClass.getClazz1());
				classes.remove(retTmpMergedClass.getClazz2());
				System.out.println("* Found Class : "+retTmpMergedClass.toString());
				mergedClasses = findDupList(mergedClasses, tmpMergedClass);
			}

		}
		mergedClasses = selectedMergedClasses;
		// remove merged class if cannot find recommend tutor
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("2.4 Merged Class - Order Tutor by merged class specification");
		TutorMatchingDao tutorMatchingDao = new TutorMatchingDao();
		List<TutorMatching> tutorMatchings = tutorMatchingDao.list();
		for (MergedClass tmpMergedClass : mergedClasses) {
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
					System.out.println("Day : " + timeLoc.getRecdDayId() + " Time : " + timeLoc.getRecdStart() + " - " + timeLoc.getRecdEnd() + " Reserved Time : "+ timeLoc.getRecdTravelStart() + " - " + timeLoc.getRecdTravelEnd());
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
				tmpMergedClass.getRecommendTutors().get(i).setRecOrder(i + 1L);
			}

		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		for (MergedClass tmpMergedClass : mergedClasses) {
			System.out.println("------------ Class " + tmpMergedClass.toString() + " ------------");
			for (RecommendTutor recTutor : tmpMergedClass.getRecommendTutors()) {
				System.out.println("Tutor Name : " + recTutor.getTutor().getTurFirstname() + " Student ID : "+recTutor.getMergedClass().getClazz1().getStudent().getStuId()+ ","+recTutor.getMergedClass().getClazz2().getStudent().getStuId());

				for (RecommendTimelocation timeLoc : recTutor.getRecommendTimelocations()) {
					System.out.println("Day : " + timeLoc.getRecdDayId() + " Time : " + timeLoc.getRecdStart() + " - " + timeLoc.getRecdEnd() + " Reserved Time : "+ timeLoc.getRecdStart() + " - " + timeLoc.getRecdTravelEnd());
				}
			}

		}

		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("2.5 Merged Class -  Check time overlap of student");
		// Get the first recommend tutors
		RecommendTutorService recTutorService = new RecommendTutorService();
		List<RecommendTutor> mergedTutors = new ArrayList<>();
		for (MergedClass tmpMergedClass : mergedClasses) {
			if (!tmpMergedClass.getRecommendTutors().isEmpty()) {
				mergedTutors.add(tmpMergedClass.getRecommendTutors().get(0));
			}
		}
		//List<RecommendTutor> mergedTutors = mergedClasses.stream().filter(mergedClass -> !mergedClass.getRecommendTutors().isEmpty()).map(mergedClass -> mergedClass.getRecommendTutors().get(0)).collect(Collectors.toList());
		System.out.println("mergedTutors size : "+mergedTutors.size());
		Map<Long,List<RecommendTutor>> groupMergedClassByStudent = groupByStudentMerged(mergedTutors);
		Map<Long,List<RecommendTutor>> notOverlapMergedStudent = selectTutor(groupMergedClassByStudent, new HashMap<>(), Constants.FLAG_STUDENT);

		System.out.println("groupMergedClassByStudent size : "+groupMergedClassByStudent.size());
		System.out.println("notOverlapMergedStudent size : "+notOverlapMergedStudent.size());
		System.out.println("\r\n** Class that can match student **");
		List<Class> matchMergedClass = new ArrayList<>();
		List<RecommendTutor> recTutors = new ArrayList<>();
		for (Map.Entry<Long,List<RecommendTutor>> entry : notOverlapMergedStudent.entrySet())
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
		System.out.println("2.6 Merged Class -  Check time overlap of tutor");

		Map<Long,List<RecommendTutor>> groupMergedClassByTutor = groupByTutor(recTutorsConvertFromMergedStuMap);
		Map<Long,List<RecommendTutor>> notOverlapMergedTutor = selectTutor(groupMergedClassByTutor, new HashMap<>() ,Constants.FLAG_TUTOR);

		System.out.println("\r\n** Class that can match tutor **");
		Set<Class> matchClasses = new HashSet<>();
		List<MergedClass> finalMergedClasses = new ArrayList<>();
		for (Map.Entry<Long,List<RecommendTutor>> entry : notOverlapMergedTutor.entrySet())
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
		TutorDao tutorDao = new TutorDao();
		for (Class cls : classes) {
			List<RecommendTutor> recommendTutors = new ArrayList<>();
			cls.setRecommendTutors(recommendTutors);
			Long subjectID = cls.getSubject().getSubjId();
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
				cls.getRecommendTutors().get(i).setRecOrder(i + 1L);
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
					System.out.println("Day : " + timeLoc.getRecdDayId() + " Time : " + timeLoc.getRecdStart() + " - " + timeLoc.getRecdEnd() + " Ava Time : " + timeLoc.getRecdAvaStart() + " - " + timeLoc.getRecdAvaEnd());
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
		//fistRecTutors.addAll(finalMergedRecTutors);
		/*
		Map<Long,List<RecommendTutor>> groupClassByTutor = groupByTutor(fistRecTutors);
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("3.5 Select the first recommend tutor and check time overlap by tutor");
		Map<Long,List<RecommendTutor>> notOverlap = selectTutor(groupClassByTutor, Constants.FLAG_TUTOR);

		System.out.println("\r\n** Class that can match tutor **");
		for (Map.Entry<Long,List<RecommendTutor>> entry : notOverlap.entrySet())
		{

			System.out.println("Tutor ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Subject : " + rec.getClazz().getSubject().getSubjName() + " : " + rec.getClazz().getSubjectGroupDetail().getGrpdName()  + " / Student ID : "+rec.getClazz().getStudent().getStuId() + " / Days : "+rec.getClazz().getClsDayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				matchClasses.add(rec.getClazz());
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()){
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd() + " / Location : "+recTime.getRecdLoc());
				}
			}
		}
		System.out.println("\r\n** Class that not match any tutors **");
		classes.removeAll(matchClasses);
		for (Class cls : classes) {
			System.out.println("- Class ID : " + cls.getClsId());
		}
		// Add merged c
		// Convert map to rec tutor list
		List<RecommendTutor> recTutorsConvertFromTutorMap = recTutorService.convertMapToRecTutors(notOverlap);
		//Map<Long,List<RecommendTutor>> groupClassByStudent = groupByStudentMerged(recTutorsConvertFromTutorMap);

		Map<Long,List<RecommendTutor>> groupClassByStudent = groupByStudent(recTutorsConvertFromTutorMap);
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("3.6 Select the first recommend tutor and check time overlap by student");
		Map<Long,List<RecommendTutor>> notOverlapStudent = selectTutor(groupClassByStudent, Constants.FLAG_STUDENT);

		System.out.println("\r\n** Class that can match tutor **");
		List<Class> matchClassStudent = new ArrayList<>();
		for (Map.Entry<Long,List<RecommendTutor>> entry : notOverlapStudent.entrySet())
		{

			System.out.println("Student ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Subject : " + rec.getClazz().getSubject().getSubjName() + " : " + rec.getClazz().getSubjectGroupDetail().getGrpdName()  + " / Tutor ID : "+rec.getTutor().getTurId() + " / Days : "+rec.getClazz().getClsDayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				matchClassStudent.add(rec.getClazz());
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()){
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd() + " / Location : "+recTime.getRecdLoc());
				}
			}
		}
		System.out.println("\r\n** Class that not match any tutors **");
		classes.removeAll(matchClassStudent);
		for (Class cls : classes) {
			System.out.println("- Class ID : " + cls.getClsId());
		}

*/

		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("3. Check overlap merged class and class");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		// Find Recommend Tutors duplicated time slot for mix classes case (Student)
		//////////////// REMOVE //////////////////
	//	mergedRecTutorsStep19 = new ArrayList<>();
		// Convert map to rec tutors list
	//	List<RecommendTutor> recTutorsConvertFromStuMap = recTutorService.convertMapToRecTutors(notOverlapStudent);


		Map<Long,List<RecommendTutor>> groupMergedClassByStudentStep4Map = groupByStudentMerged(finalMergedRecTutors);
		//Map<Long,List<RecommendTutor>> groupClassByStudentStep4Map = groupByStudent(recTutorsConvertFromStuMap);
		Map<Long,List<RecommendTutor>> groupClassByStudentStep4Map = groupByStudent(fistRecTutors);
		//Map<Long,List<RecommendTutor>> allByStudentMap = selectTutorForMix(groupMergedClassByStudentStep4Map, groupClassByStudentStep4Map, Constants.FLAG_STUDENT);
		System.out.println("Normal : "+groupClassByStudentStep4Map.size());
		System.out.println("Merged : "+groupMergedClassByStudentStep4Map.size());
		Map<Long,List<RecommendTutor>> allByStudentMap = selectTutor( groupClassByStudentStep4Map, groupMergedClassByStudentStep4Map, Constants.FLAG_STUDENT);
		recTutors = new ArrayList<>();
		System.out.println("+++ Normal Case +++");
		for (Map.Entry<Long,List<RecommendTutor>> entry : allByStudentMap.entrySet())
		{
			System.out.println("Student ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				recTutors.add(rec);
				if (rec.getMergedClass() != null) {
					System.out.println("- Class ID : " + rec.getMergedClass().toString() + " / Tutor ID : " + rec.getTutor().getTurId() + " / Days : "+rec.getMergedClass().getMerMindayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				} else {
					System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Subject : " + rec.getClazz().getSubject().getSubjName() + " : " + rec.getClazz().getSubjectGroupDetail().getGrpdName()  + " / Student ID : "+rec.getClazz().getStudent().getStuId() + " / Days : "+rec.getClazz().getClsDayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				}
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()) {
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : " + recTime.getRecdStart() + " - " + recTime.getRecdEnd() + " / Reserved time : " + recTime.getRecdTravelStart() + " - " + recTime.getRecdTravelEnd() + " / Location : " + recTime.getRecdLoc());
				}
			}
		}
		System.out.println("+++ Merged Case +++");
		for (Map.Entry<Long,List<RecommendTutor>> entry : groupMergedClassByStudentStep4Map.entrySet())
		{
			System.out.println("Student ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()){
				recTutors.add(rec);
				if (rec.getMergedClass() != null) {
					System.out.println("- Class ID : " + rec.getMergedClass().toString() + " / Tutor ID : " + rec.getTutor().getTurId() + " / Days : "+rec.getMergedClass().getMerMindayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				} else {
					System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Subject : " + rec.getClazz().getSubject().getSubjName() + " : " + rec.getClazz().getSubjectGroupDetail().getGrpdName()  + " / Student ID : "+rec.getClazz().getStudent().getStuId() + " / Days : "+rec.getClazz().getClsDayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				}
				for(RecommendTimelocation recTime : rec.getRecommendTimelocations()) {
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : " + recTime.getRecdStart() + " - " + recTime.getRecdEnd() + " / Reserved time : " + recTime.getRecdTravelStart() + " - " + recTime.getRecdTravelEnd() + " / Location : " + recTime.getRecdLoc());
				}
			}
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		// Find Recommend Tutors duplicated time slot for mix classes case (Tutor)
		// Convert map to rec tutors list
		List<RecommendTutor> recTutorsConvertFromMixStuMap = recTutorService.convertMapToRecTutors(allByStudentMap);

		MixClass recTutorsMixClass = splitMergedAndNormalClass(recTutorsConvertFromMixStuMap);
		List<RecommendTutor> rectutorsNormal = recTutorsMixClass.getRecTutorsNormal();

		Map<Long,List<RecommendTutor>> groupMergedClassByTutorStep4Map = groupByTutor(finalMergedRecTutors);
		Map<Long,List<RecommendTutor>> groupClassByTutorStep4Map = groupByTutor(rectutorsNormal);
		System.out.println("Normal : "+groupClassByTutorStep4Map.size());
		System.out.println("Merged : "+groupMergedClassByTutorStep4Map.size());
		//Map<Long,List<RecommendTutor>> allByTutorMap = selectTutorForMix(groupMergedClassByTutorStep4Map, groupClassByTutorStep4Map, Constants.FLAG_TUTOR);
		Map<Long,List<RecommendTutor>> allByTutorMap = selectTutor(groupClassByTutorStep4Map, groupMergedClassByTutorStep4Map, Constants.FLAG_TUTOR);
		System.out.println("+++ Normal Case +++");
		for (Map.Entry<Long,List<RecommendTutor>> entry : allByTutorMap.entrySet())
		{
			System.out.println("Tutor ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()) {
				if (rec.getMergedClass() != null) {
					System.out.println("- Class ID : " + rec.getMergedClass().toString() + " / Student ID : " + rec.getMergedClass().getClazz1().getStudent().getStuId() + "," + rec.getMergedClass().getClazz2().getStudent().getStuId() + " / Days : " + rec.getMergedClass().getMerMindayperweek() + " / Rec Order : " + rec.getRecOrder() + " / Score : " + rec.getRecTutorOrder());
				} else {
					System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Subject : " + rec.getClazz().getSubject().getSubjName() + " : " + rec.getClazz().getSubjectGroupDetail().getGrpdName()  + " / Student ID : "+rec.getClazz().getStudent().getStuId() + " / Days : "+rec.getClazz().getClsDayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				}
				for (RecommendTimelocation recTime : rec.getRecommendTimelocations()) {
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd() + " / Location : "+recTime.getRecdLoc());
				}
			}
		}
		System.out.println("+++ Merged Case +++");
		for (Map.Entry<Long,List<RecommendTutor>> entry : groupMergedClassByTutorStep4Map.entrySet())
		{
			System.out.println("Tutor ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()) {
				if (rec.getMergedClass() != null) {
					System.out.println("- Class ID : " + rec.getMergedClass().toString() + " / Student ID : " + rec.getMergedClass().getClazz1().getStudent().getStuId() + "," + rec.getMergedClass().getClazz2().getStudent().getStuId() + " / Days : " + rec.getMergedClass().getMerMindayperweek() + " / Rec Order : " + rec.getRecOrder() + " / Score : " + rec.getRecTutorOrder());
				} else {
					System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Subject : " + rec.getClazz().getSubject().getSubjName() + " : " + rec.getClazz().getSubjectGroupDetail().getGrpdName()  + " / Student ID : "+rec.getClazz().getStudent().getStuId() + " / Days : "+rec.getClazz().getClsDayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				}
				for (RecommendTimelocation recTime : rec.getRecommendTimelocations()) {
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd() + " / Location : "+recTime.getRecdLoc());
				}
			}
		}

		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("4. Group by tutor");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		List<Class> matchClass = new ArrayList<>();
		for (Map.Entry<Long,List<RecommendTutor>> entry : groupMergedClassByTutorStep4Map.entrySet())
		{
			if (allByTutorMap.containsKey(entry.getKey())) {
				List<RecommendTutor> existing = allByTutorMap.get(entry.getKey());
				existing.addAll(entry.getValue());
				allByTutorMap.replace(entry.getKey(), existing);
			} else {
				allByTutorMap.put(entry.getKey(),entry.getValue());
			}
		}
		Map<Long,List<RecommendTutor>> result = allByTutorMap.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		for (Map.Entry<Long,List<RecommendTutor>> entry : result.entrySet())
		{
			Map<Long, Long> dupDays = findNumberOfDuplicatedByDay(entry.getValue());
			for (RecommendTutor rec : entry.getValue()) {
				Double ratio = Double.valueOf(rec.getRecommendTimelocations().size()) / Double.valueOf(rec.getNumOfDays());
				rec.setAvailableRatio(ratio);
			}
			List<RecommendTutor> newRecTutors = new ArrayList<>(entry.getValue());
			List<RecommendTutor> recTutorRatioMoreThan1 = newRecTutors.stream()
					.filter(it -> it.getAvailableRatio() > 1).collect(Collectors.toList());
			newRecTutors.removeAll(recTutorRatioMoreThan1);
			recTutorRatioMoreThan1.sort(Comparator.comparing(RecommendTutor::getAvailableRatio)
					.thenComparing(Comparator.comparing(RecommendTutor::getAvailableDays)).reversed());
			Map<Long, Long> usedDays = new HashMap<>();
			for (RecommendTutor rec : recTutorRatioMoreThan1) {
				int i = 0;
				for (RecommendTimelocation recTimeLoc : rec.getRecommendTimelocations()) {
					Long dupDay = dupDays.get(recTimeLoc.getRecdDayId());
					recTimeLoc.setNumOfDup(dupDay);
				}
				rec.getRecommendTimelocations().sort(Comparator.comparing(RecommendTimelocation::getNumOfDup));
				List<RecommendTimelocation> newRecTimeLocs = new ArrayList<>();
				while (i < rec.getRecommendTimelocations().size()) {
					if (newRecTimeLocs.size() == rec.getNumOfDays()) {
						break;
					} else {
						if (!usedDays.containsKey(rec.getRecommendTimelocations().get(i).getRecdDayId())) {
							newRecTimeLocs.add(rec.getRecommendTimelocations().get(i));
							usedDays.put(rec.getRecommendTimelocations().get(i).getRecdDayId(), 1L);
						}
					}
					i++;
				}
				rec.setRecommendTimelocations(newRecTimeLocs);
			}
			newRecTutors.addAll(recTutorRatioMoreThan1);
			System.out.println("Tutor ID : "+entry.getKey());
			for (RecommendTutor rec : entry.getValue()) {
				if (rec.getMergedClass() != null) {
					matchClass.add(rec.getMergedClass().getClazz1());
					matchClass.add(rec.getMergedClass().getClazz2());
					System.out.println("- Class ID : " + rec.getMergedClass().toString() + " / Student ID : " + rec.getMergedClass().getClazz1().getStudent().getStuId() + "," + rec.getMergedClass().getClazz2().getStudent().getStuId() + " / Days : " + rec.getMergedClass().getMerMindayperweek() + " / Rec Order : " + rec.getRecOrder() + " / Score : " + rec.getRecTutorOrder());
				} else {
					matchClass.add(rec.getClazz());
					System.out.println("- Class ID : " + rec.getClazz().getClsId() + " / Subject : " + rec.getClazz().getSubject().getSubjName() + " : " + rec.getClazz().getSubjectGroupDetail().getGrpdName()  + " / Student ID : "+rec.getClazz().getStudent().getStuId() + " / Days : "+rec.getClazz().getClsDayperweek() + " / Rec Order : "+rec.getRecOrder() + " / Score : "+rec.getRecTutorOrder());
				}
				for (RecommendTimelocation recTime : rec.getRecommendTimelocations()) {
					System.out.println("--- Day : " + recTime.getRecdDayId() + " / Class time : "+recTime.getRecdStart() + " - "+recTime.getRecdEnd()+ " / Reserved time : "+recTime.getRecdTravelStart() + " - "+recTime.getRecdTravelEnd() + " / Location : "+recTime.getRecdLoc());
				}
			}
		}

		System.out.println("\r\n** Class that not match any tutors **");
		classes.removeAll(matchClass);
		for (Class cls : classes) {
			System.out.println("- Class ID : " + cls.getClsId());
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");


		// Create recommend tutors
		/*
		RecommendStatusDao recommendStatusDao = new RecommendStatusDao();
		RecommendStatus statusOpen = recommendStatusDao.findByStatusId(Constants.RECSTATUS_WAIT);
		System.out.println("Status :"+statusOpen.getRecStatusName());
		List<RecommendTutor> finalRecTutors = recTutorService.convertMapToRecTutors(allByTutorMap);
		finalRecTutors = assignStatus(finalRecTutors, statusOpen);
		MixClass finalMixClass = splitMergedAndNormalClass(finalRecTutors);
		List<RecommendTutor> finalRecTutorsMergedClass = finalMixClass.getRecTutorsMerged();
		List<RecommendTutor> finalRecTutorsClass = finalMixClass.getRecTutorsNormal();
		List<MergedClass> finalMeredClasses = mergedClassService.convertRecTutorToMergedClass(finalRecTutorsMergedClass);
		RecommendTutorDao recommendTutorDao = new RecommendTutorDao();
		recommendTutorDao.createList(finalRecTutorsClass);
		MergedClassDao mergedClassDao = new MergedClassDao();
		mergedClassDao.createList(finalMeredClasses);

*/
		System.exit(0);
	}

	public static List<RecommendTutor> assignStatus(List<RecommendTutor> recTutors, RecommendStatus recStatus){
		for (RecommendTutor recTutor : recTutors) {
			recTutor.setRecommendStatus(recStatus);
		}
		return recTutors;
	}

	public static MixClass splitMergedAndNormalClass(List<RecommendTutor> recTutors){

		List<RecommendTutor> recTutorsNormal = new ArrayList<>();
		List<RecommendTutor> recTutorsMerged = new ArrayList<>();

		for (RecommendTutor rectutor : recTutors) {
			//System.out.println("Status :"+rectutor.getRecommendStatus().getRecStatusName());
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
	/*
	public static Map<Long,List<RecommendTutor>> selectTutorForMix(Map<Long,List<RecommendTutor>> groupMergedClassByStudentStep4Map, Map<Long,List<RecommendTutor>> groupClassByStudentStep4Map, String flag){
		System.out.println("Flag : "+flag);
		Map<Long,List<RecommendTutor>> newGroupClassByStudentStep4Map = new HashMap<Long, List<RecommendTutor>>(groupClassByStudentStep4Map);
		for (Map.Entry<Long,List<RecommendTutor>> entry : groupMergedClassByStudentStep4Map.entrySet()) {
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
							Long id = null;
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
									long id1 = recTutor.getMergedClass().getClazz1().getStudent().getStuId();
									long id2 = recTutor.getMergedClass().getClazz2().getStudent().getStuId();
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

		Map<Long, List<RecommendTutor>> allRecTutorsMap = new HashMap<>();
		for (Map.Entry<Long, List<RecommendTutor>> entry : groupMergedClassByStudentStep4Map.entrySet()) {
			allRecTutorsMap.put(entry.getKey(), entry.getValue());

		}
		for (Map.Entry<Long, List<RecommendTutor>> entry : newGroupClassByStudentStep4Map.entrySet()) {
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
		Map<Long, Long> numOfClsDup = findNumberOfDuplicatedByDay(recommendTutors);
		Map<Long, Long> numOfMergedClsDup = findNumberOfDuplicatedByDay(mergedRecommendTutors);
		List<RecommendTimelocation> tmpRecTimeLocs =  setScheduleForMixClass(new ArrayList<>(), tmpRecTutors, tmpMergedRecTutors, numOfClsDup, numOfMergedClsDup);
		RecommendTutorService recommendTutorService = new RecommendTutorService();
		Map<Long, Long> classPerDays = new HashMap<>();
		classPerDays.put(1L, 0L);
		classPerDays.put(2L, 0L);
		classPerDays.put(3L, 0L);
		classPerDays.put(4L, 0L);
		classPerDays.put(5L, 0L);
		classPerDays.put(6L, 0L);
		classPerDays.put(7L, 0L);
	//	List<RecommendTimelocation> tmpRecTimeLocs = recommendTutorService.setSchedule(new ArrayList<>(),new ArrayList<>(), checkingRecTutors, numOfClsDup,classPerDays);
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
	public static List<RecommendTimelocation> setScheduleForMixClass(List<RecommendTimelocation> recTimeLocs, List<RecommendTutor> recommendTutors, List<RecommendTutor> mergedRecommendTutors, Map<Long, Long> numOfClsDup, Map<Long, Long> numOfMergedClsDup){
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
				Map<Long, Long> numOfCurClsDup = new HashMap<>();
				List<RecommendTimelocation> tmpNotOverlap = new ArrayList<>();
				// Get order for the specific class
				for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()) {

					List<RecommendTimelocation> sameDayAndTime = tmpRecTimeLocs
							.stream()
							.filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
					if (sameDayAndTime.isEmpty()) {
						tmpNotOverlap.add(recTimeLoc);
						Long dayCount = numOfClsDup.get(recTimeLoc.getRecdDayId());
						numOfCurClsDup.put(recTimeLoc.getRecdDayId(), dayCount);
					}
				}

				Map<Long, Long> sortedNumOfCurClsDup = numOfCurClsDup.entrySet().stream()
						.sorted(Map.Entry.comparingByValue())
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
								(oldValue, newValue) -> oldValue, LinkedHashMap::new));
				Set<Long> sortedDaysCurClsDup = sortedNumOfCurClsDup.keySet();

				// Remove days which dup with merged class and append into list.
				Set<Long> daysMergedClsDup = numOfMergedClsDup.keySet();
				Set<Long> intersectDays = new HashSet<>(sortedDaysCurClsDup);
				System.out.println("daysMergedClsDup "+daysMergedClsDup);
				System.out.println("sortedDaysCurClsDup "+intersectDays);
				intersectDays.retainAll(daysMergedClsDup);
				System.out.println("intersectDays "+intersectDays);
				sortedDaysCurClsDup.removeAll(daysMergedClsDup);
				Set<Long> sortedDaysCurClsDupWoIntersect = new HashSet<>(sortedDaysCurClsDup);
				sortedDaysCurClsDupWoIntersect.addAll(intersectDays);


				// Selected day to set schedule
				long daysPerWeek = 0;
				if (recTutor.getMergedClass() != null) {
					daysPerWeek = recTutor.getMergedClass().getMerMindayperweek();
				} else {
					daysPerWeek = recTutor.getClazz().getClsDayperweek();
				}
				System.out.println("daysPerWeek : "+daysPerWeek);
				int i = 0;
				for (Long sortedDay : sortedDaysCurClsDupWoIntersect) {
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
				long daysCount = 0;
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
					ratio = Double.valueOf(daysCount) / tmpRectutor.getMergedClass().getMerMindayperweek();
				} else {
					ratio = Double.valueOf(daysCount) / tmpRectutor.getClazz().getClsDayperweek().doubleValue();
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
					System.out.println("Day : " + recTimeLoc.getRecdDayId() + " Time : " + recTimeLoc.getRecdStart() + " - " + recTimeLoc.getRecdEnd() + " Reserved Time : "+ recTimeLoc.getRecdTravelStart() + " - " + recTimeLoc.getRecdTravelEnd());

					recTimeLocs.add(recTimeLoc);
				}
			}
		}
		System.out.println("LOOP EXIT recTimeLocs size : "+recTimeLocs.size());
		return recTimeLocs;
	}

*/

	public static List<MergedClass> findDupList(List<MergedClass> selectedMergedClass, MergedClass mergedClass){
		List<MergedClass> dupMergedClass = new ArrayList<>();
		for (MergedClass selectedClass : selectedMergedClass) {
			if (mergedClass.getClazz1().equals(selectedClass.getClazz1()) ||
					mergedClass.getClazz1().equals(selectedClass.getClazz2()) ||
					mergedClass.getClazz2().equals(selectedClass.getClazz1()) ||
					mergedClass.getClazz2().equals(selectedClass.getClazz2())) {
				dupMergedClass.add(selectedClass);
			}
		}
		System.out.println("selectedMergedClass size : "+selectedMergedClass.size());
		System.out.println("Duplicate size : "+dupMergedClass.size());
/*
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
*/
		selectedMergedClass.removeAll(dupMergedClass);
		return selectedMergedClass;
	}
	public static Map<Long,List<RecommendTutor>> groupByStudentMerged(List<RecommendTutor> recTutors){
		Map<Long,List<RecommendTutor>> recommendByStudents = new HashMap<>();

		for (RecommendTutor recTutor : recTutors) {
			long studentId1 = recTutor.getMergedClass().getClazz1().getStudent().getStuId();
			long studentId2 = recTutor.getMergedClass().getClazz2().getStudent().getStuId();
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
	public static Map<Long,List<RecommendTutor>> groupByStudent(List<RecommendTutor> recTutors){
		Map<Long,List<RecommendTutor>> recommendByStudents = new HashMap<>();

		for (RecommendTutor recTutor : recTutors) {
			long studentId = recTutor.getClazz().getStudent().getStuId();
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
	public static Map<Long,List<RecommendTutor>> groupByTutor(List<RecommendTutor> recTutors){
		Map<Long,List<RecommendTutor>> selectedTutor = new HashMap<>();

		for (RecommendTutor recTutor : recTutors) {
			Long tutorId = recTutor.getTutor().getTurId();
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
	public static Map<Long, Long> findNumberOfDuplicatedByDay(List<RecommendTutor> recommendTutors){
		Map<Long, Long> numberOfDuplcated = new HashMap<>();
		for (RecommendTutor recTutor : recommendTutors){
			for (RecommendTimelocation recTimeLocs : recTutor.getRecommendTimelocations()) {
				if (numberOfDuplcated.containsKey(recTimeLocs.getRecdDayId())) {
					long num = numberOfDuplcated.get(recTimeLocs.getRecdDayId());
					numberOfDuplcated.put(recTimeLocs.getRecdDayId(),num+1);
				} else {
					numberOfDuplcated.put(recTimeLocs.getRecdDayId(),1L);
				}

			}
		}
		return numberOfDuplcated;
	}
	public static Map<Long,List<RecommendTutor>> selectTutor(Map<Long,List<RecommendTutor>> selectedTutor,Map<Long,List<RecommendTutor>> groupMergedClassMap, String flag){

		Map<Long,List<RecommendTutor>> newMap = null;
		for (Map.Entry<Long,List<RecommendTutor>> entry : selectedTutor.entrySet())
		{
			List<RecommendTutor> recommendTutors = entry.getValue();
			List<RecommendTutor> mergedRecommendTutors = (groupMergedClassMap.get(entry.getKey()) == null) ? new ArrayList<>() : groupMergedClassMap.get(entry.getKey());

			//		System.out.println("Tutor ID : " +entry.getKey() + " / Size : " + entry.getValue().size());
			if (recommendTutors.size() > 1) {
				// All recommend tutors per tutor
				List<RecommendTutor> tmpRecommendTutors = new ArrayList<>(recommendTutors);
				System.out.println("Find Overlap ");
				recommendTutors = isOverlapTime(recommendTutors, mergedRecommendTutors);
				System.out.println("recommendTutors "+recommendTutors.size() + " tmpRecommendTutors "+tmpRecommendTutors.size());
				// Means that have problem with this tutors
				if (tmpRecommendTutors.size() != recommendTutors.size()) {
					List<RecommendTutor> problemRecTutors = new ArrayList<>(tmpRecommendTutors);
					problemRecTutors.removeAll(recommendTutors);

					List<RecommendTutorOption> options = createOption(problemRecTutors, mergedRecommendTutors, flag);
					newMap = new HashMap<Long, List<RecommendTutor>>(selectedTutor);
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
						newMap = adjustMap(newMap, newRecTutors, flag);
						return selectTutor(newMap, groupMergedClassMap,flag);
					}
				}

			}
		}
		return selectedTutor;
	}

	public static Map<Long,List<RecommendTutor>> adjustMap(Map<Long,List<RecommendTutor>> selectedTutor, List<RecommendTutor> newRecTutors, String flag){
		Map<Long,List<RecommendTutor>> newMap = new HashMap<>(selectedTutor);
		for (RecommendTutor recTutor : newRecTutors) {
			Long id = null;
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
					long id1 = recTutor.getMergedClass().getClazz1().getStudent().getStuId();
					long id2 = recTutor.getMergedClass().getClazz2().getStudent().getStuId();
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
		return newMap;
	}


	// Same tutor check time overlap or not?
	public static List<RecommendTutor> isOverlapTime(List<RecommendTutor> recommendTutors, List<RecommendTutor> mergedRecommendTutors){
		List<RecommendTutor> checkingRecTutors = new ArrayList<>(recommendTutors);
		// Find duplicated days per tutors or student
		Map<Long, Long> numberOfDuplcated = findNumberOfDuplicatedByDay(recommendTutors);
		for (RecommendTutor recommendTutor : recommendTutors) {
			if (recommendTutor.getMergedClass()!= null && recommendTutor.getMergedClass().getMerType().equals(Constants.MERGEDTYPE_PARTIAL)) {
				recommendTutor.setTmpFlag(true);
				if (recommendTutor.getMergedClass().getRemainDays1() != 0) {
					RecommendTutor tmpRecTutor = new RecommendTutor();
					tmpRecTutor.setTmpFlag(true);
					tmpRecTutor.setOwnerRecTutor(recommendTutor);
					tmpRecTutor.setTutor(recommendTutor.getTutor());
					tmpRecTutor.setNumOfDays(recommendTutor.getMergedClass().getRemainDays1().longValue());
					List<RecommendTimelocation> tmpRecTime1 = recommendTutor.getRecommendTimelocations().stream()
							.filter(it -> it.getRecdClassStart().equals(Constants.TIMELOCTYPE_ONLYCLASS1)).collect(Collectors.toList());
					tmpRecTutor.setRecommendTimelocations(tmpRecTime1);
					tmpRecTutor.setAvailableDays(Double.valueOf(tmpRecTime1.size()));
					tmpRecTutor.setAvailableRatio(Double.valueOf(tmpRecTime1.size())/Double.valueOf(recommendTutor.getMergedClass().getRemainDays1()));
					Class clz = recommendTutor.getMergedClass().getClazz1();
					clz.setClsDayperweek(recommendTutor.getMergedClass().getRemainDays1().longValue());
					tmpRecTutor.setClazz(clz);
					checkingRecTutors.add(tmpRecTutor);
				}
				if (recommendTutor.getMergedClass().getRemainDays2() != 0) {
					RecommendTutor tmpRecTutor = new RecommendTutor();
					tmpRecTutor.setTmpFlag(true);
					tmpRecTutor.setOwnerRecTutor(recommendTutor);
					tmpRecTutor.setTutor(recommendTutor.getTutor());
					tmpRecTutor.setNumOfDays(recommendTutor.getMergedClass().getRemainDays2().longValue());
					List<RecommendTimelocation> tmpRecTime2 = recommendTutor.getRecommendTimelocations().stream()
							.filter(it -> it.getRecdClassStart().equals(Constants.TIMELOCTYPE_ONLYCLASS2)).collect(Collectors.toList());
					tmpRecTutor.setRecommendTimelocations(tmpRecTime2);
					tmpRecTutor.setAvailableDays(Double.valueOf(tmpRecTime2.size()));
					tmpRecTutor.setAvailableRatio(Double.valueOf(tmpRecTime2.size())/Double.valueOf(recommendTutor.getMergedClass().getRemainDays2()));
					Class clz = recommendTutor.getMergedClass().getClazz2();
					clz.setClsDayperweek(recommendTutor.getMergedClass().getRemainDays2().longValue());
					tmpRecTutor.setClazz(clz);
					checkingRecTutors.add(tmpRecTutor);
				}
			}
		}

		RecommendTutorService recommendTutorService = new RecommendTutorService();
		Map<Long, Long> classPerDays = new HashMap<>();
		classPerDays.put(1L, 0L);
		classPerDays.put(2L, 0L);
		classPerDays.put(3L, 0L);
		classPerDays.put(4L, 0L);
		classPerDays.put(5L, 0L);
		classPerDays.put(6L, 0L);
		classPerDays.put(7L, 0L);
		List<RecommendTimelocation> tmpRecTimeLocs = recommendTutorService.setSchedule(new ArrayList<>(),new ArrayList<>(), checkingRecTutors, mergedRecommendTutors, numberOfDuplcated,classPerDays);
		//List<RecommendTimelocation> tmpRecTimeLocs =  setSchedule(new ArrayList<>(),new ArrayList<>(), checkingRecTutors, numberOfDuplcated);
		tmpRecTimeLocs = changeRectutorFromTmp(tmpRecTimeLocs);

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
		return newRecommends;
	}

	public static List<RecommendTimelocation> changeRectutorFromTmp(List<RecommendTimelocation> recommendTimelocations) {
		List<RecommendTimelocation> tmpRecTimeLoc =  new ArrayList<>();
		for (RecommendTimelocation recTimeLoc : recommendTimelocations) {
			if (recTimeLoc.getRecommendTutor().isTmpFlag()) {
				recTimeLoc.setRecommendTutor(recTimeLoc.getRecommendTutor().getOwnerRecTutor());
			}
			tmpRecTimeLoc.add(recTimeLoc);
		}
		return tmpRecTimeLoc;
	}

	public static List<RecommendTutor> getRecommentTutorByRecLoc(List<RecommendTimelocation> recommendTimelocations){

		Set<RecommendTutor> recTutors = recommendTimelocations.stream().map(RecommendTimelocation::getRecommendTutor).collect(Collectors.toSet());
		return new ArrayList<>(recTutors);
	}
	public static List<RecommendTutorOption> createOption(List<RecommendTutor> problemRecTutors, List<RecommendTutor> mergedRecommendTutors, String flag){
		// Create possible option
		System.out.println(" Create option list Size : "+problemRecTutors.size() + " Flag : "+flag);
		// set ratio
		for (RecommendTutor tmpRectutor : problemRecTutors) {
			Double ratio = tmpRectutor.getAvailableDays() / tmpRectutor.getNumOfDays().doubleValue();
			/*
			if (tmpRectutor.getMergedClass() != null) {
				ratio = tmpRectutor.getAvailableDays() / tmpRectutor.getMergedClass().getMerMindayperweek();
			} else {
				ratio = tmpRectutor.getAvailableDays() / tmpRectutor.getClazz().getClsDayperweek().doubleValue();
			}*/
			tmpRectutor.setAvailableRatio(ratio);
		}

		List<RecommendTutorOption> recOptions = new ArrayList<>();
		List<RecommendTutor> others = new ArrayList<>(problemRecTutors);

		for (RecommendTutor problem : others) {
			List<RecommendTutor> recommendTutors = new ArrayList<>();
			RecommendTutor nextTutor = null;
			// Loop others
			for (RecommendTutor otherRecTutor : others) {
				//System.out.println("problem :: Class id : "+problem.getClazz().getClsId() + " Tutor ID : "+problem.getTutor().getTurId());
				if (problem.getClazz() != null && problem.getClazz().getClsId() == otherRecTutor.getClazz().getClsId()) {
					long nextRecOrder = problem.getRecOrder()+1;
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
			Long minRecOrder = 0L;
			Long maxRecOrder = 0L;
			List<RecommendTutor> tmpList = option.getRecommendTutors();
			int sumTutorSpec = 0;
			Double sumTravelTime = 0D;
			Long sumClsId = 0L;
			Double preAverageLat = 0D;
			Double preAverageLong = 0D;
			Double averageLat = 0D;
			Double averageLong = 0D;
			Double averageDistance = 0D;
			for (int j = 0; j<tmpList.size() ; j++) {
				RecommendTutor rec = tmpList.get(j);

				if (j > 0) {
					RecommendTutor previousRec = tmpList.get(j-1);
					for (RecommendTimelocation recTimeLoc : previousRec.getRecommendTimelocations()) {
						preAverageLat += recTimeLoc.getRecdLat();
						preAverageLong += recTimeLoc.getRecdLong();
					}
					preAverageLat = preAverageLat/ Double.valueOf(previousRec.getRecommendTimelocations().size());
					preAverageLong = preAverageLong/ Double.valueOf(previousRec.getRecommendTimelocations().size());
				}
				for (RecommendTimelocation recTimeLoc : rec.getRecommendTimelocations()) {
					averageLat += recTimeLoc.getRecdLat();
					averageLong += recTimeLoc.getRecdLong();
				}
				averageLat = averageLat/ Double.valueOf(rec.getRecommendTimelocations().size());
				averageLong = averageLong/ Double.valueOf(rec.getRecommendTimelocations().size());
				averageDistance += GoogleMapUtils.distFrom(preAverageLat,preAverageLong,averageLat,averageLong);
				sumTutorSpec += rec.getRecTutorOrder();
				sumTravelTime += rec.getMinTravelTime();
				if (minRecOrder > rec.getRecTutorOrder()) {
					minRecOrder = rec.getRecTutorOrder();
				}
				if (maxRecOrder < rec.getRecTutorOrder()) {
					maxRecOrder = rec.getRecTutorOrder();
				}
				if (rec.getMergedClass() != null) {
					sumClsId += rec.getMergedClass().getClazz1().getClsId() + rec.getMergedClass().getClazz2().getClsId();
				} else {
					sumClsId += rec.getClazz().getClsId();
				}

			}
			if (option.getNextRecommendTutor() != null) {

				sumTutorSpec += option.getNextRecommendTutor().getRecTutorOrder();
				sumTravelTime += option.getNextRecommendTutor().getMinTravelTime();
				if (minRecOrder > option.getNextRecommendTutor().getRecTutorOrder()) {
					minRecOrder = option.getNextRecommendTutor().getRecTutorOrder();
				}
				if (maxRecOrder < option.getNextRecommendTutor().getRecTutorOrder()) {
					maxRecOrder = option.getNextRecommendTutor().getRecTutorOrder();
				}
				if (option.getNextRecommendTutor().getMergedClass() != null) {
					sumClsId += option.getNextRecommendTutor().getMergedClass().getClazz1().getClsId() + option.getNextRecommendTutor().getMergedClass().getClazz2().getClsId();
				} else {
					sumClsId += option.getNextRecommendTutor().getClazz().getClsId();
				}
			}
			List<RecommendTutor> noProblemTutor = isOverlapTime(tmpList, mergedRecommendTutors);
			option.setDiffRecTutorOrder(maxRecOrder-minRecOrder);
			option.setSumTutorSpec(sumTutorSpec);
			option.setSumTravelTime(sumTravelTime);
			option.setNoProblemSize(noProblemTutor.size());
			option.setSumClassId(sumClsId);
			option.setAverageDistance(averageDistance / Double.valueOf(tmpList.size()));
			System.out.println("Option "+i+" / Recommend size : "+option.getRecommendSize()+" No problem size : "+option.getNoProblemSize()+" Sum tutor spec : "+option.getSumTutorSpec()+" Sum travel time : "+option.getSumTravelTime());
			for (RecommendTutor recTutor : option.getRecommendTutors()) {
				System.out.println("- Class : "+recTutor.getClazz().getClsId() + " Tutor ID : "+recTutor.getTutor().getTurId());
			}
			if (option.getNextRecommendTutor() != null) {
				System.out.println("- Class : "+option.getNextRecommendTutor().getClazz().getClsId() + " Tutor ID : "+option.getNextRecommendTutor().getTutor().getTurId());
			}
			i++;
		}

		// Select the best option
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getDiffRecTutorOrder));
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getSumTravelTime));
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getAverageDistance));
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getSumTutorSpec));
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getNoProblemSize).reversed());
		recOptions.sort(Comparator.comparing(RecommendTutorOption::getRecommendSize).reversed());



		System.out.println("** SELECT OPTION ***");
		RecommendTutorOption selected = recOptions.get(0);
		for (RecommendTutor recTutor : selected.getRecommendTutors()) {
			System.out.println("Class : "+recTutor.getClazz().getClsId() + " Tutor ID : "+recTutor.getTutor().getTurId());
		}
		if (selected.getNextRecommendTutor() != null) {
			System.out.println("Class : "+selected.getNextRecommendTutor().getClazz().getClsId() + " Tutor ID : "+selected.getNextRecommendTutor().getTutor().getTurId());
		}
		return recOptions;
	}


}

