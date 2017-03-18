package com.headless922.githubclient.network;

import com.headless922.githubclient.requestmodel.RepoRequestModel;
import com.headless922.githubclient.requestmodel.UserRequestModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Headless922 on 17.03.2017.
 */

public interface GitHubAPI {

    @GET("/users")
    Call<List<UserRequestModel>> getUserList();

    @GET("/users")
    Call<List<UserRequestModel>> getUserListSince(@Query("since") int userId);

    @GET("/users/{username}")
    Call<UserRequestModel> getUserInfo(@Path("username") String username);

    @GET("users/{username}/repos")
    Call<ArrayList<RepoRequestModel>> getReposList(@Path("username") String username);
}
