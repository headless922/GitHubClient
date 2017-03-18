package com.headless922.githubclient.recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.headless922.githubclient.R;
import com.headless922.githubclient.requestmodel.RepoRequestModel;

import java.net.URL;
import java.util.List;

/**
 * Created by Headless922 on 18.03.2017.
 */

public class ReposRecyclerViewAdapter extends RecyclerView.Adapter<ReposRecyclerViewAdapter.ReposViewHolder>{

    private Context mContext;
    private List<RepoRequestModel> mRepos;

    public ReposRecyclerViewAdapter(Context context, List<RepoRequestModel> reposList) {
        this.mContext = context;
        this.mRepos = reposList;
    }

    @Override
    public ReposViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repos_recycler_view, parent, false);
        return new ReposViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReposViewHolder holder, final int position) {
        holder.textViewName.setText(mRepos.get(position).getName());
        holder.textViewDescription.setText(mRepos.get(position).getDescription());
        holder.buttonOpenInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRepos.get(position).getUrl()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mRepos == null)
            return 0;
        return mRepos.size();
    }

    class ReposViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private TextView textViewDescription;
        private Button buttonOpenInBrowser;

        public ReposViewHolder(View view) {
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.text_view_repos_name);
            this.textViewDescription = (TextView) view.findViewById(R.id.text_view_repos_description);
            this.buttonOpenInBrowser = (Button) view.findViewById(R.id.button_show_repos_in_browser);
        }
    }
}
