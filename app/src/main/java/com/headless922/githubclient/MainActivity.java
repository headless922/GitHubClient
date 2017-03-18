package com.headless922.githubclient;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.headless922.githubclient.recyclerviews.RecyclerItemClickListener;
import com.headless922.githubclient.recyclerviews.UserInfoRecyclerViewAdapter;
import com.headless922.githubclient.requestmodel.UserRequestModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;
    private List<UserRequestModel> mUsers;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mUsers = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.users_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        UserInfoRecyclerViewAdapter adapter = new UserInfoRecyclerViewAdapter(this, mUsers);
        mRecyclerView.setAdapter(adapter);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);

        App.getApi().getUserList().enqueue(new Callback<List<UserRequestModel>>() {
            @Override
            public void onResponse(Call<List<UserRequestModel>> call, Response<List<UserRequestModel>> response) {
                mUsers.addAll(response.body());
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<UserRequestModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        MainActivity.this,
                        mRecyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(MainActivity.this, UserInformationActivity.class);
                                intent.putExtra("username",mUsers.get(position).getLogin());
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }));
    }

    @Override
    public void onRefresh() {
        /*int lastId;
        lastId = mUsers.get(mUsers.size()-1).getUserId();*/
        App.getApi().getUserList().enqueue(new Callback<List<UserRequestModel>>() {
            @Override
            public void onResponse(Call<List<UserRequestModel>> call, Response<List<UserRequestModel>> response) {
                mUsers.clear();
                mUsers.addAll(response.body());
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<UserRequestModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
