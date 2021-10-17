package com.video.tamas.Utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.video.tamas.R;

public class Common {
    private static final int CONTAINER_ID = R.id.fragment_container;


    public static void changeFragment(FragmentActivity activity, Fragment fragment) {
        if (fragment == null || activity == null) {
            return;
        }
        try {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(CONTAINER_ID, fragment, activity.getString(R.string.tag_fragment));
            transaction.addToBackStack(activity.getString(R.string.tag_fragment));
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void imageRotation(ImageView imageView) {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(9000);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(rotate);
    }

    public static String getDeviceId() {

        return FirebaseInstanceId.getInstance().getToken();
    }

    public static Boolean doValidation(Activity activity, EditText username, EditText password) {
        if (!TextUtils.isEmpty(username.getText()) && Patterns.EMAIL_ADDRESS.matcher(username.getText()).matches()) {
            if (!TextUtils.isEmpty(password.getText())) {
                if (password.getText().toString().length() < 6) {


                } else {
                    Toast.makeText(activity, "Password must contain of minimum 8 characters, capital letter, special character & a number", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(activity, "Password cannot be empty, please enter password", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(activity, "Please enter correct email address", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    public static String generateFilenameFromFileUrl(String url) {
        return url.replace("/", "_").replace(":", "_").replace(" ", "_");
    }


}

