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
    public List<EntityModal> getAll();

    @Query("SELECT * FROM savedviews WHERE templateName =:name")
    public List<EntityModal> getTemplate(String name);

    @Query("DELETE FROM savedviews WHERE templateName=:templateName")
    public void deleteTemplate(String templateName);

    @Insert
    public void insertTemplate(EntityModal... viewEntity);

    @Query("SELECT ID FROM savedviews WHERE ID=:templateID")
    public int getTemplateId(int templateID);

//    @Query("UPDATE savedviews SET ID=:ID,templateName=:templateName,viewCoordinateX=:viewCoordinateX,viewCoordinateY=:viewCoordinateY,viewData=:viewData,viewTempSaveDate=:viewTempSaveDate")
//    public void updateTemplate(int ID, String templateName,
//                               float viewCoordinateX,
//                               float viewCoordinateY,
//                               String viewData,
//                               String viewTempSaveDate);

    @Update
    public void updateTemplate(EntityModal viewEntity);
}
