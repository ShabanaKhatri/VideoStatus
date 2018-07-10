/*
package com.ploodi.parse;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.singleafghans.model.MessageModel;
import com.singleafghans.model.MessageUserList;
import com.singleafghans.model.Register;
import com.singleafghans.model.Users;
import com.singleafghans.utills.AndyConstants;
import com.singleafghans.utills.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


*/
/**
 * @author Kishan H Dhamat , Email: kishan.dhamat105@gmail.com
 *//*

public class ParseContent {

    private static final String TAG = "ParseContent";
    private Context context;
    private PreferenceHelper preferenceHelper;
    public final String KEY_SUCCESS = "success";
    private String strmessage;

    public ParseContent(Context context) {
        this.context = context;
        preferenceHelper = new PreferenceHelper(context);
    }

    public boolean isSuccess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getBoolean(AndyConstants.Params.STATUS);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String isnotSuccess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!jsonObject.getBoolean(AndyConstants.Params.STATUS)) {
                strmessage = jsonObject.getString(AndyConstants.Params.MESSAGE);
                return strmessage;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean parseLogin(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(AndyConstants.Params.STATUS)) {
                jsonObject = jsonObject.getJSONObject(AndyConstants.Params.DATA);

                preferenceHelper.putIsLogin(true);
                preferenceHelper.putPassword(jsonObject.getString(AndyConstants.Params.PASSWORD) + "");
                preferenceHelper.putUserId(jsonObject.getString(AndyConstants.Params.USER_ID) + "");
                preferenceHelper.putEmail(jsonObject.getString(AndyConstants.Params.EMAIL) + "");
                preferenceHelper.putUserImage(jsonObject.getString(AndyConstants.Params.IMAGE) + "");

                if (jsonObject.has(AndyConstants.Params.AGE) && !TextUtils.isEmpty(jsonObject.optString(AndyConstants.Params.AGE)) && Integer.parseInt(jsonObject.optString(AndyConstants.Params.AGE)) >= 18) {
                    preferenceHelper.putIsProfileComplete(true);
                }
                preferenceHelper.putLastName(jsonObject.getString(AndyConstants.Params.NAME) + "");
                preferenceHelper.putGender(jsonObject.getString(AndyConstants.Params.GENDER) + "");
                preferenceHelper.putAge(jsonObject.getString(AndyConstants.Params.AGE) + "");
                preferenceHelper.putEducation(jsonObject.getString(AndyConstants.Params.EDUCATION) + "");


                preferenceHelper.putState(jsonObject.getString(AndyConstants.Params.STATE) + "");
                preferenceHelper.putCountry(jsonObject.getString(AndyConstants.Params.COUNTRY) + "");
                preferenceHelper.putEthnicity(jsonObject.getString(AndyConstants.Params.ETHNICITY) + "");
                preferenceHelper.putLookinFor(jsonObject.getString(AndyConstants.Params.LOOKING) + "");
                preferenceHelper.putAbout(jsonObject.getString(AndyConstants.Params.ABOUT) + "");
                preferenceHelper.putFBID(jsonObject.getString(AndyConstants.Params.FB_ID) + "");

                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String parseSuccessId(String response) {
        String userid = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(AndyConstants.Params.STATUS)) {
                jsonObject = jsonObject.getJSONObject(AndyConstants.Params.DATA);
                userid = jsonObject.getString(AndyConstants.Params.USER_ID);
                return userid;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userid;
    }

    public ArrayList<Register> getRegister(String response) {
        ArrayList<Register> registerArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has(AndyConstants.Params.DATA)) {
                JSONObject jsonObjectData = jsonObject.optJSONObject(AndyConstants.Params.DATA);
                Register register = new Register();

                if (jsonObjectData.has(AndyConstants.Params.USER_ID)) {
                    register.setUser_id(jsonObjectData.optString(AndyConstants.Params.USER_ID));
                    preferenceHelper.putUserId(jsonObjectData.optString(AndyConstants.Params.USER_ID));
                }
                if (jsonObjectData.has(AndyConstants.Params.FB_ID)) {
                    register.setFbid(jsonObjectData.optString(AndyConstants.Params.FB_ID));
                }
                if (jsonObjectData.has(AndyConstants.Params.USER_NAME)) {
                    register.setUser_name(jsonObjectData.optString(AndyConstants.Params.USER_NAME));
                }
                if (jsonObjectData.has(AndyConstants.Params.EMAIL)) {
                    register.setEmail(jsonObjectData.optString(AndyConstants.Params.EMAIL));
                }
                if (jsonObjectData.has(AndyConstants.Params.PASSWORD)) {
                    register.setPassword(jsonObjectData.optString(AndyConstants.Params.PASSWORD));
                }
                if (jsonObjectData.has(AndyConstants.Params.IMAGE)) {
                    register.setImage(jsonObjectData.optString(AndyConstants.Params.IMAGE));
                }
                if (jsonObjectData.has(AndyConstants.Params.NAME)) {
                    register.setName(jsonObjectData.optString(AndyConstants.Params.NAME));
                }
                if (jsonObjectData.has(AndyConstants.Params.GENDER)) {
                    register.setGender(jsonObjectData.optString(AndyConstants.Params.GENDER));
                }
                if (jsonObjectData.has(AndyConstants.Params.AGE)) {
                    register.setAge(jsonObjectData.optString(AndyConstants.Params.AGE));
                }
                if (jsonObjectData.has(AndyConstants.Params.EDUCATION)) {
                    register.setEducation(jsonObjectData.optString(AndyConstants.Params.EDUCATION));
                }
                if (jsonObjectData.has(AndyConstants.Params.ABOUT)) {
                    register.setAbout(jsonObjectData.optString(AndyConstants.Params.ABOUT));
                }
                if (jsonObjectData.has(AndyConstants.Params.LOOKING)) {
                    register.setLooking(jsonObjectData.optString(AndyConstants.Params.LOOKING));
                }
                if (jsonObjectData.has(AndyConstants.Params.ETHNICITY)) {
                    register.setEthnicity(jsonObjectData.optString(AndyConstants.Params.ETHNICITY));
                }
                if (jsonObjectData.has(AndyConstants.Params.COUNTRY)) {
                    register.setCountry(jsonObjectData.optString(AndyConstants.Params.COUNTRY));
                }
                if (jsonObjectData.has(AndyConstants.Params.STATE)) {
                    register.setState(jsonObjectData.optString(AndyConstants.Params.STATE));
                }
                if (jsonObjectData.has(AndyConstants.Params.CREATEDAT)) {
                    register.setCreatedAt(jsonObjectData.optString(AndyConstants.Params.CREATEDAT));
                }
                Log.d(TAG, "getRegister:" + register);
                registerArrayList.add(register);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return registerArrayList;
    }

    public ArrayList<Register> getUserProfile(String response) {
        ArrayList<Register> registerArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has(AndyConstants.Params.DATA)) {
                JSONObject jsonObjectData = jsonObject.optJSONObject(AndyConstants.Params.DATA);
                Register register = new Register();

                if (jsonObjectData.has(AndyConstants.Params.USER_ID)) {
                    register.setUser_id(jsonObjectData.optString(AndyConstants.Params.USER_ID));
                }
                if (jsonObjectData.has(AndyConstants.Params.FB_ID)) {
                    register.setFbid(jsonObjectData.optString(AndyConstants.Params.FB_ID));
                }
                if (jsonObjectData.has(AndyConstants.Params.USER_NAME)) {
                    register.setUser_name(jsonObjectData.optString(AndyConstants.Params.USER_NAME));
                }
                if (jsonObjectData.has(AndyConstants.Params.EMAIL)) {
                    register.setEmail(jsonObjectData.optString(AndyConstants.Params.EMAIL));
                }
                if (jsonObjectData.has(AndyConstants.Params.PASSWORD)) {
                    register.setPassword(jsonObjectData.optString(AndyConstants.Params.PASSWORD));
                }
                if (jsonObjectData.has(AndyConstants.Params.IMAGE)) {
                    register.setImage(jsonObjectData.optString(AndyConstants.Params.IMAGE));
                }
                if (jsonObjectData.has(AndyConstants.Params.NAME)) {
                    register.setName(jsonObjectData.optString(AndyConstants.Params.NAME));
                }
                if (jsonObjectData.has(AndyConstants.Params.GENDER)) {
                    register.setGender(jsonObjectData.optString(AndyConstants.Params.GENDER));
                }
                if (jsonObjectData.has(AndyConstants.Params.AGE)) {
                    register.setAge(jsonObjectData.optString(AndyConstants.Params.AGE));
                }
                if (jsonObjectData.has(AndyConstants.Params.EDUCATION)) {
                    register.setEducation(jsonObjectData.optString(AndyConstants.Params.EDUCATION));
                }
                if (jsonObjectData.has(AndyConstants.Params.ABOUT)) {
                    register.setAbout(jsonObjectData.optString(AndyConstants.Params.ABOUT));
                }
                if (jsonObjectData.has(AndyConstants.Params.LOOKING)) {
                    register.setLooking(jsonObjectData.optString(AndyConstants.Params.LOOKING));
                }
                if (jsonObjectData.has(AndyConstants.Params.ETHNICITY)) {
                    register.setEthnicity(jsonObjectData.optString(AndyConstants.Params.ETHNICITY));
                }
                if (jsonObjectData.has(AndyConstants.Params.COUNTRY)) {
                    register.setCountry(jsonObjectData.optString(AndyConstants.Params.COUNTRY));
                }
                if (jsonObjectData.has(AndyConstants.Params.STATE)) {
                    register.setState(jsonObjectData.optString(AndyConstants.Params.STATE));
                }
                if (jsonObjectData.has(AndyConstants.Params.CREATEDAT)) {
                    register.setCreatedAt(jsonObjectData.optString(AndyConstants.Params.CREATEDAT));
                }
                Log.d(TAG, "getRegister:" + register);
                registerArrayList.add(register);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return registerArrayList;
    }

    public ArrayList<Users> getNearest(String response) {
        ArrayList<Users> registerArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has(AndyConstants.Params.DATA)) {
                JSONArray jsonArrayData = jsonObject.optJSONArray(AndyConstants.Params.DATA);
                for (int i = 0; i < jsonArrayData.length(); i++) {
                    JSONObject jsonObjectData = jsonArrayData.optJSONObject(i);

                    Users register = new Users();

                    if (jsonObjectData.has(AndyConstants.Params.USER_ID)) {
                        register.setUser_id(jsonObjectData.optString(AndyConstants.Params.USER_ID));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.FB_ID)) {
                        register.setFbid(jsonObjectData.optString(AndyConstants.Params.FB_ID));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.USER_NAME)) {
                        register.setUser_name(jsonObjectData.optString(AndyConstants.Params.USER_NAME));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.EMAIL)) {
                        register.setEmail(jsonObjectData.optString(AndyConstants.Params.EMAIL));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.PASSWORD)) {
                        register.setPassword(jsonObjectData.optString(AndyConstants.Params.PASSWORD));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.IMAGE)) {
                        register.setImage(jsonObjectData.optString(AndyConstants.Params.IMAGE));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.NAME)) {
                        register.setName(jsonObjectData.optString(AndyConstants.Params.NAME));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.GENDER)) {
                        register.setGender(jsonObjectData.optString(AndyConstants.Params.GENDER));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.AGE)) {
                        register.setAge(jsonObjectData.optString(AndyConstants.Params.AGE));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.EDUCATION)) {
                        register.setEducation(jsonObjectData.optString(AndyConstants.Params.EDUCATION));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.ABOUT)) {
                        register.setAbout(jsonObjectData.optString(AndyConstants.Params.ABOUT));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.LOOKING)) {
                        register.setLooking(jsonObjectData.optString(AndyConstants.Params.LOOKING));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.ETHNICITY)) {
                        register.setEthnicity(jsonObjectData.optString(AndyConstants.Params.ETHNICITY));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.COUNTRY)) {
                        register.setCountry(jsonObjectData.optString(AndyConstants.Params.COUNTRY));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.STATE)) {
                        register.setState(jsonObjectData.optString(AndyConstants.Params.STATE));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.CREATEDAT)) {
                        register.setCreatedAt(jsonObjectData.optString(AndyConstants.Params.CREATEDAT));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.IS_FAVORITE)) {
                        register.setIs_favorite(jsonObjectData.optString(AndyConstants.Params.IS_FAVORITE));
                    }


                    registerArrayList.add(register);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return registerArrayList;
    }

    public ArrayList<MessageModel> getChatHistroy(String response, ArrayList<MessageModel> messageModelArrayList) {


        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("data")) {
                JSONArray jsonArrayData = jsonObject.optJSONArray("data");
                for (int i = 0; i < jsonArrayData.length(); i++) {
                    MessageModel messageModel = new MessageModel();
                    JSONObject jsonObjectInside = jsonArrayData.optJSONObject(i);
                    if (jsonObjectInside.has(AndyConstants.Params.SENDER_ID)) {
                        messageModel.setSender_id(jsonObjectInside.optString(AndyConstants.Params.SENDER_ID));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.CHAT_ID)) {
                        messageModel.setChat_id(jsonObjectInside.optString(AndyConstants.Params.CHAT_ID));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.RECIVER_ID)) {
                        messageModel.setReceiver_id(jsonObjectInside.optString(AndyConstants.Params.RECIVER_ID));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.MESSAGE_TYPE)) {
                        messageModel.setMessage_type(jsonObjectInside.optString(AndyConstants.Params.MESSAGE_TYPE));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.TEXT)) {
                        messageModel.setText(jsonObjectInside.optString(AndyConstants.Params.TEXT));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.CHAT_IMAGE)) {
                        messageModel.setChat_image(jsonObjectInside.optString(AndyConstants.Params.CHAT_IMAGE));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.SENDER_IMAGE)) {
                        messageModel.setSender_image(jsonObjectInside.optString(AndyConstants.Params.SENDER_IMAGE));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.SENDER_NAME)) {
                        messageModel.setSender_name(jsonObjectInside.optString(AndyConstants.Params.SENDER_NAME));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.ISREAD)) {
                        messageModel.setIs_read(jsonObjectInside.optString(AndyConstants.Params.ISREAD));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.TIME_ZONE)) {
                        messageModel.setTime_zone(jsonObjectInside.optString(AndyConstants.Params.TIME_ZONE));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.CREATEDAT)) {
                        messageModel.setCreatedAt(jsonObjectInside.optString(AndyConstants.Params.CREATEDAT));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.LAT)) {
                        messageModel.setLat(jsonObjectInside.optString(AndyConstants.Params.LAT));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.LONG)) {
                        messageModel.setLng(jsonObjectInside.optString(AndyConstants.Params.LONG));
                    }

                    messageModelArrayList.add(messageModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messageModelArrayList;
    }

    public ArrayList<MessageUserList> getMessageUserList(String res, ArrayList<MessageUserList> messageUserLists) {
        messageUserLists.clear();
        try {
            JSONObject jsonObject = new JSONObject(res);
            if (jsonObject.has("data")) {
                JSONArray jsonArrayData = jsonObject.optJSONArray("data");
                for (int i = 0; i < jsonArrayData.length(); i++) {
                    MessageUserList userData = new MessageUserList();
                    MessageModel messageModel = new MessageModel();
                    JSONObject jsonObjectInside = jsonArrayData.optJSONObject(i).getJSONObject("chat");
                    if (jsonObjectInside.has(AndyConstants.Params.SENDER_ID)) {
                        messageModel.setSender_id(jsonObjectInside.optString(AndyConstants.Params.SENDER_ID));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.CHAT_ID)) {
                        messageModel.setChat_id(jsonObjectInside.optString(AndyConstants.Params.CHAT_ID));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.RECIVER_ID)) {
                        messageModel.setReceiver_id(jsonObjectInside.optString(AndyConstants.Params.RECIVER_ID));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.MESSAGE_TYPE)) {
                        messageModel.setMessage_type(jsonObjectInside.optString(AndyConstants.Params.MESSAGE_TYPE));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.TEXT)) {
                        messageModel.setText(jsonObjectInside.optString(AndyConstants.Params.TEXT));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.CHAT_IMAGE)) {
                        messageModel.setChat_image(jsonObjectInside.optString(AndyConstants.Params.CHAT_IMAGE));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.SENDER_IMAGE)) {
                        messageModel.setSender_image(jsonObjectInside.optString(AndyConstants.Params.SENDER_IMAGE));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.SENDER_NAME)) {
                        messageModel.setSender_name(jsonObjectInside.optString(AndyConstants.Params.SENDER_NAME));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.ISREAD)) {
                        messageModel.setIs_read(jsonObjectInside.optString(AndyConstants.Params.ISREAD));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.TIME_ZONE)) {
                        messageModel.setTime_zone(jsonObjectInside.optString(AndyConstants.Params.TIME_ZONE));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.CREATEDAT)) {
                        messageModel.setCreatedAt(jsonObjectInside.optString(AndyConstants.Params.CREATEDAT));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.LAT)) {
                        messageModel.setLat(jsonObjectInside.optString(AndyConstants.Params.LAT));
                    }
                    if (jsonObjectInside.has(AndyConstants.Params.LONG)) {
                        messageModel.setLng(jsonObjectInside.optString(AndyConstants.Params.LONG));
                    }
                    userData.setLastMessage(messageModel);
                    JSONObject userObject = jsonArrayData.optJSONObject(i).getJSONObject("user");
                    userData.setUnreadCount(userObject.getJSONArray("unrend_mess").length());
                    JSONObject jsonObjectData = userObject;
                    Users register = new Users();
                    if (jsonObjectData.has(AndyConstants.Params.USER_ID)) {
                        register.setUser_id(jsonObjectData.optString(AndyConstants.Params.USER_ID));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.FB_ID)) {
                        register.setFbid(jsonObjectData.optString(AndyConstants.Params.FB_ID));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.USER_NAME)) {
                        register.setUser_name(jsonObjectData.optString(AndyConstants.Params.USER_NAME));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.EMAIL)) {
                        register.setEmail(jsonObjectData.optString(AndyConstants.Params.EMAIL));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.PASSWORD)) {
                        register.setPassword(jsonObjectData.optString(AndyConstants.Params.PASSWORD));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.IMAGE)) {
                        register.setImage(jsonObjectData.optString(AndyConstants.Params.IMAGE));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.NAME)) {
                        register.setName(jsonObjectData.optString(AndyConstants.Params.NAME));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.GENDER)) {
                        register.setGender(jsonObjectData.optString(AndyConstants.Params.GENDER));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.AGE)) {
                        register.setAge(jsonObjectData.optString(AndyConstants.Params.AGE));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.EDUCATION)) {
                        register.setEducation(jsonObjectData.optString(AndyConstants.Params.EDUCATION));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.ABOUT)) {
                        register.setAbout(jsonObjectData.optString(AndyConstants.Params.ABOUT));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.LOOKING)) {
                        register.setLooking(jsonObjectData.optString(AndyConstants.Params.LOOKING));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.ETHNICITY)) {
                        register.setEthnicity(jsonObjectData.optString(AndyConstants.Params.ETHNICITY));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.COUNTRY)) {
                        register.setCountry(jsonObjectData.optString(AndyConstants.Params.COUNTRY));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.STATE)) {
                        register.setState(jsonObjectData.optString(AndyConstants.Params.STATE));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.CREATEDAT)) {
                        register.setCreatedAt(jsonObjectData.optString(AndyConstants.Params.CREATEDAT));
                    }
                    if (jsonObjectData.has(AndyConstants.Params.IS_FAVORITE)) {
                        register.setIs_favorite(jsonObjectData.optString(AndyConstants.Params.IS_FAVORITE));
                    }
                    userData.setUserDetail(register);
                    messageUserLists.add(userData);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messageUserLists;
    }

    public ArrayList<MessageModel> getSingleChat(String response,ArrayList<MessageModel> messageModelArrayList) {

        messageModelArrayList.clear();
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONObject jsonObjectInside = jsonObject.optJSONObject("data");
            MessageModel messageModel = new MessageModel();

            if (jsonObjectInside.has(AndyConstants.Params.SENDER_ID)) {
                messageModel.setSender_id(jsonObjectInside.optString(AndyConstants.Params.SENDER_ID));
            }
            if (jsonObjectInside.has(AndyConstants.Params.CHAT_ID)) {
                messageModel.setChat_id(jsonObjectInside.optString(AndyConstants.Params.CHAT_ID));
            }
            if (jsonObjectInside.has(AndyConstants.Params.RECIVER_ID)) {
                messageModel.setReceiver_id(jsonObjectInside.optString(AndyConstants.Params.RECIVER_ID));
            }
            if (jsonObjectInside.has(AndyConstants.Params.MESSAGE_TYPE)) {
                Log.d(TAG, "getSingleChat:"+jsonObjectInside.optString(AndyConstants.Params.MESSAGE_TYPE));
                messageModel.setMessage_type(jsonObjectInside.optString(AndyConstants.Params.MESSAGE_TYPE));
            }
            if (jsonObjectInside.has(AndyConstants.Params.TEXT)) {
                messageModel.setText(jsonObjectInside.optString(AndyConstants.Params.TEXT));
            }
            if (jsonObjectInside.has(AndyConstants.Params.CHAT_IMAGE)) {
                messageModel.setChat_image(jsonObjectInside.optString(AndyConstants.Params.CHAT_IMAGE));
            }

            if (jsonObjectInside.has(AndyConstants.Params.ISREAD)) {
                messageModel.setIs_read(jsonObjectInside.optString(AndyConstants.Params.ISREAD));
            }
            if (jsonObjectInside.has(AndyConstants.Params.TIME_ZONE)) {
                messageModel.setTime_zone(jsonObjectInside.optString(AndyConstants.Params.TIME_ZONE));
            }
            if (jsonObjectInside.has(AndyConstants.Params.CREATEDAT)) {
                messageModel.setCreatedAt(jsonObjectInside.optString(AndyConstants.Params.CREATEDAT));
            }
            if (jsonObjectInside.has(AndyConstants.Params.LAT)) {
                messageModel.setLat(jsonObjectInside.optString(AndyConstants.Params.LAT));
            }
            if (jsonObjectInside.has(AndyConstants.Params.LONG)) {
                messageModel.setLng(jsonObjectInside.optString(AndyConstants.Params.LONG));
            }
            messageModelArrayList.add(messageModel);
            Log.d(TAG, "getSingleChat:"+messageModelArrayList.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messageModelArrayList;
    }
}
*/
