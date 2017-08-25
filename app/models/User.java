package models;


public class User  {

    public String name;
    public String password;
    public boolean isAdmin;

    public User(){
       super();
    }
    
    public User(String name, String password, boolean isAdmin) {
      this.name = name;
      this.password = password;
      this.isAdmin = isAdmin;
    }

    

}