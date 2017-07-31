package com.headless922.githubclient.recyclerviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.headless922.githubclient.R;
import com.headless922.githubclient.requestmodel.UserRequestModel;

import java.util.List;

public class UserInfoRecyclerViewAdapter extends RecyclerView.Adapter<UserInfoRecyclerViewAdapter.CustomViewHolder>{

    private Context mContext;
    private List<UserRequestModel> mUsers;

    public UserInfoRecyclerViewAdapter(Context context, List<UserRequestModel> userList) {
        this.mContext = context;
        this.mUsers = userList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_view, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {

        Glide.with(mContext)
                .load(mUsers.get(position).getAvatarUrl()).asBitmap().centerCrop()
                .into(new BitmapImageViewTarget(holder.imageViewAvatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable roundedBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        roundedBitmapDrawable.setCircular(true);
                        holder.imageViewAvatar.setImageDrawable(roundedBitmapDrawable);
                    }
                });

        holder.textViewUsername.setText(mUsers.get(position).getLogin());
    }

    @Override
    public int getItemCount() {
        if (mUsers == null)
                return 0;
        return mUsers.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageViewAvatar;
        private TextView textViewUsername;

        public CustomViewHolder(View view) {
            super(view);
            this.imageViewAvatar = (ImageView) view.findViewById(R.id.item_image_view);
            this.textViewUsername = (TextView) view.findViewById(R.id.item_text_view);
        }
    }
}
