package me.maxandroid.ppjoke.ui.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;

import me.maxandroid.libcommon.AppGlobals;
import me.maxandroid.network.libnetwork.ApiResponse;
import me.maxandroid.network.libnetwork.ApiService;
import me.maxandroid.network.libnetwork.JsonCallback;
import me.maxandroid.ppjoke.model.Feed;
import me.maxandroid.ppjoke.model.User;
import me.maxandroid.ppjoke.ui.login.UserManager;

public class InteractionPresenter {

    private static final String URL_TOGGLE_FEED_LIK = "/ugc/toggleFeedLike";

    private static final String URL_TOGGLE_FEED_DISS = "/ugc/dissFeed";

    public static void toggleFeedLike(LifecycleOwner owner, Feed feed) {
        if (!UserManager.get().isLogin()) {
            LiveData<User> loginLiveData = UserManager.get().login(AppGlobals.getApplication());
            loginLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        toggleFeedLikeInternal(feed);
                    }
                    loginLiveData.removeObserver(this);
                }
            });
            return;
        }
        toggleFeedLikeInternal(feed);
    }

    private static void toggleFeedLikeInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_LIK)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked").booleanValue();
                            feed.getUgc().setHasLiked(hasLiked);
                        }

                    }
                });
    }

    public static void toggleFeedDiss(LifecycleOwner owner, Feed feed) {
        if (!UserManager.get().isLogin()) {
            LiveData<User> loginLiveData = UserManager.get().login(AppGlobals.getApplication());
            loginLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        toggleFeedDissInternal(feed);
                    }
                    loginLiveData.removeObserver(this);
                }
            });
            return;
        }

        toggleFeedDissInternal(feed);
    }

    private static void toggleFeedDissInternal(Feed feed) {

        ApiService.get(URL_TOGGLE_FEED_DISS).addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked").booleanValue();
                            feed.getUgc().setHasdiss(hasLiked);
                        }
                    }
                });
    }

}
