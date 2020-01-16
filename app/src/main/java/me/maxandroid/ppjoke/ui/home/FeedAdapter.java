package me.maxandroid.ppjoke.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import me.maxandroid.libcommon.LiveDataBus;
import me.maxandroid.ppjoke.databinding.LayoutFeedTypeImageBinding;
import me.maxandroid.ppjoke.databinding.LayoutFeedTypeVideoBinding;
import me.maxandroid.ppjoke.model.Feed;
import me.maxandroid.ppjoke.ui.detail.FeedDetailActivity;
import me.maxandroid.ppjoke.view.ListPlayerView;

public class FeedAdapter extends PagedListAdapter<Feed, FeedAdapter.ViewHolder> {

    private String category;
    private LifecycleOwner mLifecycleOwner;

    protected FeedAdapter(LifecycleOwner lifecycleOwner, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });
        mLifecycleOwner = lifecycleOwner;
        this.category = category;
    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = getItem(position);
        return feed.itemType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        if (viewType == Feed.TYPE_IMAGE) {
            binding = LayoutFeedTypeImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        } else {
            binding = LayoutFeedTypeVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(getItem(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeedDetailActivity.startFeedDetailActivity(holder.itemView.getContext(), getItem(position), category);
                if (mFeedObserver == null) {
                    mFeedObserver = new FeedObserver();
                    LiveDataBus.get()
                            .with(InteractionPresenter.DATA_FROM_INTERACTION)
                            .observe(mLifecycleOwner, mFeedObserver);
                }
                mFeedObserver.setFeed(getItem(position));
            }
        });
    }

    private FeedObserver mFeedObserver;

    private class FeedObserver implements Observer<Feed> {

        private Feed mFeed;

        @Override
        public void onChanged(Feed newOne) {
            if (mFeed.id != newOne.id)
                return;
            mFeed.author = newOne.author;
            mFeed.ugc = newOne.ugc;
            mFeed.notifyChange();
        }

        public void setFeed(Feed feed) {
            mFeed = feed;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding mBinding;
        public ListPlayerView listPlayerView;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            this.mBinding = binding;
        }

        public void bindData(Feed item) {
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                imageBinding.setLifeCycleOwner(mLifecycleOwner);
                imageBinding.setFeed(item);
                imageBinding.feedImage.bindData(item.width, item.height, 16, item.cover);
            } else {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.setLifeCycleOwner(mLifecycleOwner);
                videoBinding.setFeed(item);
                videoBinding.listPlayerView.bindData(category, item.width, item.height, item.cover, item.url);
                listPlayerView = videoBinding.listPlayerView;
            }
        }

        public boolean isVideoItem() {
            return mBinding instanceof LayoutFeedTypeVideoBinding;
        }

        public ListPlayerView getListPlayerView() {
            return listPlayerView;
        }
    }
}
