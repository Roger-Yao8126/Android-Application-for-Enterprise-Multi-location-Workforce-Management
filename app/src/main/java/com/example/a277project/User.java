package com.example.a277project;

import java.util.ArrayList;
import java.util.List;

public class User {

    String Username, Role, Location, Shift;
    String Workday;

    public User() {
    }

    public User(String username, String role, String Location, String Shift, String Workday) {
        this.Username = username;
        this.Role = role;
        this.Location = Location;
        this.Shift = Shift;
        this.Workday = Workday;

    }


    public String getusername() {
        return Username;
    }

    public void setusername(String username) {
        this.Username = username;
    }

    public String getrole() {
        return Role;
    }

    public void setrole(String role) {
        this.Role = role;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getShift() {
        return Shift;
    }

    public void setShift(String shift) {
        Shift = shift;
    }


    public String getWorkday() {
        return Workday;
    }

    public void setWorkday(String workday) {
        Workday = workday;
    }
}
