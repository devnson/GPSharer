package com.example.blackpearl.gpsharer;

public class GetSetUser {

    private String id;
    public String username;
    private String fullname;
    public String CircleCode;


    public GetSetUser() {

    }

    public GetSetUser(String id, String username, String fullname, String CircleCode) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.CircleCode = CircleCode;
    }

    public String getFullname() {
        return fullname;
    }

    public String getCircleCode() {
        return CircleCode;
    }

    public void setCircleCode(String circleCode) {
        CircleCode = circleCode;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
