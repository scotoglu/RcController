package com.scoto.rccontroller.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.scoto.rccontroller.Dao.EntityDao;
import com.scoto.rccontroller.Modal.EntityModal;
/*
* version have to start 1,if starts zero gives error
* mutltiple class(table) can add to do that use entities={Class.class,Class.class)
*
* */
@Database(entities = {EntityModal.class},version = 3,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EntityDao viewDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"RCController.db")
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_LATEST)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_LATEST = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            //Log.i("Tag" , "Migration Started");
            //Migration logic
            //Log.i("Tag" , "Migration Ended");
        }
    };

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
