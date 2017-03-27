package com.headless922.githubclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.headless922.githubclient.requestmodel.UserRequestModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInformationActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextViewLogin;
    private TextView mTextViewName;
    private TextView mTextViewCompany;
    private TextView mTextViewEmail;

    private UserRequestModel mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user_activity);
        toolbar.setTitle(getIntent().getStringExtra("username") + "'s information");
        setSupportActionBar(toolbar);

        mImageView = (ImageView) findViewById(R.id.image_view_user_avatar);
        mTextViewLogin = (TextView) findViewById(R.id.text_view_user_login);
        mTextViewName = (TextView) findViewById(R.id.text_view_user_full_name);
        mTextViewCompany = (TextView) findViewById(R.id.text_view_user_company);
        mTextViewEmail = (TextView) findViewById(R.id.text_view_user_email);

        App.getApi().getUserInfo(getIntent().getStringExtra("username")).enqueue(new Callback<UserRequestModel>() {
            @Override
            public void onResponse(Call<UserRequestModel> call, Response<UserRequestModel> response) {
                mUserInfo = response.body();
                Glide.with(UserInformationActivity.this)
                        .load(mUserInfo.getAvatarUrl())
                        .into(mImageView);
                String bufString = mTextViewLogin.getText() + mUserInfo.getLogin();
                mTextViewLogin.setText(bufString);
                bufString = mTextViewName.getText() + mUserInfo.getName();
                mTextViewName.setText(bufString);
                if(mUserInfo.getCompany() != null) {
                    bufString = mTextViewCompany.getText() + mUserInfo.getCompany();
                    mTextViewCompany.setText(bufString);
                }
                else mTextViewCompany.setVisibility(View.GONE);
                bufString = mTextViewEmail.getText() + mUserInfo.getEmail();
                mTextViewEmail.setText(bufString);
            }

            @Override
            public void onFailure(Call<UserRequestModel> call, Throwable t) {
                Toast.makeText(UserInformationActivity.this, "Error occured during networking.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickShowUserInformationButton(View view) {
        Intent intent = new Intent(UserInformationActivity.this,UserRepositoriesActivity.class);
        intent.putExtra("username",mUserInfo.getLogin());
        startActivity(intent);
    }
}
