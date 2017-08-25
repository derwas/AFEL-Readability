package controllers;

import java.util.List;

import com.google.inject.Inject;
import com.typesafe.config.Config;

import models.User;
import play.mvc.*;
import play.twirl.api.Html;
import play.data.Form;
import play.data.Form.*;
import play.data.FormFactory;
import play.Logger;
import play.libs.Json;
import views.html.*;

public class Application extends Controller {

	private FormFactory formFactory;
	private static Config config;


    @Inject
    public Application(final FormFactory formFactory, Config config) {
        this.formFactory = formFactory;
        this.config = config;

    }
    
    


    
    public Result login() {
   	 	return ok(views.html.login.render(formFactory.form(User.class).bindFromRequest())); 
    }
    
    public  Result logout() {
        session().clear();
        flash("message", "You've been logged out");
        return ok(views.html.login.render(formFactory.form(User.class).bindFromRequest())); 

    }
    
    public static boolean authorisedUser(String name, String password){
    	
      	List <String> lst = config.getStringList("users");
    	if (name.equals("afel")&&password.equals("afel")){
    		return true;
    	}
    	else{
    	   	return false;
    	}
    }
    
    public static boolean authorisedUser(String base64Credentials){
    	
      	List <String> lst = config.getStringList("users");
    	if (lst.contains(base64Credentials)){
    		return true;
    	}
    	else{
    	   	return false;
    	}
    }
    
    public Result authenticate() {
    	
    	Form<User> loginForm = formFactory.form(User.class).bindFromRequest();
    	
   	
    	Logger.info("Authenticating!!!"+loginForm.rawData().get("name"));
    	
    	
        if (loginForm.hasErrors()) {
        	Logger.info("Authentication error : form has errors!");
            flash("message", "Authentication errors!");
	   	 	return ok(views.html.login.render(formFactory.form(User.class).bindFromRequest())); 
        } else {
        	if (authorisedUser(loginForm.rawData().get("name"), loginForm.rawData().get("password"))){
        		session().clear();
                session("name", loginForm.get().name);
    	   	 	return ok(views.html.index.render("http://",Html.apply(""),Html.apply(""),Html.apply(""))); 
        	}else{
            	Logger.info("Authentication error : wrong login details!");
                flash("success", "Unable to recognise user details!");
    	   	 	return ok(views.html.login.render(formFactory.form(User.class).bindFromRequest())); 
        	}
            

        }
    }
    

}
