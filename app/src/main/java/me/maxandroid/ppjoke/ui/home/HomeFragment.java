package me.maxandroid.ppjoke.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import me.maxandroid.libnavannotation.FragmentDestination;
import me.maxandroid.ppjoke.exoplayer.PageListPlayerDetector;
import me.maxandroid.ppjoke.exoplayer.PageListPlayerManager;
import me.maxandroid.ppjoke.model.Feed;
import me.maxandroid.ppjoke.ui.AbsListFragment;
import me.maxandroid.ppjoke.ui.MutableDataSource;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {
    private PageListPlayerDetector playDetector;
    private String feedType;


    public static HomeFragment newInstance(String feedType) {

        Bundle args = new Bundle();
        args.putString("feedType", feedType);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getCacheLiveData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                adapter.submitList(feeds);
            }
        });
        playDetector = new PageListPlayerDetector(this, mRecyclerView);
        mViewModel.setFeedType(feedType);
    }

    @Override
    public PagedListAdapter getAdapter() {
        String feedType = (getArguments() == null || getArguments().getString("feedType") == null) ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(this, feedType) {
            @Override
            public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.getListPlayerView());
                }

            }

            @Override
            public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                playDetector.removeTarget(holder.getListPlayerView());
            }
        };
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        Feed feed = adapter.getCurrentList().get(adapter.getItemCount() - 1);
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {
            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = adapter.getCurrentList().getConfig();
                if (data != null && data.size() > 0) {
                    MutableDataSource dataSource = new MutableDataSource();
                    dataSource.data.addAll(data);
                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    submitList(pagedList);
                }
            }
        });

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getDataSource().invalidate();
    }


    @Override
    public void onPause() {
        playDetector.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        playDetector.onResume();
    }


    @Override
    public void onDestroy() {
        PageListPlayerManager.release(feedType);
        super.onDestroy();
    }
}
