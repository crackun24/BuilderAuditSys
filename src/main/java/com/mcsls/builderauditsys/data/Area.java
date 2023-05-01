package com.mcsls.builderauditsys.data;

import org.bukkit.Location;

public class Area {
    public Location location;//原点坐标
    public int topicId;//区域的建筑主题
    public int areaId;//区域的ID

    public Area(Location location, int topicId, int areaId)//构造函数
    {
        this.location = location;//区域的坐标
        this.topicId = topicId;//区域的建筑主题
        this.areaId = areaId;//区域的ID
    }
}
