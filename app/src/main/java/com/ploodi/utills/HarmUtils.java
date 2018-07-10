package com.ploodi.utills;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by shootme-hardik on 25/3/17.
 */

public class HarmUtils {

    //region PRIVATE_DECLARATIONS

    private static ProgressDialog mProgressDialog;

    //endregion



    //region PUBLIC_METHODS

//  Method to check whether network connectivity is available or not
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


//  Method to show non-cancelable simple progress dialog
    public static void showSimpleProgressDialog(Context context) {
        showSimpleProgressDialog(context, null, "Loading...", false);
    }


//  Method to show simple progress dialog
    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//  Method to remove simple progress dialog
    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//  Method to show toast message in app
    public static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


//  Method to set focus on EditText Widget and open Keyboard
    public static void setFocusOnEditText(Context context, EditText editText) {
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }


//  Method to vibrate phone for seconds set in "seconds" parameter
    public static void vibratePhone(Context context, float seconds) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate works on milliseconds therefore multiplied by 1000
        v.vibrate((long)(seconds * 1000));
    }


//  Method to open activity
    public static void openActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }


//  Method to open activity
    public static void openActivity(Context context, Class<?> cls, boolean shouldFinish) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
        if(shouldFinish) {
            Activity activity = (Activity) context;
            activity.finish();
        }
    }


//  Method to open activity
    public static void openActivityAndClearPreviousStack(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        clearActivityStack(intent);
        context.startActivity(intent);
    }


    /*
    must set below lines in style.xml between <style></style>
    * <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
        <item name="android:windowLightStatusBar" tools:targetApi="m">true</item>
        <item name="android:windowDrawsSystemBarBackgrounds">true</item>
    * */
//  To set colorbar of status, PARAMETER: activity, R.color.colorButtonPink
    public static void setStatusBarColor(Activity activity, int colorToSet) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorToSet));
        }
    }


    public static void hideKeyboardByDefault(Window window) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }



//    Method to get address from provided LatLong in parameter
    public static String getAddressFromLatLong(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                }
                strAdd = strReturnedAddress.toString();
                strAdd = strAdd.substring(0, strAdd.length() - 2);
            } else {
                Log.w("HarM", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("HarM", "Can not get Address!");
        }
        return strAdd;
    }


    public static void openAppInfoSettings(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }


    public static void showErrorToastMessage(Context context, float time, String message) {
        HarmUtils.vibratePhone(context, time);
        HarmUtils.showToast(message, context);
    }


    public static void showErrorToastMessage(Context context, float time, String message, EditText editText) {
        HarmUtils.vibratePhone(context, time);
        HarmUtils.showToast(message, context);
        HarmUtils.setFocusOnEditText(context, editText);
    }


    public static void showConfirmationDialog(Context context, String message, String positiveButtonText, String negativeButtonText, final Runnable runnable) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(positiveButtonText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        runnable.run();
                    }
                });

        alertDialogBuilder.setNegativeButton(negativeButtonText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void showConfirmationDialog(Context context, String message, String positiveButtonText, final Runnable runnable) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(positiveButtonText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        runnable.run();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void waitForXSecondsAndExecute (float numberOfSeconds, final Runnable runnable) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                runnable.run();
            }
        }, (long) (numberOfSeconds * 1000));
    }


    public static void reopenApp(Context context, Class<?> cls) {
        Intent mStartActivity = new Intent(context, cls);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    //endregion



    //region PRIVATE_METHODS

    //  To clear all activity stack
    private static void clearActivityStack(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //endregion

}
