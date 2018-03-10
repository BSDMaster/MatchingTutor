package com.bsd.tutor.service;

import com.bsd.tutor.model.Class;
import com.bsd.tutor.model.RecommendTutor;
import com.bsd.tutor.model.TutorSubject;
import com.bsd.tutor.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kewalins on 2/26/2018 AD.
 */
@Service
public class RecommendTutorService {

    public List<RecommendTutor> convertMapToRecTutors(Map<Integer, List<RecommendTutor>> map) {

        List<RecommendTutor> recTutors = new ArrayList<>();
        for (Map.Entry<Integer,List<RecommendTutor>> entry : map.entrySet())
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
}