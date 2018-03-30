package com.bsd.tutor.dao;

/**
 * Created by kewalins on 2/13/2018 AD.
 */

import com.bsd.tutor.model.StudentAvailableTimelocation;
import com.bsd.tutor.model.Tutor;
import com.bsd.tutor.utils.Constants;
import com.bsd.tutor.utils.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


/**
 * @author imssbora
 */
@Transactional
@Repository
public class StudentAvailableTimelocationDao {


    public List<StudentAvailableTimelocation> list() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<StudentAvailableTimelocation> timelocations = session.createQuery("FROM StudentAvailableTimelocation").list();
        session.close();
        System.out.println("Found " + timelocations.size() + " StudentAvailableTimelocations");
        return timelocations;
    }

    public static Long create(StudentAvailableTimelocation e) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(e);
        session.getTransaction().commit();
        session.close();
        System.out.println("Successfully created " + e.toString());
        return e.getSavId();

    }

    public static List<StudentAvailableTimelocation> read() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<StudentAvailableTimelocation> timelocations = session.createQuery("FROM StudentAvailableTimelocation").list();
        session.close();
        System.out.println("Found " + timelocations.size() + " Tutor");
        return timelocations;

    }


    public static StudentAvailableTimelocation findByID(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        StudentAvailableTimelocation e = (StudentAvailableTimelocation) session.load(StudentAvailableTimelocation.class, id);
        session.close();
        return e;
    }

    public static List<StudentAvailableTimelocation> findByExceptSpecificDays(Set<Long> days) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        String hql = "SELECT st FROM StudentAvailableTimelocation as st " +
                "WHERE st.savDayId NOT IN (:days)";
        Query query = session.createQuery(hql);
        query.setParameter("days", days);

        List<StudentAvailableTimelocation> timelocations = query.list();
        session.close();
        System.out.println("Found " + timelocations.size() + " StudentAvailableTimelocation");
        return timelocations;
    }
}
