package me.maxandroid.ppjoke.ui.home;

import androidx.paging.PagedListAdapter;

import me.maxandroid.libnavannotation.FragmentDestination;
import me.maxandroid.ppjoke.model.Feed;
import me.maxandroid.ppjoke.ui.AbsListFragment;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {

    @Override
    public PagedListAdapter getAdapter() {
        String feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(feedType);
    }
}
