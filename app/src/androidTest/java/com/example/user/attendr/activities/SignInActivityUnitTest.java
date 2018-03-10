package com.example.user.attendr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
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

public class SignInActivityUnitTest {

    @Rule
    public ActivityTestRule<SignInActivity> rule  = new  ActivityTestRule<SignInActivity>(SignInActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, SignInActivity.class);

            Bundle bundle = new Bundle();
            bundle.putInt(DbConstants.EVENT_KEY_EVENT_ID, 1);
//            result.putExtra(DbConstants.EVENT_KEY_EVENT_ID, 1);
            result.putExtras(bundle);
            return result;
        }
    };


    @Test
    public void testTextViewExists(){
        SignInActivity activity = rule.getActivity();
        TextView tvDate = activity.findViewById(R.id.tvDate);
        TextClock textClock = activity.findViewById(R.id.textClock1);
        ImageView qrCodeView = activity.findViewById(R.id.qrCodeView);

        assertNotNull(tvDate);
        assertNotNull(textClock);
        assertNotNull(qrCodeView);
    }
}
