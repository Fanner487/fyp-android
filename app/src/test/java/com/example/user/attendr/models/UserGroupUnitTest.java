package com.example.user.attendr.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.user.attendr.activities.MainActivity;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.credentials.CredentialManager;
import com.example.user.attendr.enums.EventType;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by Eamon on 10/03/2018.
 */

public class UserGroupUnitTest {

    @Test
    public void testCreate(){

        int id = 1;
        String username = "test";
        String groupName = "test";
        ArrayList<String> users = new ArrayList<>();
        users.add("a");
        users.add("b");
        String description = "test";

        UserGroup userGroup = new UserGroup(id, username, groupName, users, description);

        assertEquals(userGroup.getId(), id);
        assertEquals(userGroup.getUsername(), username);
        assertEquals(userGroup.getGroupName(), groupName);
        assertEquals(userGroup.getUsers(), users);
        assertEquals(userGroup.getDescription(), description);
    }
}
