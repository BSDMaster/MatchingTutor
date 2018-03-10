package com.bsd.tutor.dao;

/**
 * Created by kewalins on 2/13/2018 AD.
 */

import com.bsd.tutor.model.Employee;
import com.bsd.tutor.model.Tutor;
import com.bsd.tutor.utils.Constants;
import com.bsd.tutor.utils.HibernateUtil;
import org.hibernate.Criteria;
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
public class TutorDao {


    public List<Tutor> list() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<Tutor> tutors = session.createQuery("FROM Tutor").list();
        session.close();
        System.out.println("Found " + tutors.size() + " Tutor");
        return tutors;
    }

    public static List<Tutor> findByClassInfo(Integer subjectId, Integer subjectDetailId) {
        Session session = HibernateUtil.getSessionFactory().openSession();

       // List<Tutor> tutors = session.createQuery("FROM Tutor WHERE tutorSubject.subjectDetail.subject.subjId = : AND tutorSubject.subjectDetail.subjectGroup.").list();
       /* String hql = "FROM Tutor WHERE tutorSubject.subjectDetail.subject.subjId=:subjectID";
        Query query = session.createQuery(hql);
        query.setParameter(":subjectID", subjectId);
        List<Tutor> tutors = query.list();*/
        @SuppressWarnings("unchecked")
        String hql = "FROM Tutor WHERE tutorSubject.subjectDetail.subject.subjId=:subjectID";
        Query query = session.createQuery(hql);
        query.setParameter(":subjectID", subjectId);
        List<Tutor> tutors = query.list();
                session.close();
        System.out.println("Found " + tutors.size() + " Tutor");
        return tutors;
    }


    public static Integer create(Tutor e) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(e);
        session.getTransaction().commit();
        session.close();
        System.out.println("Successfully created " + e.toString());
        return e.getTurId();

    }

    public static List<Tutor> read() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<Tutor> tutors = session.createQuery("FROM Tutor").list();
        session.close();
        System.out.println("Found " + tutors.size() + " Tutor");
        return tutors;

    }


    public static Tutor findByID(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Tutor e = (Tutor) session.load(Tutor.class, id);
        session.close();
        return e;
    }

    public static List<Tutor> findBySubjectAndProgram(Integer subjectId, Integer groupId, Integer programId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        String hql = "SELECT t FROM Tutor as t INNER JOIN t.tutorSubject as s ON t.turId = s.tutor.turId " +
                "INNER JOIN t.tutorPrograms as p ON t.turId = p.tutor.turId " +
                "WHERE t.turBlacklistFlag=:blacklistFlag AND t.turVerifiedFlag=:verifyFlag AND s.subjectDetail.subject.subjId=:subjectID " +
                "AND s.subjectDetail.subjectGroup.grpId=:groupID " +
                "AND p.ttpPrgId =:programId";
        Query query = session.createQuery(hql);
        query.setParameter("blacklistFlag", Constants.FLAG_NO);
        query.setParameter("verifyFlag", Constants.FLAG_YES);
        query.setParameter("subjectID", subjectId);
        query.setParameter("groupID", groupId);
        query.setParameter("programId", programId);
       // query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Tutor> tutors = query.list();
        session.close();
       // System.out.println("Found " + tutors.size() + " tutors");
        return tutors;
    }
    public static List<Tutor> findBySubjectAndProgram(Integer subjectId1, Integer groupId1, Integer subjectId2, Integer groupId2, Integer programId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        String hql = "SELECT t FROM Tutor as t INNER JOIN t.tutorSubject as s ON t.turId = s.tutor.turId " +
                "INNER JOIN t.tutorPrograms as p ON t.turId = p.tutor.turId " +
                "WHERE t.turBlacklistFlag=:blacklistFlag AND t.turVerifiedFlag=:verifyFlag AND s.subjectDetail.subject.subjId IN (:subjectID1,:subjectID2) " +
                "AND s.subjectDetail.subjectGroup.grpId IN (:groupID1,:groupID2) " +
                "AND p.ttpPrgId =:programId";
        Query query = session.createQuery(hql);
        query.setParameter("blacklistFlag", Constants.FLAG_NO);
        query.setParameter("verifyFlag", Constants.FLAG_YES);
        query.setParameter("subjectID1", subjectId1);
        query.setParameter("groupID1", groupId1);
        query.setParameter("subjectID2", subjectId2);
        query.setParameter("groupID2", groupId2);
        query.setParameter("programId", programId);
        // query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Tutor> tutors = query.list();
        session.close();
        System.out.println("Found " + tutors.size() + " tutors");
        return tutors;
    }
}
