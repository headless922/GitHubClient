package com.headless922.githubclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user_repositories);
        toolbar.setTitle(getIntent().getStringExtra("username") + "'s repositories");
        setSupportActionBar(toolbar);

        mReposList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.repos_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        ReposRecyclerViewAdapter adapter = new ReposRecyclerViewAdapter(this, mReposList);
        mRecyclerView.setAdapter(adapter);

        App.getApi().getReposList(getIntent().getStringExtra("username"))
                .enqueue(new Callback<ArrayList<RepoRequestModel>>() {
            @Override
            public void onResponse(Call<ArrayList<RepoRequestModel>> call,
                                   Response<ArrayList<RepoRequestModel>> response) {
                if (response.code() == 200) {
                    if (!response.body().isEmpty()) {
                        mReposList.addAll(response.body());
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        Toast.makeText(UserRepositoriesActivity.this,
                                "There are no repositories.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 403) {
                    Toast.makeText(UserRepositoriesActivity.this,
                            getString(R.string.request_limit),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserRepositoriesActivity.this,
                            getString(R.string.something_wrong),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RepoRequestModel>> call, Throwable t) {
                Toast.makeText(UserRepositoriesActivity.this,
                        getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
