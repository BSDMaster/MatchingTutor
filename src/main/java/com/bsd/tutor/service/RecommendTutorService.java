package com.bsd.tutor.service;

import com.bsd.tutor.model.*;
import com.bsd.tutor.model.Class;
import com.bsd.tutor.utils.Constants;
import com.bsd.tutor.utils.DateTimeUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kewalins on 2/26/2018 AD.
 */
@Service
public class RecommendTutorService {

    public List<RecommendTutor> convertMapToRecTutors(Map<Long, List<RecommendTutor>> map) {

        List<RecommendTutor> recTutors = new ArrayList<>();
        for (Map.Entry<Long,List<RecommendTutor>> entry : map.entrySet())
        {

            for (RecommendTutor recTutorFromMap : entry.getValue()) {
                boolean isExist = false;
                for (RecommendTutor existRecTutor : recTutors) {
                    if (recTutorFromMap.getClazz() != null && existRecTutor.getClazz() != null
                            && recTutorFromMap.getClazz().getClsId() == existRecTutor.getClazz().getClsId()
                            && recTutorFromMap.getTutor().getTurId() == existRecTutor.getTutor().getTurId()) {
                        isExist = true;
                    } else if (recTutorFromMap.getMergedClass() != null && existRecTutor.getMergedClass() != null
                            && recTutorFromMap.getMergedClass().getClazz1().getClsId() == existRecTutor.getMergedClass().getClazz1().getClsId()
                            && recTutorFromMap.getMergedClass().getClazz2().getClsId() == existRecTutor.getMergedClass().getClazz2().getClsId()
                            && recTutorFromMap.getTutor().getTurId() == existRecTutor.getTutor().getTurId()) {
                        isExist = true;
                    }
                }
                if (!isExist) {
                    recTutors.add(recTutorFromMap);
                }
            }


        }
        return recTutors;
    }

