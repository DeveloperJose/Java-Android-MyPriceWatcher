package edu.utep.cs.cs4330.mypricewatcher.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreItem;

@Database(entities = {StoreItem.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract StoreItemDao storeItemDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "database-name")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
