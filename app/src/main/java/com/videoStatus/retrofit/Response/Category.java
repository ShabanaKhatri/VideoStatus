package com.videoStatus.retrofit.Response;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Shabana Khatri on 22-06-2018.
 */

public class Category implements Serializable {
    @SerializedName("Id")
    private String Id;
    @SerializedName("Parameter")
    private String Parameter;
    @SerializedName("Icon")
    private String Icon;
    @SerializedName("Order")
    private String Order;
    @SerializedName("Status")
    private String Status;
    @SerializedName("CategoryName")
    private String categoryName;

    //Change below parameter type with string while integrating webservice
    private Drawable imgRes;


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Drawable getImgRes() {
        return imgRes;
    }

    public void setImgRes(Drawable imgRes) {
        this.imgRes = imgRes;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getParameter() {
        return Parameter;
    }

    public void setParameter(String parameter) {
        Parameter = parameter;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
