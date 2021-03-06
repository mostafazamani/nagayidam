package com.op.crush.Room.CircleCrush;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.op.crush.Room.ProgressState;

import java.util.List;

@Dao
public interface UserCrushDao {

    @Insert
    void insert(UserCrush state);

    @Update
    void update(UserCrush state);

    @Delete
    void delete(UserCrush state);

    @Query("DELETE FROM UserCrush")
    void deleteAll();


    @Query("SELECT * FROM UserCrush")
    List<UserCrush> getUserCrush();

}
