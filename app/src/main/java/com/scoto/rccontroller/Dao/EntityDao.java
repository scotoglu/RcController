package com.scoto.rccontroller.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.scoto.rccontroller.Modal.EntityModal;

import java.util.List;

@Dao
public interface EntityDao {

    @Query("SELECT * FROM savedviews")
     List<EntityModal> getAll();

    @Query("SELECT * FROM savedviews WHERE templateName =:name")
     List<EntityModal> getTemplate(String name);

    @Query("DELETE FROM savedviews WHERE templateName=:templateName")
     void deleteTemplate(String templateName);

    @Insert
     void insertTemplate(EntityModal... viewEntity);

    @Query("SELECT ID FROM savedviews WHERE ID=:templateID")
     int getTemplateId(int templateID);

    @Update
     void updateTemplate(EntityModal viewEntity);

}
