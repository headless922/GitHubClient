package com.headless922.githubclient.requestmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Headless922 on 20.03.2017.
 */

public class SearchRequestModel {

    @SerializedName("total_count")
    @Expose
    private int totalCount;

    @SerializedName("incomplete_results")
    @Expose
    private boolean incompleteResults;

    @SerializedName("items")
    @Expose
    private List<UserRequestModel> userList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isIncompleteResults() {
        return incompleteResults;
    }

    public void setIncompleteResults(boolean incompleteResults) {
        this.incompleteResults = incompleteResults;
    }

    public List<UserRequestModel> getUserList() {
        return userList;
    }

    public void setUserList(List<UserRequestModel> userList) {
        this.userList = userList;
    }
}
