package edu.utep.cs.cs4330.mypricewatcher.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.cs4330.mypricewatcher.store.core.Store;
import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreItem;

@Dao
public abstract class StoreItemDao {
    @Query("SELECT * FROM storeitem")
    public abstract List<StoreItem> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract List<Long> insert(List<StoreItem> list);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(StoreItem item);

    @Update
    public abstract void update(StoreItem item);

    @Update
    public abstract void update(List<StoreItem> obj);

    @Delete
    public abstract void delete(StoreItem user);

    @Transaction
    public void insertOrUpdate(StoreItem obj) {
        long id = insert(obj);
        if (id == -1) {
            update(obj);
        }
    }

    @Transaction
    public void insertOrUpdate(List<StoreItem> list) {
        List<Long> insertResult = insert(list);
        List<StoreItem> updateList = new ArrayList<>();

        for (int i = 0; i < insertResult.size(); i++) {
            if (insertResult.get(i) == -1) {
                updateList.add(list.get(i));
            }
        }

        if (!updateList.isEmpty()) {
            update(updateList);
        }
    }

}