    public Double findRatioForPartialMerged(RecommendTutor recTutor, List<RecommendTimelocation> tmpRecTimeLocs, List<RecommendTimelocation> problemRecTimeLocs) {
        List<RecommendTimelocation> intersectPartials = new ArrayList<>();
        List<RecommendTimelocation> recTutorTimeLocFull = recTutor.getRecommendTimelocations().stream()
                .filter(it -> it.getRecdClassStart().equals(Constants.TIMELOCTYPE_STARTFROM1) || it.getRecdClassStart().equals(Constants.TIMELOCTYPE_STARTFROM2)).collect(Collectors.toList());
        for (RecommendTimelocation recTimeLoc : recTutorTimeLocFull) {
            List<RecommendTimelocation> intersectTmpTimeLocEachDays = tmpRecTimeLocs.stream()
                    .filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
            List<RecommendTimelocation> intersectProbTimeLocEachDays = problemRecTimeLocs.stream()
                    .filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
            if (!intersectTmpTimeLocEachDays.isEmpty() || !intersectProbTimeLocEachDays.isEmpty()) {
                return 0D;
            }
        }

        List<RecommendTimelocation> recTutorTimeLocPart1 = recTutor.getRecommendTimelocations().stream()
                .filter(it -> it.getRecdClassStart().equals(Constants.TIMELOCTYPE_ONLYCLASS1)).collect(Collectors.toList());
        List<RecommendTimelocation> recTutorTimeLocPart2 = recTutor.getRecommendTimelocations().stream()
                .filter(it -> it.getRecdClassStart().equals(Constants.TIMELOCTYPE_ONLYCLASS2)).collect(Collectors.toList());
        List<RecommendTimelocation> tmpPartialMergedTimes1 = new ArrayList<>(recTutorTimeLocPart1);
        List<RecommendTimelocation> tmpPartialMergedTimes2 = new ArrayList<>(recTutorTimeLocPart2);
        for (RecommendTimelocation recTutorPart1TimeLoc : recTutorTimeLocPart1) {
            List<RecommendTimelocation> intersectPartialTimeLocEachDays = recTutorTimeLocPart2.stream()
                    .filter(it -> recTutorPart1TimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
            intersectPartials.addAll(intersectPartialTimeLocEachDays);
            tmpPartialMergedTimes1.removeAll(intersectPartialTimeLocEachDays);
            tmpPartialMergedTimes2.removeAll(intersectPartialTimeLocEachDays);
        }

        Double availableDays = Double.valueOf(recTutorTimeLocFull.size() + tmpPartialMergedTimes1.size() + tmpPartialMergedTimes2.size() + intersectPartials.size());
        return availableDays / recTutorTimeLocFull.size() + recTutor.getMergedClass().getRemainDays1() + recTutor.getMergedClass().getRemainDays2();
    }

    public void arrangeDays(List<RecommendTutor> recTutors){
        // Get current class per day

        // group rectimeloc by day
        Map<Long, List<RecommendTimelocation>> groupByDays = new HashMap<>();
        for (RecommendTutor recTutor : recTutors) {
            for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()) {
                Long dayId = recTimeLoc.getRecdDayId();
                if (groupByDays.containsKey(dayId)) {
                    groupByDays.get(dayId).add(recTimeLoc);
                } else {
                    List<RecommendTimelocation> tmpRecTimeLocs = new ArrayList<>();
                    tmpRecTimeLocs.add(recTimeLoc);
                    groupByDays.put(recTimeLoc.getRecdDayId(), tmpRecTimeLocs);
                }
            }
        }
        // Check by day
        for (Map.Entry<Long, List<RecommendTimelocation>> entry : groupByDays.entrySet())
        {
            Long day = entry.getKey();
            List<RecommendTimelocation> tmpRecTimeLocsByDay = entry.getValue();
            if (tmpRecTimeLocsByDay.size() > Constants.CLASSPERDAY) {

            }
        }

    }
    public static List<RecommendTimelocation> setSchedule(List<RecommendTimelocation> tmpRecTimeLocs, List<RecommendTimelocation> problemRecTimeLocs, List<RecommendTutor> recommendTutors,List<RecommendTutor>  mergedRecommendTutors,  Map<Long, Long> numberOfDuplcated, Map<Long, Long> classPerDays){
        // Start
        if (!recommendTutors.isEmpty()) {
           // System.out.println("Start Remain Size : "+recommendTutors.size());
            recommendTutors.sort(Comparator.comparing(RecommendTutor::getNumOfDays).reversed());
            recommendTutors.sort(Comparator.comparing(RecommendTutor::getAvailableRatio));

            RecommendTutor recTutor = recommendTutors.get(0);
            if (recTutor.getClazz()!=null) {
                System.out.println("----- Rec tutor : Class ID : " + recTutor.getClazz().getClsId() + " Tutor : " + recTutor.getTutor().getTurId() + " Ratio : " + recTutor.getAvailableRatio() + " Days : " + recTutor.getNumOfDays());
            } else {
                System.out.println("----- Rec tutor : Class ID : " + recTutor.getMergedClass().toString() + " Tutor : " + recTutor.getTutor().getTurId() + " Ratio : " + recTutor.getAvailableRatio() + " Days : " + recTutor.getNumOfDays());

            }
            Map<Long, Long> tmpClassPerDays = new HashMap<>(classPerDays);
            if (recTutor.getAvailableRatio() >= 1D) {
                Double minRatio = 100D;
                        // Get only not intersect days with tmp and problem time loc.
                List<RecommendTimelocation> newRecTimeLocs = new ArrayList<>();
                List<RecommendTimelocation> removeTimeLocs = new ArrayList<>();
                List<RecommendTimelocation> addTimeLocs = new ArrayList<>();
                for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()){
                    System.out.println("- day : "+recTimeLoc.getRecdDayId()+" / Time : "+recTimeLoc.getRecdStart()+ "-"+recTimeLoc.getRecdEnd()+" / Ava Time : "+recTimeLoc.getRecdAvaStart()+ "-"+recTimeLoc.getRecdAvaEnd()+ " / Location : "+recTimeLoc.getRecdLoc());
                    long tobeClassPerDay = tmpClassPerDays.get(recTimeLoc.getRecdDayId());
                    List<RecommendTimelocation> sameRecTimeLocWtTmp = tmpRecTimeLocs.stream()
                            .filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
                    List<RecommendTimelocation> sameRecTimeLocWtProb = problemRecTimeLocs.stream()
                            .filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
                    List<RecommendTimelocation> sameRecTimeLocWtMerged = new ArrayList<>();
                    List<RecommendTimelocation> tmp = new ArrayList<>(tmpRecTimeLocs);
                    tmp.add(recTimeLoc);
                    minRatio = findMinMergedRatio(mergedRecommendTutors,tmp);
                    if (minRatio >= 1D) {
                        for (RecommendTutor mergedRecTutor : mergedRecommendTutors) {
                            sameRecTimeLocWtMerged.addAll(mergedRecTutor.getRecommendTimelocations().stream()
                                    .filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList()));
                        }
                        System.out.println("Same tmp size : " + sameRecTimeLocWtTmp.size());
                        System.out.println("Same prob size : " + sameRecTimeLocWtProb.size());
                        System.out.println("Same merged size : " + sameRecTimeLocWtMerged.size());
                        int exist = 0;
                        int dupOfMerged = 0;
                        for (RecommendTimelocation existing : sameRecTimeLocWtTmp) {
                            if (existing.getRecdClassStart() == Constants.TIMELOCTYPE_STARTFROM1 || existing.getRecdClassStart() == Constants.TIMELOCTYPE_STARTFROM2) {
                                exist += 2;
                            } else {
                                exist += 1;
                            }
                        }
                        for (RecommendTimelocation existing : sameRecTimeLocWtMerged) {
                            dupOfMerged += 2;
                        }
                        exist += dupOfMerged;
                        if (recTimeLoc.getRecdClassStart() == Constants.TIMELOCTYPE_STARTFROM1 || recTimeLoc.getRecdClassStart() == Constants.TIMELOCTYPE_STARTFROM2) {
                            tobeClassPerDay = tobeClassPerDay + exist + 2;
                        } else {
                            tobeClassPerDay = tobeClassPerDay + exist + 1;
                        }
                        if (tobeClassPerDay <= Constants.CLASSPERDAY) {
                            long numOfDup = numberOfDuplcated.get(recTimeLoc.getRecdDayId()) + dupOfMerged;
                            System.out.println(" Day : " + recTimeLoc.getRecdDayId() + " / numOfDup : " + numOfDup);
                            recTimeLoc.setNumOfDup(numOfDup);
                            if (sameRecTimeLocWtTmp.isEmpty() && sameRecTimeLocWtProb.isEmpty()) {
                                newRecTimeLocs.add(recTimeLoc);
                            } else if (!sameRecTimeLocWtTmp.isEmpty()) {
                                RecommendTimeLocationOption option = manageTimeWithinDay(sameRecTimeLocWtTmp, recTimeLoc);
                                if (option != null) {
                                    System.out.println("Select OPtion : " + option.getOptionId());
                                    RecommendTimelocation tmpTimeLoc = option.getRecommendTimelocations().get(option.getOptionId());
                                    List<RecommendTimelocation> newRecTimeLocWtTmp = new ArrayList<>(option.getRecommendTimelocations());
                                    newRecTimeLocWtTmp.remove(tmpTimeLoc);
                                    newRecTimeLocs.add(tmpTimeLoc);
                                    removeTimeLocs.addAll(sameRecTimeLocWtTmp);
                                    addTimeLocs.addAll(newRecTimeLocWtTmp);
                                }
                            }
                        }
                    }
                }
                newRecTimeLocs.sort(Comparator.comparing(RecommendTimelocation::getNumOfDup));
                newRecTimeLocs.sort(Comparator.comparing(RecommendTimelocation::getRecdTraveltime));
                // if not found intersect, can add.
              //  System.out.println("Rec Days : "+newRecTimeLocs.size()+ " Required Days : "+recTutor.getNumOfDays());
                if (newRecTimeLocs.size() >= recTutor.getNumOfDays() && minRatio >= 1D) {
                    int i = 0;
                    while (i < recTutor.getNumOfDays()) {
                        RecommendTimelocation recTimeLoc = newRecTimeLocs.get(i);
                        long tobeClassPerDay = classPerDays.get(recTimeLoc.getRecdDayId());
                       // System.out.println(" 1. To be Day : "+recTimeLoc.getRecdDayId()+ " Class per days : "+tobeClassPerDay);

                        if (recTimeLoc.getRecdClassStart() == Constants.TIMELOCTYPE_STARTFROM1 || recTimeLoc.getRecdClassStart() == Constants.TIMELOCTYPE_STARTFROM2) {
                            tobeClassPerDay += 2;
                        } else {
                            tobeClassPerDay += 1;
                        }
                        tmpClassPerDays.replace(recTimeLoc.getRecdDayId(), tobeClassPerDay);
                       // System.out.println(" 2. To be Day : "+recTimeLoc.getRecdDayId()+ " Class per days : "+tmpClassPerDays.get(recTimeLoc.getRecdDayId()));
                        tmpRecTimeLocs.add(recTimeLoc);
                        i++;
                    }
                    tmpRecTimeLocs.removeAll(removeTimeLocs);
                    tmpRecTimeLocs.addAll(addTimeLocs);
                    System.out.println("Tmp size : "+tmpRecTimeLocs.size());

                } else {
                    // if fount intersect, add into problem list and remove from tmp
                    List<RecommendTimelocation>[] probs = problemRectutor(tmpRecTimeLocs, problemRecTimeLocs, recTutor);
                    tmpRecTimeLocs = probs[0];
                    problemRecTimeLocs = probs[1];
                }
            }
            // Remove checking recommend tutor.

