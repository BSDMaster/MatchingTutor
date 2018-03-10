package com.bsd.tutor.dao;

/**
 * Created by kewalins on 2/13/2018 AD.
 */

import com.bsd.tutor.model.Tutor;
import com.bsd.tutor.model.TutorMatching;
import com.bsd.tutor.utils.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author imssbora
 */
@Transactional
@Repository
public class TutorMatchingDao {


    public List<TutorMatching> list() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<TutorMatching> tutorMatchings = session.createQuery("FROM TutorMatching").list();
        session.close();
        System.out.println("Found " + tutorMatchings.size() + " TutorMatching");
        return tutorMatchings;
    }

}
