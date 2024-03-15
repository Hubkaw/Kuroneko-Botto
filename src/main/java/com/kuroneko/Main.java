package com.kuroneko;

import com.kuroneko.Config.Bot;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
//        new Updater().start();
        new Bot();
    }
}
