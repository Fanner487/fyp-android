package com.example.user.attendr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.user.attendr.R;
import com.example.user.attendr.constants.DbConstants;

import org.junit.Rule;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Eamon on 10/03/2018.
 */

public class RegisterActivityUnitTest {

    @Rule
    public ActivityTestRule<RegisterActivity> rule  = new  ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void testTextViewExists(){
        RegisterActivity activity = rule.getActivity();

        EditText etUsername = activity.findViewById(R.id.etUsername);
        EditText etEmail = activity.findViewById(R.id.etEmail);
        EditText etPassword = activity.findViewById(R.id.etPassword);
        EditText etPasswordConfirm = activity.findViewById(R.id.etPasswordConfirm);
        EditText etFirstName = activity.findViewById(R.id.etFirstName);
        EditText etLastName = activity.findViewById(R.id.etLastName);
        Button btnSubmit = activity.findViewById(R.id.btnSubmit);

        assertNotNull(etUsername);
        assertNotNull(etEmail);
        assertNotNull(etPassword);
        assertNotNull(etPasswordConfirm);
        assertNotNull(etFirstName);
        assertNotNull(etLastName);
        assertNotNull(btnSubmit);
    }
}
