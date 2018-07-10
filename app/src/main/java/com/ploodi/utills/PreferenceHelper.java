package com.ploodi.utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author Kishan H Dhamat , Email: kishan.dhamat105@gmail.com
 */
public class PreferenceHelper {

    private SharedPreferences app_prefs;
    private final String USER_ID = "user_id";
    private final String EMAIL = "email";
    private final String PHONE = "phone";
    private final String USER_IMAGE = "user_image";
    private final String USER_TYPE = "user_type";
    private final String Password = "password";
    private final String FIRST_NAME = "firstName";
    private final String LASTNAME = "lastName";
    private final String ISLOGIN = "islogin";
    private final String CITY = "city";
    private final String USER_NAME = "userName";
    private final String MOBILENUM = "mobile_no";
    private final String Address = "address";
    private final String DEVICE_TOKEN = "device_token";
    private final String GENDER = "gender";
    private final String COUNTRY = "country";
    private final String STATE = "state";
    private final String ABOUT = "about";
    private final String ETHNICITY = "ethnicity";
    private final String LOOKING = "lookingfor";
    private final String AGE = "age";
    private final String EDUCATION = "education";
    private final String DOB = "dob";
    private final String FBID = "fbid";
    private final String ISPROFILE = "isprofile";
    private Context context;
    private int lastName;


    public PreferenceHelper(Context context) {
        app_prefs = context.getSharedPreferences(AndyConstants.PREF_NAME,
                Context.MODE_PRIVATE);
        this.context = context;
    }

    public void putUserId(String userId) {
        Log.d("tagg", "putUserId:"+userId);
        Editor edit = app_prefs.edit();
        edit.putString(USER_ID, userId);
        edit.commit();
    }

    public String getUserId() {
        return app_prefs.getString(USER_ID, null);
    }

    public void putEmail(String email) {
        Editor edit = app_prefs.edit();
        edit.putString(EMAIL, email);
        edit.commit();
    }

    public String getEmail() {
        return app_prefs.getString(EMAIL, null);
    }

    public void putPassword(String password){
        Editor edit = app_prefs.edit();
        edit.putString(Password, password);
        edit.commit();
    }

    public String getPassword() {
        return app_prefs.getString(Password, null);
    }

    public void putUsername(String uString) {
        Editor edit = app_prefs.edit();
        edit.putString(USER_NAME, uString);
        edit.commit();
    }

    public void putMobileNumber(String mobilenum) {
        Editor edit = app_prefs.edit();
        edit.putString(MOBILENUM, mobilenum);
        edit.commit();
    }

    public String getMobileNum() {
        return app_prefs.getString(MOBILENUM, null);
    }

    public void putAddress(String address) {
        Editor edit = app_prefs.edit();
        edit.putString(Address, address);
        edit.commit();
    }

    public void onLogOut() {
        putEducation("");
        putUserImage("");
        putCountry("");
        putState("");
        putEmail("");
        putUsername("");
        putLastName("");
        putAddress("");
        putMobileNumber("");
        putUserType("");
        putPassword("");
        putIsLogin(false);
        putIsProfileComplete(false);
        putDob("");
        putUserType("");
        putUserImage("");
        putUserId("");

    }

    public void putUserImage(String user_image) {
        Editor edit = app_prefs.edit();
        edit.putString(USER_IMAGE, user_image);
        edit.commit();
    }

    public String getUserImage() {
        return app_prefs.getString(USER_IMAGE, "");
    }

    public void putUserType(String user_type) {
        Editor edit = app_prefs.edit();
        edit.putString(USER_TYPE, user_type);
        edit.commit();
    }

    public String getUserType() {
        return app_prefs.getString(USER_TYPE, "");
    }

    public void putPhone(String phone) {
        Editor edit = app_prefs.edit();
        edit.putString(PHONE, phone);
        edit.commit();
    }

    public String getPhone() {
        return app_prefs.getString(PHONE, "");
    }

    public void putDob(String dob) {
        Editor edit = app_prefs.edit();
        edit.putString(DOB, dob);
        edit.commit();
    }

    public String getDob() {
        return app_prefs.getString(DOB, "");
    }

    public void putLastName(String l_name) {
        Editor edit = app_prefs.edit();
        edit.putString(LASTNAME, l_name);
        edit.commit();
    }

    public String getLastName() {
        return app_prefs.getString(LASTNAME, "");
    }

    public void putIsLogin(boolean isLogin) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(ISLOGIN, isLogin);
        edit.commit();
    }

    public Boolean getIsLogin() {
        return app_prefs.getBoolean(ISLOGIN, false);
    }

    public void putDeviceToken(String refreshedToken) {
        Editor edit = app_prefs.edit();
        edit.putString(DEVICE_TOKEN, refreshedToken);
        edit.commit();
    }

    public String getDeviceToken() {
        return app_prefs.getString(DEVICE_TOKEN, "");
    }

    public void putGender(String gender) {
        Editor edit = app_prefs.edit();
        edit.putString(GENDER, gender);
        edit.commit();
    }

    public String getGender() {
        return app_prefs.getString(GENDER, "");
    }

    public void putAge(String age) {
        Editor edit = app_prefs.edit();
        edit.putString(AGE, age);
        edit.commit();
    }
    public String getAge() {
        return app_prefs.getString(AGE, "");
    }

    public void putEducation(String education) {
        Editor edit = app_prefs.edit();
        edit.putString(EDUCATION, education);
        edit.commit();
    }
    public String getEducation() {
        return app_prefs.getString(EDUCATION, "");
    }

    public void putState(String state) {
        Editor edit = app_prefs.edit();
        edit.putString(STATE, state);
        edit.commit();
    }

    public String getState() {
        return app_prefs.getString(STATE, "");
    }

    public void putCountry(String country) {
        Editor edit = app_prefs.edit();
        edit.putString(COUNTRY, country);
        edit.commit();
    }

    public String getCounrty() {
        return app_prefs.getString(COUNTRY, "");
    }

    public void putEthnicity(String ethnicity) {
        Editor edit = app_prefs.edit();
        edit.putString(ETHNICITY, ethnicity);
        edit.commit();
    }
    public String getEthnicity() {
        return app_prefs.getString(ETHNICITY, "");
    }

    public void putLookinFor(String lookingfor) {
        Editor edit = app_prefs.edit();
        edit.putString(LOOKING, lookingfor);
        edit.commit();
    }
    public String getLookingFor() {
        return app_prefs.getString(LOOKING, "");
    }

    public void putAbout(String about) {
        Editor edit = app_prefs.edit();
        edit.putString(ABOUT, about);
        edit.commit();
    }

    public String getAbout() {
        return app_prefs.getString(ABOUT, "");
    }

    public void putFBID(String fb_id) {
        Editor edit = app_prefs.edit();
        edit.putString(FBID, fb_id);
        edit.commit();
    }

    public String getFBID() {
        return app_prefs.getString(FBID, "");
    }


    public Boolean getIsProfileComplete() {
        return app_prefs.getBoolean(ISPROFILE, false);
    }

    public void putIsProfileComplete(boolean isprofile) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(ISPROFILE, isprofile);
        edit.commit();
    }
}
