package com.headless922.githubclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
        UserInfoRecyclerViewAdapter mAdapter = new UserInfoRecyclerViewAdapter(this, mUsers);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading && (totalItemCount > previousTotal)) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    int userId = mUsers.get(totalItemCount - 1).getUserId();
                    App.getApi().getUserListSince(userId).enqueue(new Callback<List<UserRequestModel>>() {
                        @Override
                        public void onResponse(Call<List<UserRequestModel>> call, Response<List<UserRequestModel>> response) {
                            switch (response.code()) {
                                case 200:
                                    mUsers.addAll(response.body());
                                    mRecyclerView.getAdapter()
                                            .notifyItemRangeInserted(totalItemCount - 1, 30);
                                    loading = true;
                                    break;
                                case 403:
                                    Toast.makeText(
                                            MainActivity.this,
                                            R.string.request_limit,
                                            Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(
                                            MainActivity.this,
                                            getString(R.string.something_wrong) + response.code(),
                                            Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                        @Override
                        public void onFailure(Call<List<UserRequestModel>> call, Throwable t) {
                            Toast.makeText(
                                    MainActivity.this,
                                    getString(R.string.network_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        requestUserList();

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        MainActivity.this,
                        mRecyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(MainActivity.this,
                                        UserInformationActivity.class);
                                intent.putExtra("username",mUsers.get(position).getLogin());
                                startActivity(intent);
                            }
                        }));
    }

    private void requestUserList() {
        App.getApi().getUserList().enqueue(new Callback<List<UserRequestModel>>() {
            @Override
            public void onResponse(Call<List<UserRequestModel>> call,
                                   Response<List<UserRequestModel>> response) {
                switch (response.code()) {
                    case 200:
                        if (!mUsers.isEmpty()) {
                            mUsers.clear();
                        }
                        mUsers.addAll(response.body());
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        break;
                    case 403:
                        Toast.makeText(
                                MainActivity.this,
                                getString(R.string.request_limit),
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(
                                MainActivity.this,
                                getString(R.string.something_wrong) + response.code(),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<UserRequestModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        App.getApi().getUserList().enqueue(new Callback<List<UserRequestModel>>() {
            @Override
            public void onResponse(Call<List<UserRequestModel>> call,
                                   Response<List<UserRequestModel>> response) {
                switch (response.code()) {
                    case 200:
                        if (!mUsers.isEmpty()) {
                            mUsers.clear();
                        }
                        mUsers.addAll(response.body());
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        previousTotal = 0;
                        break;
                    case 403:
                        Toast.makeText(MainActivity.this, getString(R.string.request_limit), Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    default:
                        Toast.makeText(
                                MainActivity.this,
                                getString(R.string.something_wrong) + response.code(),
                                Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<UserRequestModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
                        requestSearchUsersByLogin(login);
                        dialog.dismiss();
                    }
                    else Toast.makeText(MainActivity.this, "It was necessary to enter login.", Toast.LENGTH_LONG).show();
                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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

    private void requestSearchUsersByLogin(String login) {
        App.getApi().searchUserByLogin(login).enqueue(new Callback<SearchRequestModel>() {
            @Override
            public void onResponse(Call<SearchRequestModel> call, Response<SearchRequestModel> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body().getTotalCount() != 0) {
                            mUsers.clear();
                            mUsers.addAll(response.body().getUserList());
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                            break;
                        } else {
                            Toast.makeText(MainActivity.this, "No users found", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    case 403:
                        Toast.makeText(MainActivity.this, "Request's limit is out. (60 per hour)", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(
                                MainActivity.this,
                                getString(R.string.something_wrong) + response.code(),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onFailure(Call<SearchRequestModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
