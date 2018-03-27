package com.example.user.attendr.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.user.attendr.R;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.constants.TimeFormats;
import com.example.user.attendr.credentials.CredentialManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignInActivity extends AppCompatActivity {

    TextClock tClock;
    ImageView qrCodeView;
    TextView tvDate;
    Bundle bundle;
    ImageView circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hides Activity bar
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){e.fillInStackTrace();}

        setContentView(R.layout.activity_sign_in);

        bundle = getIntent().getExtras();

        tClock = findViewById(R.id.textClock1);
        qrCodeView = findViewById(R.id.qrCodeView);
        tvDate = findViewById(R.id.tvDate);
//        circle = findViewById(R.id.imageView3);

//        TranslateAnimation animation = new TranslateAnimation(0.0f, 400.0f,
//                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
//        animation.setDuration(5000);  // animation duration
//        animation.setRepeatCount(5);  // animation repeat count
//        animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
//        //animation.setFillAfter(true);

//        circle.startAnimation(animation);

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(TimeFormats.DATE_DISPLAY_FORMAT, Locale.ENGLISH);
        String formattedDate = format.format(date);

        tvDate.setText(formattedDate);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        String username = CredentialManager.getCredential(getApplicationContext(), BundleAndSharedPreferencesConstants.USERNAME);
        String eventId = Integer.toString(bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID));
        String encodeMessage = username + " " + eventId;

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 70 / 100.0f;
        getWindow().setAttributes(lp);

        int width = 200;
        int height = 200;

        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(encodeMessage, BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);

            qrCodeView.setImageBitmap(bitmap);
        }
        catch (WriterException e){
            e.printStackTrace();
        }

    }
}
