package com.bsd.tutor.dao;

/**
 * Created by kewalins on 2/13/2018 AD.
 */

import com.bsd.tutor.model.Tutor;
import com.bsd.tutor.model.TutorSubject;
import com.bsd.tutor.utils.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author imssbora
 */
@Transactional
@Repository
public class TutorSubjectDao {


    public List<TutorSubject> list() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<TutorSubject> tutorSubjects = session.createQuery("FROM TutorSubject").list();
        session.close();
        System.out.println("Found " + tutorSubjects.size() + " TutorSubject");
        return tutorSubjects;
    }
    public static List<TutorSubject> findBySubject(Integer subjectId, Integer groupId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        String hql = "FROM TutorSubject WHERE subjectDetail.subject.subjId=:subjectID AND subjectDetail.subjectGroup.grpId=:groupID";
        Query query = session.createQuery(hql);
        query.setParameter("subjectID", subjectId);
        query.setParameter("groupID", groupId);
        List<TutorSubject> tutorSubjects = query.list();
        session.close();
        System.out.println("Found " + tutorSubjects.size() + " TutorSubject");
        return tutorSubjects;
    }


    public static Integer create(TutorSubject e) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(e);
        session.getTransaction().commit();
        session.close();
        System.out.println("Successfully created " + e.toString());
        return e.getTsbjId();

    }

    public static List<TutorSubject> read() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<TutorSubject> tutorSubjects = session.createQuery("FROM TutorSubject").list();
        session.close();
        System.out.println("Found " + tutorSubjects.size() + " TutorSubject");
        return tutorSubjects;

    }


    public static TutorSubject findByID(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        TutorSubject e = (TutorSubject) session.load(TutorSubject.class, id);
        session.close();
        return e;
    }


}
