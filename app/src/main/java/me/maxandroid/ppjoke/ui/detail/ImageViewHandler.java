package me.maxandroid.ppjoke.ui.detail;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import me.maxandroid.ppjoke.R;
import me.maxandroid.ppjoke.databinding.ActivityFeedDetailTypeImageBinding;
import me.maxandroid.ppjoke.databinding.LayoutFeedDetailTypeImageHeaderBinding;
import me.maxandroid.ppjoke.model.Feed;
import me.maxandroid.ppjoke.view.PPImageView;

public class ImageViewHandler extends ViewHandler {

    protected ActivityFeedDetailTypeImageBinding mImageBinding;

    protected LayoutFeedDetailTypeImageHeaderBinding mHeaderBinding;

    public ImageViewHandler(FragmentActivity activity) {
        super(activity);
        mImageBinding = DataBindingUtil.setContentView(activity, R.layout.activity_feed_detail_type_image);
        mRecyclerView = mImageBinding.recyclerView;
        mInateractionBinding = mImageBinding.interactionLayout;
    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        mImageBinding.setFeed(mFeed);

        mHeaderBinding = LayoutFeedDetailTypeImageHeaderBinding.inflate(LayoutInflater.from(mActivity), mRecyclerView, false);
        mHeaderBinding.setFeed(mFeed);

        PPImageView headerImage = mHeaderBinding.headerImage;
        headerImage.bindData(mFeed.width, mFeed.height, mFeed.width > mFeed.height ? 0 : 16, mFeed.cover);
        listAdapter.addHeaderView(mHeaderBinding.getRoot());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean visible = mHeaderBinding.getRoot().getTop() <= -mImageBinding.titleLayout.getMeasuredHeight();
                mImageBinding.authorInfoLayout.getRoot().setVisibility(visible ? View.VISIBLE : View.GONE);
                mImageBinding.title.setVisibility(visible ? View.GONE : View.VISIBLE);

            }
        });
    }
}
