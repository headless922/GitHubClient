package com.headless922.githubclient;

import android.app.Application;

import com.headless922.githubclient.network.GitHubAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Headless922 on 17.03.2017.
 */

public class App extends Application {

    private static GitHubAPI gitHubApi;

    @Override
    public void onCreate() {

        super.onCreate();
        initRetrofit();
    }

    private static void initRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gitHubApi = retrofit.create(GitHubAPI.class);
    }

    public static GitHubAPI getApi() {
        return gitHubApi;
    }
}
