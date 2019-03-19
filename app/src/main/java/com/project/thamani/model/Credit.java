package com.project.thamani.model;



import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Credit {

@SerializedName("data")
@Expose
private List<All> all = null;

public List<All> getAll() {
return all;
}

public void setAll(List<All> all) {
this.all = all;
}

}

