package com.ploodi.utills;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * @author Kishan H Dhamat , Email: kishan.dhamat105@gmail.com
 */
@SuppressLint("NewApi")
public class AndyUtils {
    private static final String TAG = "AndyUtils";
    static float density = 1;
    private static ProgressDialog mProgressDialog, mLocationDialog;
    private static Dialog mDialog;
    private static Bitmap myBitmap;

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

    public static void showSimpleProgressDialog(Context context) {
        showSimpleProgressDialog(context, null, "Loading...", false);
    }

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

    public static void removeCustomProgressDialog() {
        try {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

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

    public static boolean eMailValidation(String emailstring) {
        if (null == emailstring || emailstring.length() == 0) {
            return false;
        }
        Pattern emailPattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher emailMatcher = emailPattern.matcher(emailstring);
        return emailMatcher.matches();
    }

    public static boolean passwordValidation(String passwordstring) {
        if (null == passwordstring || passwordstring.length() == 0) {
            return false;
        }
        Pattern passwordPattern = Pattern
                .compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})");
        Matcher passwordMatcher = passwordPattern.matcher(passwordstring);
        return passwordMatcher.matches();
    }

    public static void showToast(String msg, Context ctx) {

        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public static void openAppInfoSettings(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static String takeScreenShot(final View view, final Context context) {
        String path = null;
        view.post(new Runnable() {
            public void run() {

                // take screenshot
                myBitmap = captureScreen(view);

                try {
                    if (myBitmap != null) {
                        // save image to SD card
                        saveImage(myBitmap, context);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        return path;
    }

    public static Bitmap captureScreen(View v) {

        Bitmap screenshot = null;
        try {

            if (v != null) {

                screenshot = Bitmap.createBitmap(v.getMeasuredWidth(),
                        v.getMeasuredHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(screenshot);
                v.draw(canvas);
            }

        } catch (Exception e) {
            Log.d("ScreenShotActivity", "Failed to capture screenshot because:"
                    + e.getMessage());
        }

        return screenshot;
    }

    public static void saveImage(Bitmap bitmap, Context context)
            throws IOException {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/AdsGroup");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        File f = new File(wallpaperDirectory, Calendar.getInstance()
                .getTimeInMillis() + ".jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        MediaScannerConnection.scanFile(context, new String[]{f.getPath()},
                new String[]{"image/jpeg"}, null);
        fo.close();
        // new PreferenceHelper(context).putCardPath(f.getAbsolutePath());

    }

    public static final String getBase64String(Bitmap photoBitmap) {
        String photo;
        if (photoBitmap != null) {

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
            // photoBitmap.recycle();
            photo = android.util.Base64.encodeToString(bao.toByteArray(),
                    android.util.Base64.DEFAULT);
            try {
                bao.close();
                bao = null;
                photoBitmap = null;

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            photo = "";
        }
        return photo;
    }

    public static void IntentStart(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public static void DialogForNoInternetConnection(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(
                "No Internet connection, Please Turn On Your Mobile Data or Wifi")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    final DialogInterface dialog,
                                    final int id) {
                                Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(dialogIntent);
                            }
                        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //  Method to open activity
    public static void openActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public static int[] getWidthHight(Activity mActivity) {
        int[] mWidthHight = new int[2];
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        mWidthHight[0] = width;
        mWidthHight[1] = height;

        return mWidthHight;
    }

}
