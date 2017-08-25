package controllers;

import play.mvc.Controller;

public class Users extends Controller{

    public static boolean authenticate(String name, String password) {
        return name.equals("Admin") && password.equals("AFEL");
    }
}
