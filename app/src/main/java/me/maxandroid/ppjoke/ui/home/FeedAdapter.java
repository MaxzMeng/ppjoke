package me.maxandroid.ppjoke.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import me.maxandroid.ppjoke.R;
import me.maxandroid.ppjoke.databinding.LayoutFeedTypeImageBinding;
import me.maxandroid.ppjoke.databinding.LayoutFeedTypeVideoBinding;
import me.maxandroid.ppjoke.model.Feed;

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
        this.category = category;
        this.mLifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        if (viewType == Feed.TYPE_IMAGE_TEXT) {
            binding = LayoutFeedTypeImageBinding.inflate(LayoutInflater.from(parent.getContext()));
        } else {
            binding = LayoutFeedTypeVideoBinding.inflate(LayoutInflater.from(parent.getContext()));
        }
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = getItem(position);
        if (feed.itemType == Feed.TYPE_IMAGE_TEXT) {
            return R.layout.layout_feed_type_image;
        } else if (feed.itemType == Feed.TYPE_VIDEO) {
            return R.layout.layout_feed_type_video;
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding mBinding;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            this.mBinding = binding;
        }

        public void bindData(Feed item) {
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                imageBinding.setLifecycleOwner(mLifecycleOwner);
                imageBinding.setFeed(item);
                imageBinding.feedImage.bindData(item.width, item.height, 16, item.cover);
            } else {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.setFeed(item);
                videoBinding.setLifecycleOwner(mLifecycleOwner);
                videoBinding.listPlayerView.bindData(category, item.width, item.height, item.cover, item.url);
            }
        }
    }
}
