package me.maxandroid.ppjoke.ui.detail;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.maxandroid.network.libnetwork.ApiResponse;
import me.maxandroid.network.libnetwork.ApiService;
import me.maxandroid.network.libnetwork.JsonCallback;
import me.maxandroid.ppjoke.AbsViewModel;
import me.maxandroid.ppjoke.model.Comment;
import me.maxandroid.ppjoke.ui.login.UserManager;

public class FeedDetailViewModel extends AbsViewModel<Comment> {
    private long itemId;

    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    class DataSource extends ItemKeyedDataSource<Integer, Comment> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Comment> callback) {
            loadData(params.requestedInitialKey, params.requestedLoadSize, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Comment> callback) {
            loadData(params.key, params.requestedLoadSize, callback);
        }

        private void loadData(Integer key, int requestedLoadSize, LoadCallback<Comment> callback) {
            //这个地方一定要用同步请求，如果用异步的话会回传一个空的集合，这样EmptyView就去不掉了，坑
            List<Comment> response = ((List<Comment>) ApiService.get("/comment/queryFeedComments")
                    .addParam("id", key)
                    .addParam("itemId", itemId)
                    .addParam("userId", UserManager.get().getUserId())
                    .addParam("pageCount", requestedLoadSize)
                    .responseType(new TypeReference<ArrayList<Comment>>() {
                    }.getType())
                    .execute().body);

            List<Comment> list = response == null ? Collections.emptyList() : response;
            callback.onResult(list);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Comment> callback) {
//            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Comment item) {
            return item.id;
        }
    }
}
