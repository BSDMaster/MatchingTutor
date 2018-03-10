package com.bsd.tutor.utils;

import com.bsd.tutor.model.*;
import com.bsd.tutor.model.Class;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * Created by kewalins on 2/13/2018 AD.
 */

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {

        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Class.class);
        configuration.addAnnotatedClass(ClassStatus.class);
        configuration.addAnnotatedClass(ClassTutorFaculty.class);
        configuration.addAnnotatedClass(ClassTutorUniversity.class);
        configuration.addAnnotatedClass(CourseCalendar.class);
        configuration.addAnnotatedClass(Faculty.class);
        configuration.addAnnotatedClass(Parent.class);
        configuration.addAnnotatedClass(RecommendTimelocation.class);
        configuration.addAnnotatedClass(RecommendTutor.class);
        configuration.addAnnotatedClass(Student.class);
        configuration.addAnnotatedClass(StudentAvailableTimelocation.class);
        configuration.addAnnotatedClass(Subject.class);
        configuration.addAnnotatedClass(SubjectDetail.class);
        configuration.addAnnotatedClass(SubjectGroup.class);
        configuration.addAnnotatedClass(SubjectGroupDetail.class);
        configuration.addAnnotatedClass(Traveltype.class);
        configuration.addAnnotatedClass(Tutor.class);
        configuration.addAnnotatedClass(Employee.class);
        configuration.addAnnotatedClass(FacultyOfUniversity.class);
        configuration.addAnnotatedClass(TutorAvailableTimeloacation.class);
        configuration.addAnnotatedClass(TutorMatching.class);
        configuration.addAnnotatedClass(TutorProgram.class);
        configuration.addAnnotatedClass(TutorSubject.class);
        configuration.addAnnotatedClass(University.class);
        configuration.addAnnotatedClass(MergedClass.class);
        configuration.addAnnotatedClass(MergedClassAvailabletimeLocation.class);
        configuration.addAnnotatedClass(TravelTime.class);
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());
        sessionFactory = configuration
                .buildSessionFactory(builder.build());
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}