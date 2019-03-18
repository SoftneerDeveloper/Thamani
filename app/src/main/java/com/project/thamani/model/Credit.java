package com.project.thamani.model;



import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Credit {

@SerializedName("data")
@Expose
private List<All> data = null;

public List<All> getAll() {
return data;
}

public void setAll(List<All> data) {
this.data = data;
}

}

