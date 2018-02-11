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

    public UserGroup(int id, String username, String groupName, ArrayList<String> users) {
        this.id = id;
        this.username = username;
        this.groupName = groupName;
        this.users = users;
    }

    public UserGroup(String username, String groupName, ArrayList<String> users) {
        this.username = username;
        this.groupName = groupName;
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
