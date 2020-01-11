package me.maxandroid.ppjoke.ui.home;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import me.maxandroid.network.libnetwork.ApiResponse;
import me.maxandroid.network.libnetwork.ApiService;
import me.maxandroid.network.libnetwork.JsonCallback;
import me.maxandroid.network.libnetwork.Request;
import me.maxandroid.ppjoke.AbsViewModel;
import me.maxandroid.ppjoke.model.Feed;
import me.maxandroid.ppjoke.ui.MutableDataSource;
import me.maxandroid.ppjoke.ui.login.UserManager;

public class HomeViewModel extends AbsViewModel<Feed> {

    private volatile boolean witchCache = true;
    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();
    private AtomicBoolean loadAfter = new AtomicBoolean(false);
    private String mFeedType;

    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }

    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    public void setFeedType(String feedType) {
        mFeedType = feedType;
    }

    class FeedDataSource extends ItemKeyedDataSource<Integer, Feed> {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            //加载初始化数据的
            Log.e("homeviewmodel", "loadInitial: ");
            loadData(0, params.requestedLoadSize, callback);
            witchCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //向后加载分页数据的
            Log.e("homeviewmodel", "loadAfter: ");
            loadData(params.key, params.requestedLoadSize, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
            //能够向前加载数据的
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    }

    private void loadData(int key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (key > 0) {
            loadAfter.set(true);
        }
        //feeds/queryHotFeedsList
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", mFeedType)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("feedId", key)
                .addParam("pageCount", count)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());
        if (witchCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.e("loadData", "onCacheSuccess: ");
                    MutableDataSource dataSource = new MutableDataSource<Feed>();
                    if (response.body != null) {
                        dataSource.data.addAll(response.body);
                        PagedList pagedList = dataSource.buildNewPagedList(config);
                        cacheLiveData.postValue(pagedList);
                    }

                }
            });
        }

        try {
            Request netRequest = witchCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            netRequest.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onSuccess(ApiResponse<List<Feed>> response) {
                    callback.onResult(response.body);
                    if (key > 0) {
                        //通过BoundaryPageData发送数据 告诉UI层 是否应该主动关闭上拉加载分页的动画
                        ((MutableLiveData) getBoundaryPageData()).postValue(response.body.size() > 0);
                        loadAfter.set(false);
                    }
                }

                @Override
                public void onError(ApiResponse<List<Feed>> response) {
                    callback.onResult(Collections.emptyList());
                }
            });

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        Log.e("loadData", "loadData: key:" + key);

    }

    @SuppressLint("RestrictedApi")
    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id, config.pageSize, callback);
            }
        });
    }
}