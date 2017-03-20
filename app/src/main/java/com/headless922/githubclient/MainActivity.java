package com.headless922.githubclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.headless922.githubclient.recyclerviews.RecyclerItemClickListener;
import com.headless922.githubclient.recyclerviews.UserInfoRecyclerViewAdapter;
import com.headless922.githubclient.requestmodel.SearchRequestModel;
import com.headless922.githubclient.requestmodel.UserRequestModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private UserInfoRecyclerViewAdapter mAdapter;
    private List<UserRequestModel> mUsers;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText editText;

    private boolean loading;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mUsers = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.users_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new UserInfoRecyclerViewAdapter(this, mUsers);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    Log.i("page", "end reached");

                    App.getApi().getUserListSince(mUsers.get(totalItemCount-1).getUserId()).enqueue(new Callback<List<UserRequestModel>>() {
                        @Override
                        public void onResponse(Call<List<UserRequestModel>> call, Response<List<UserRequestModel>> response) {
                            mUsers.addAll(response.body());
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<List<UserRequestModel>> call, Throwable t) {
                            Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                    });

                    loading = true;
                }
            }
        });

        App.getApi().getUserList().enqueue(new Callback<List<UserRequestModel>>() {
            @Override
            public void onResponse(Call<List<UserRequestModel>> call, Response<List<UserRequestModel>> response) {
                mUsers.addAll(response.body());
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<UserRequestModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        MainActivity.this,
                        mRecyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                view.setBackgroundColor(Color.parseColor("#f0f8ff"));
                                Intent intent = new Intent(MainActivity.this, UserInformationActivity.class);
                                intent.putExtra("username",mUsers.get(position).getLogin());
                                startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbar_item_search) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = this.getLayoutInflater().inflate(R.layout.search_dialog, null);
            builder.setView(view);
            builder.setTitle("Search users by login");
            editText = (EditText) view.findViewById(R.id.edit_text_search_dialog);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String login = editText.getText().toString();
                    if (!"".equals(login)) {
                        App.getApi().searchUserByLogin(login).enqueue(new Callback<SearchRequestModel>() {
                            @Override
                            public void onResponse(Call<SearchRequestModel> call, Response<SearchRequestModel> response) {
                                mUsers.clear();
                                mUsers.addAll(response.body().getUserList());
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                            @Override
                            public void onFailure(Call<SearchRequestModel> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }
                    else Toast.makeText(MainActivity.this, "It was necessary to enter login.", Toast.LENGTH_LONG).show();
                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка Отмена
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
