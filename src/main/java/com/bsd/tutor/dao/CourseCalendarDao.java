package com.bsd.tutor.dao;

/**
 * Created by kewalins on 2/13/2018 AD.
 */

import com.bsd.tutor.model.Class;
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
public class CourseCalendarDao {


    public List<Class> list() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<Class> classes = session.createQuery("FROM Class").list();
        session.close();
        System.out.println("Found " + classes.size() + " Class");
        return classes;
    }


    public static Integer create(Class e) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(e);
        session.getTransaction().commit();
        session.close();
        System.out.println("Successfully created " + e.toString());
        return e.getClsId();

    }

    public static List<Class> read() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<Class> classes = session.createQuery("FROM Class").list();
        session.close();
        System.out.println("Found " + classes.size() + " Class");
        return classes;

    }


    public static Class findByID(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Class e = (Class) session.load(Class.class, id);
        session.close();
        return e;
    }

    public static List<Class> findByStatus(List<String> status) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        String hql = "FROM Class WHERE classStatus.cosStatusName IN (:status)";
        Query query = session.createQuery(hql);
        query.setParameter("status", status);
        // query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Class> classes = query.list();
        session.close();
        System.out.println("Found " + classes.size() + " Classes");
        return classes;
    }

}
