/*
package com.ploodi.adapter;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ploodi.R;
import com.ploodi.fragment.MemesFragment;
import com.ploodi.fragment.OnlyMemeFragment;
import com.ploodi.retrofit.DataItem;
import com.ploodi.retrofit.Response.Meme;
import com.ploodi.widgets.OnLoadMoreListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MemeAdapter";
    private final Activity activity;
    private final  ArrayList<Meme> arrayList;
    private final int VIEW_TYPE_LOADING = 1;
    private final RecyclerView mrecyclerView;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;

    public MemeAdapter(Activity activity, final  ArrayList<Meme> arrayList,RecyclerView recyclerView) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.mrecyclerView = recyclerView;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mrecyclerView.getLayoutManager();
        mrecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                Log.d(TAG, "onScrolled:---->");
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && totalItemCount < arrayList.size()) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });

    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == 11) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meme, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == 12) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_layout, parent, false);
            return new ViewHolder2(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        return position;
        if (position != 0) {
            if (arrayList.get(position) == null) {
                return VIEW_TYPE_LOADING;
            } else if (position % 3 == 0) {
                return 12;
            } else {
                return 11;
            }
        } else {
            return 11;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == 11) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.tv_meme_name.setText(arrayList.get(position).getMeme_name());

            Picasso.with(activity)
                    .load(arrayList.get(position).getMeme_link())
                    .into(itemViewHolder.img_meme);


            itemViewHolder.mImageViewShareMeme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    // Add data to the intent, the receiving app will decide
                    // what to do with it.
                    share.putExtra(Intent.EXTRA_SUBJECT, arrayList.get(position).getMeme_name());
                    share.putExtra(Intent.EXTRA_TEXT, arrayList.get(position).getMeme_link());

                    activity.startActivity(Intent.createChooser(share, "Share Meme!"));
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else if (holder instanceof ViewHolder2) {
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            AdRequest adRequest = new AdRequest.Builder().build();
            viewHolder2.mAdView.loadAd(adRequest);

        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_meme;
        private final TextView tv_meme_name;
        private final ImageView mImageViewShareMeme;


        public ItemViewHolder(View itemView) {
            super(itemView);

            img_meme = itemView.findViewById(R.id.img_meme);
            tv_meme_name = itemView.findViewById(R.id.tv_meme_name);
            mImageViewShareMeme = itemView.findViewById(R.id.mImageViewShareMeme);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        private final AdView mAdView;

        public ViewHolder2(View itemView) {
            super(itemView);
            mAdView = (AdView) itemView.findViewById(R.id.adView);


        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }
}
*/
