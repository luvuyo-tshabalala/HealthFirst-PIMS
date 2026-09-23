package model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String fullName;


public User(){}

public User(int userId, String username, String password, String role, String fullName){
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.role = role;
    this.fullName = fullName;
}

public User(String username, String password, String role, String fullName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
}

    public int getuserId(){
        return userId;
    }
    
    public void setUserID(int userID){
        this.userId = userID;
    }

    public String getUsername(){
        return username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole(){
        return role;
    }
    
    public void setRole(String role){
        this.role = role;
    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

}