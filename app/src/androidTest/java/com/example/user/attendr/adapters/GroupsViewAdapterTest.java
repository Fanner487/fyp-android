package com.example.user.attendr.adapters;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Eamon on 10/03/2018.
 */

public class GroupsViewAdapterTest {

    private Context targetContext;
    private ArrayList<UserGroup> groups;
    private GroupsViewAdapter groupsViewAdapter;

    @Before
    public void setUp() throws Exception{

        targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();

        ArrayList<String> users = new ArrayList<>();
        users.add("a");
        users.add("b");
        users.add("c");

        groups = new ArrayList<>();
        groups.add(new UserGroup("test", "test", users));
        groups.add(new UserGroup("test1", "test", users));
        groups.add(new UserGroup("test2", "test", users));

    }

    @Test
    public void testGetItemCount(){
        groupsViewAdapter = new GroupsViewAdapter(targetContext, groups);

        assertEquals(groupsViewAdapter.getItemCount(), 3);
    }
}
