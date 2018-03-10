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

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> rule  = new  ActivityTestRule<>(LoginActivity.class);

    @Test
    public void testTextViewExists(){
        LoginActivity activity = rule.getActivity();

        EditText etUsername = activity.findViewById(R.id.etUsername);
        EditText etPassword = activity.findViewById(R.id.etPassword);
        Button btnSubmit = activity.findViewById(R.id.btnSubmit);
        Button btnRegister = activity.findViewById(R.id.btnRegister);

        assertNotNull(etUsername);
        assertNotNull(etPassword);
        assertNotNull(btnSubmit);
        assertNotNull(btnRegister);
    }


}
