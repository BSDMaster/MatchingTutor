package com.bsd.tutor.dao;

/**
 * Created by kewalins on 2/13/2018 AD.
 */

import com.bsd.tutor.model.Class;
import com.bsd.tutor.model.MergedClass;
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
public class MergedClassDao {


    public List<MergedClass> list() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<MergedClass> classes = session.createQuery("FROM MergedClass").list();
        session.close();
        System.out.println("Found " + classes.size() + " MergedClass");
        return classes;
    }


    public static Integer create(MergedClass e) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(e);
        session.getTransaction().commit();
        session.close();
        System.out.println("Successfully created " + e.toString());
        return e.getMerId();

    }

    public static List<MergedClass> createList(List<MergedClass> mergedClasses) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        for (MergedClass mergedClass : mergedClasses) {
            session.save(mergedClass);
        }
        session.getTransaction().commit();
        session.close();
        System.out.println("Successfully created " + mergedClasses.size());
        return mergedClasses;

    }
    public static List<MergedClass> read() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<MergedClass> classes = session.createQuery("FROM MergedClass").list();
        session.close();
        System.out.println("Found " + classes.size() + " MergedClass");
        return classes;

    }


    public static MergedClass findByID(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        MergedClass e = (MergedClass) session.load(MergedClass.class, id);
        session.close();
        return e;
    }


}