            List<RecommendTutor> remainRecTutors = new ArrayList<>(recommendTutors);
           // System.out.println("1 End Remain Size : "+remainRecTutors.size());
            remainRecTutors.remove(recTutor);
            // set new available ratio for other remain rectutors.
            List<RecommendTutor> problemsRecTutors = new ArrayList<>();
          //  System.out.println("2 End Remain Size : "+remainRecTutors.size());
            for (RecommendTutor tmpRectutor : remainRecTutors) {
                // Find same days
                long daysCount = 0;
                for (RecommendTimelocation recTimeLoc : tmpRectutor.getRecommendTimelocations()) {
                    List<RecommendTimelocation> sameRecTimeLocWtTmp = tmpRecTimeLocs.stream()
                            .filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
                    List<RecommendTimelocation> sameRecTimeLocWtProb = problemRecTimeLocs.stream()
                            .filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
                    System.out.println("Day Id : "+recTimeLoc.getRecdDayId());
                    System.out.println("Same tmp size : "+sameRecTimeLocWtTmp.size());
                    System.out.println("Same prob size : "+sameRecTimeLocWtProb.size());
                    if (sameRecTimeLocWtProb.isEmpty()) {
                        long tobeClassPerDay = tmpClassPerDays.get(recTimeLoc.getRecdDayId());
                        if (sameRecTimeLocWtTmp.size() < Constants.CLASSPERDAY)
                            if (recTimeLoc.getRecdClassStart() == Constants.TIMELOCTYPE_STARTFROM1 || recTimeLoc.getRecdClassStart() == Constants.TIMELOCTYPE_STARTFROM2) {
                                tobeClassPerDay += 2;
                            } else {
                                tobeClassPerDay += 1;
                            }
                        if (tobeClassPerDay <= Constants.CLASSPERDAY) {
                            daysCount++;
                        }
                    }
                }

                Double ratio = Double.valueOf(daysCount) / tmpRectutor.getNumOfDays();
                System.out.println("Ratio : "+ratio+" New day count : "+daysCount);
                if (ratio < 1D) {
                    // for partial merged
                    // 1. Remove all time loc that already added into tmp list
                    // 2. Add time loc of specific class to problem list
                    // 3. Remove other classes that related to the merged class (all) from remain rectutor
                    MergedClassService mergedClassService = new MergedClassService();
                    List<RecommendTutor> problems = mergedClassService.findRecTutorByClsId(tmpRecTimeLocs, recTutor);
                    if (!problems.isEmpty()) {
                        problemsRecTutors.addAll(problems);
                    } else {
                        problemsRecTutors.add(tmpRectutor);
                    }

                    List<RecommendTimelocation>[] probs = problemRectutor(tmpRecTimeLocs, problemRecTimeLocs, recTutor);
                    tmpRecTimeLocs = probs[0];
                    problemRecTimeLocs = probs[1];

                } else {
                    tmpRectutor.setAvailableRatio(ratio);

                }
            }
            remainRecTutors.removeAll(problemsRecTutors);
            System.out.println("------------------------------------");
            return setSchedule(tmpRecTimeLocs, problemRecTimeLocs,remainRecTutors, mergedRecommendTutors, numberOfDuplcated, classPerDays);
        }
        return tmpRecTimeLocs;

    }

    public static Double findMinMergedRatio(List<RecommendTutor>  mergedRecommendTutors, List<RecommendTimelocation> tmpRecTimeLocs){
        Double minRatio = 100D;
        for (RecommendTutor recTutor : mergedRecommendTutors) {
            Double dayCount = 0D;
            for (RecommendTimelocation recTimeLoc : recTutor.getRecommendTimelocations()) {
                List<RecommendTimelocation> sameRecTimeLocWtTmp = tmpRecTimeLocs.stream()
                        .filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
                if (!sameRecTimeLocWtTmp.isEmpty()) {
                    dayCount += 1D;
                }
            }

            Double ratio = dayCount / recTutor.getNumOfDays();
            if (ratio < minRatio) {
                minRatio = ratio;
            }
        }
        return minRatio;

    }

    public static RecommendTimeLocationOption manageTimeWithinDay(List<RecommendTimelocation> sameRecTimeLocWtTmp, RecommendTimelocation newRecTimeLoc){
        int numOfOptions = sameRecTimeLocWtTmp.size() + 1;
        List<RecommendTimeLocationOption> recTimeOptions = new ArrayList<>();


        for (int i = 0 ; i< numOfOptions ; i++) {
            List<RecommendTimelocation> newTmpRecTimeLocs = new ArrayList<>();
            for (int j = 0; j< numOfOptions ; j++) {
                if (i==j) {
                    newTmpRecTimeLocs.add(newRecTimeLoc);

                }
                if (j < sameRecTimeLocWtTmp.size()) {
                    RecommendTimelocation tmpRecTimeLoc = sameRecTimeLocWtTmp.get(j);
                    newTmpRecTimeLocs.add(tmpRecTimeLoc);
                }
            }

            RecommendTimeLocationOption option = checkOption(newTmpRecTimeLocs, i);
            if (option!=null) {
                System.out.println("OK Option : "+option.getOptionId());
                recTimeOptions.add(option);
            }
        }
        // get the best
        if (recTimeOptions.isEmpty()) {
            return null;
        } else {
            recTimeOptions.sort(Comparator.comparing(RecommendTimeLocationOption::getSumTravelTime));
            return recTimeOptions.get(0);
        }
    }

    public static RecommendTimeLocationOption checkOption(List<RecommendTimelocation> newTmpRecTimeLocs, int optionId){
        System.out.println("-------------------------------------------");
        System.out.println("Option ID : "+optionId);
        GoogleAPIMapServices googleMapService = new GoogleAPIMapServices();
        RecommendTimelocation previousRecTimeLoc = newTmpRecTimeLocs.get(0);
        if (previousRecTimeLoc.getRecommendTutor().getClazz()!=null) {
            System.out.println("- Class ID : "+previousRecTimeLoc.getRecommendTutor().getClazz().getClsId());
        } else {
            System.out.println("- Class ID : "+previousRecTimeLoc.getRecommendTutor().getMergedClass().toString());
        }
/*
        System.out.println("previousRecTimeLoc curClassStart : "+previousRecTimeLoc.getRecdStart());
        System.out.println("previousRecTimeLoc curClassEnd : "+previousRecTimeLoc.getRecdEnd());
        System.out.println("previousRecTimeLoc curClassAvaStart : "+previousRecTimeLoc.getRecdAvaStart());
        System.out.println("previousRecTimeLoc curClassAvaEnd : "+previousRecTimeLoc.getRecdAvaEnd());*/
        Long dayId = previousRecTimeLoc.getRecdDayId();
        TutorAvailableTimeloacation tutorDay = newTmpRecTimeLocs.get(0).getRecommendTutor().getTutor().getTutorAvailableTimeloacations().stream().filter(it -> dayId == it.getTavDayId())
                .findAny()
                .orElse(null);
        int numOfTimeLoc = newTmpRecTimeLocs.size();
        int i = 1;
        Double sumTravelTime = previousRecTimeLoc.getRecdTraveltime1();
        while (i < numOfTimeLoc) {
            RecommendTimelocation curRecTimeLoc = newTmpRecTimeLocs.get(i);
            Double newTravelTime = DateTimeUtils.convertSecondsToHours(googleMapService.calculateTravelTime(previousRecTimeLoc.getRecdLat(), previousRecTimeLoc.getRecdLong(), curRecTimeLoc.getRecdLat(), curRecTimeLoc.getRecdLong(),
            tutorDay.getTavTraDrive(), tutorDay.getTavTraTaxi(), tutorDay.getTavTraTrain(), tutorDay.getTavTraBus(), googleMapService));
            Date curClassStart = DateTimeUtils.addTime(previousRecTimeLoc.getRecdEnd(), newTravelTime);
            Date curClassEnd = DateTimeUtils.addTime(previousRecTimeLoc.getRecdEnd(), newTravelTime, curRecTimeLoc.getDuration());
            Date curClassAvaStart = DateTimeUtils.doubleToDate(curRecTimeLoc.getRecdAvaStart());
            Date curClassAvaEnd = DateTimeUtils.doubleToDate(curRecTimeLoc.getRecdAvaEnd());
            if (previousRecTimeLoc.getRecommendTutor().getClazz()!=null) {
                System.out.println("- Class ID : "+curRecTimeLoc.getRecommendTutor().getClazz().getClsId());
            } else {
                System.out.println("- Class ID : "+curRecTimeLoc.getRecommendTutor().getMergedClass().toString());
            }
/*
            System.out.println("newTravelTime : "+newTravelTime);
            System.out.println("curClassStart : "+Double.valueOf(DateTimeUtils.hourFormat(curClassStart)));
            System.out.println("curClassEnd : "+Double.valueOf(DateTimeUtils.hourFormat(curClassEnd)));
            System.out.println("curClassAvaStart : "+Double.valueOf(DateTimeUtils.hourFormat(curClassAvaStart)));
            System.out.println("curClassAvaEnd : "+Double.valueOf(DateTimeUtils.hourFormat(curClassAvaEnd)));*/
            // travel time
            Double[] time = DateTimeUtils.canManageClassInDay(previousRecTimeLoc.getRecdAvaStart(), previousRecTimeLoc.getRecdAvaEnd(), curRecTimeLoc.getRecdAvaStart(), curRecTimeLoc.getRecdAvaEnd(), previousRecTimeLoc.getDuration(), curRecTimeLoc.getDuration(), newTravelTime);

            if (time[0]!=null) {
                // Previous
                previousRecTimeLoc.setRecdTraveltime2(newTravelTime);
                previousRecTimeLoc.setRecdTraveltime(previousRecTimeLoc.getRecdTraveltime1() + previousRecTimeLoc.getRecdTraveltime2() );
                previousRecTimeLoc.setRecdTravelStart(Double.valueOf(DateTimeUtils.hourFormat(DateTimeUtils.subtractTime(time[0], previousRecTimeLoc.getRecdTraveltime1()))));
                previousRecTimeLoc.setRecdTravelEnd(time[2]);
                previousRecTimeLoc.setRecdStart(time[0]);
                previousRecTimeLoc.setRecdEnd(time[1]);
                // Current
                curRecTimeLoc.setRecdTraveltime1(newTravelTime);
                curRecTimeLoc.setRecdTraveltime(curRecTimeLoc.getRecdTraveltime1() + curRecTimeLoc.getRecdTraveltime2() );
                curRecTimeLoc.setRecdStart(time[2]);
                curRecTimeLoc.setRecdEnd(time[3]);
                curRecTimeLoc.setRecdTravelStart(time[1]);
                curRecTimeLoc.setRecdTravelEnd(Double.valueOf(DateTimeUtils.hourFormat(DateTimeUtils.addTime(time[3],curRecTimeLoc.getRecdTraveltime2()))));

                newTmpRecTimeLocs.set(i - 1, previousRecTimeLoc);
                newTmpRecTimeLocs.set(i, curRecTimeLoc);
                sumTravelTime += curRecTimeLoc.getRecdTraveltime2();
            } else {
                return null;
            }
            previousRecTimeLoc = curRecTimeLoc;
            i++;
        }
        RecommendTimeLocationOption recTimeOption = new RecommendTimeLocationOption(optionId, newTmpRecTimeLocs, sumTravelTime);
        return recTimeOption;
    }

    public static List<RecommendTimelocation>[] problemRectutor(List<RecommendTimelocation> tmpRecTimeLocs, List<RecommendTimelocation> problemRecTimeLocs, RecommendTutor problemRecTutor){
        List<RecommendTimelocation> newTmpRecTimeLocs = new ArrayList<>(tmpRecTimeLocs);
        List<RecommendTimelocation> newProblemRecTimeLocs = new ArrayList<>(problemRecTimeLocs);
        if (problemRecTutor.isTmpFlag()) {
            if (problemRecTutor.getMergedClass() != null) {
                // filter Merged class, class1 and class2 out from tmpRecTimeLocs
                // add rectimelocs of Merged class into problemRecTimeLocs
                Class cls1 = problemRecTutor.getMergedClass().getClazz1();
                Class cls2 = problemRecTutor.getMergedClass().getClazz2();
                List<RecommendTimelocation> removeFromTmp = tmpRecTimeLocs.stream()
                        .filter(it -> it.getRecommendTutor().getMergedClass() != null
                                && (it.getRecommendTutor().getMergedClass().getClazz1().getClsId() == cls1.getClsId()
                        || it.getRecommendTutor().getMergedClass().getClazz2().getClsId() == cls2.getClsId())).collect(Collectors.toList());
                newTmpRecTimeLocs.removeAll(removeFromTmp);

            } else {
                // Get merged class
                // filter Merged class, class1 and class2 out from tmpRecTimeLocs
                // add rectimelocs of specific class into problemRecTimeLocs
                Class cls =  problemRecTutor.getClazz();
                List<RecommendTimelocation> recTimeLocMerged = tmpRecTimeLocs.stream()
                        .filter(it -> it.getRecommendTutor().getMergedClass() != null
                                && (it.getRecommendTutor().getMergedClass().getClazz1().getClsId() == cls.getClsId()
                                || it.getRecommendTutor().getMergedClass().getClazz2().getClsId() == cls.getClsId())).collect(Collectors.toList());
                if (!recTimeLocMerged.isEmpty()) {
                    MergedClass mergedClass = recTimeLocMerged.get(0).getRecommendTutor().getMergedClass();
                    Class cls1 = mergedClass.getClazz1();
                    Class cls2 = mergedClass.getClazz2();
                    List<RecommendTimelocation> removeFromTmp = tmpRecTimeLocs.stream()
                            .filter(it -> it.getRecommendTutor().getMergedClass() != null
                                    && (it.getRecommendTutor().getMergedClass().getClazz1().getClsId() == cls1.getClsId()
                                    || it.getRecommendTutor().getMergedClass().getClazz2().getClsId() == cls2.getClsId())).collect(Collectors.toList());
                    newTmpRecTimeLocs.removeAll(removeFromTmp);
                }

            }
        }
        for (RecommendTimelocation recTimeLoc : problemRecTutor.getRecommendTimelocations()){
            List<RecommendTimelocation> sameDayAndTime = tmpRecTimeLocs.stream()
                    .filter(it -> recTimeLoc.getRecdDayId() == it.getRecdDayId()).collect(Collectors.toList());
            for (RecommendTimelocation recLoc : sameDayAndTime) {
                newProblemRecTimeLocs.addAll(recLoc.getRecommendTutor().getRecommendTimelocations());
                newTmpRecTimeLocs.removeAll(recLoc.getRecommendTutor().getRecommendTimelocations());
            }
        }
        return new List[]{newTmpRecTimeLocs, newProblemRecTimeLocs};
    }

}