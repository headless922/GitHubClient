package com.headless922.githubclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.headless922.githubclient.recyclerviews.ReposRecyclerViewAdapter;
import com.headless922.githubclient.requestmodel.RepoRequestModel;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepositoriesActivity extends AppCompatActivity {

    private ArrayList<RepoRequestModel> mReposList;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_repositories);

        mReposList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.repos_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        ReposRecyclerViewAdapter adapter = new ReposRecyclerViewAdapter(this, mReposList);
        mRecyclerView.setAdapter(adapter);

        App.getApi().getReposList(getIntent().getStringExtra("username")).enqueue(new Callback<ArrayList<RepoRequestModel>>() {
            @Override
            public void onResponse(Call<ArrayList<RepoRequestModel>> call, Response<ArrayList<RepoRequestModel>> response) {
                mReposList.addAll(response.body());
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<RepoRequestModel>> call, Throwable t) {
                Toast.makeText(UserRepositoriesActivity.this, "The error occured during networking.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickOpenRepoInBrowser(View view) {
        Toast.makeText(this, "asdasdasdasdasdasdasdasd", Toast.LENGTH_SHORT).show();
    }
}
