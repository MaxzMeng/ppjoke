package me.maxandroid.network.libnetwork.cache;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import me.maxandroid.libcommon.AppGlobals;

@Database(entities = {Cache.class}, version = 1)
public abstract class CacheDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "ppjoke_cache";
    private static final CacheDatabase database;

    static {
        database = Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public static CacheDatabase get() {
        return database;
    }

    public abstract CacheDao getCache();
}
