package com.project.thamani.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Items {

@SerializedName("data")
@Expose
private List<Note> data = null;

public List<Note> getData() {
return data;
}

public void setData(List<Note> data) {
this.data = data;
}

}