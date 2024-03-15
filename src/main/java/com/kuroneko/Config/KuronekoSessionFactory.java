package com.kuroneko.Config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class KuronekoSessionFactory {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null){
            createSessionFactory();
        }
        return sessionFactory;
    }

    private static void createSessionFactory(){
        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.username", Config.getConfig().getDbUser())
                .setProperty("hibernate.connection.password", Config.getConfig().getDbPassword())
                .configure();

        sessionFactory = configuration.buildSessionFactory();
    }
}
