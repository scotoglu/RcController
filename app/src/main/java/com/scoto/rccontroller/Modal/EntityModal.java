package com.scoto.rccontroller.Modal;

import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "savedViews")
public class EntityModal {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int ID;


    @ColumnInfo(name = "templateName")
    private String templateName;


    @ColumnInfo(name = "viewCoordinateX")
    private float viewCoordinateX;

    @ColumnInfo(name = "viewCoordinateY")
    private float viewCoordinateY;

    @ColumnInfo(name = "viewData")
    private String viewData;

    @ColumnInfo(name = "color")
    private int color;

    @ColumnInfo(name = "icons")
    private int icons;


    @ColumnInfo(name = "viewTempSaveDate")
    private String viewTempSaveDate;


    public EntityModal(String templateName, float viewCoordinateX,
                       float viewCoordinateY, String viewData,
                       String viewTempSaveDate) {
        this.templateName = templateName;
        this.viewCoordinateX = viewCoordinateX;
        this.viewCoordinateY = viewCoordinateY;
        this.viewData = viewData;
        this.viewTempSaveDate = viewTempSaveDate;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public float getViewCoordinateX() {
        return viewCoordinateX;
    }

    public void setViewCoordinateX(float viewCoordinateX) {
        this.viewCoordinateX = viewCoordinateX;
    }

    public float getViewCoordinateY() {
        return viewCoordinateY;
    }

    public void setViewCoordinateY(float viewCoordinateY) {
        this.viewCoordinateY = viewCoordinateY;
    }

    public String getViewData() {
        return viewData;
    }

    public void setViewData(String viewData) {
        this.viewData = viewData;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getIcons() {
        return icons;
    }

    public void setIcons(int icons) {
        this.icons = icons;
    }

    public String getViewTempSaveDate() {
        return viewTempSaveDate;
    }

    public void setViewTempSaveDate(String viewTempSaveDate) {
        this.viewTempSaveDate = viewTempSaveDate;
    }
}
