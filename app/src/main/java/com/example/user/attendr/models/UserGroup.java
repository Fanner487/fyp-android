package com.example.user.attendr.models;

import java.util.ArrayList;

/**
 * Created by Eamon on 11/02/2018.
 *
 * model for groups
 */

public class UserGroup {

    private int id;
    private String username;
    private String groupName;
    private ArrayList<String> users;
    private String description;

//    public UserGroup(int id, String username, String groupName, ArrayList<String> users) {
//        this.id = id;
//        this.username = username;
//        this.groupName = groupName;
//        this.users = users;
//    }

    public UserGroup(int id, String username, String groupName, ArrayList<String> users, String description) {
        this.id = id;
        this.username = username;
        this.groupName = groupName;
        this.users = users;
        this.description = description;

    }

    public UserGroup(String groupName, String description, ArrayList<String> users) {
        this.groupName = groupName;
        this.description = description;
        this.users = users;
    }



    public UserGroup(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", groupName='" + groupName + '\'' +
                ", users=" + users +
                '}';
    }
}
