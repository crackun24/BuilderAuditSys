package com.mcsls.builderauditsys.actions;

import org.bukkit.Location;
import org.bukkit.Material;

public class BuildPlatform {
    static public void Build(Location location, int size) {//建造一个正方形的平台,传入右下角的坐标

        Location temp = location.clone();
        int x = (int) temp.getX();//获取位置信息
        int y = (int) temp.getY() - 1;//计算的坐标为玩家的下半身的坐标,减去1为玩家站着的那个方块的坐标
        int z = (int) temp.getZ();

        temp.setY(y);

        int Xplacement = 0;
        while (Xplacement < size) {
            temp.setX(x - Xplacement);//计算放置的位置
            Xplacement++;

            int Zplacement = 0;
            while (Zplacement < size) {//z坐标的循环
                temp.setZ(z - Zplacement);
                Zplacement++;
                if(Zplacement == 1 || Xplacement == 1 || Xplacement == size || Zplacement == size)//判断是否为z轴的第一个方块,或者第一个方块
                {
                    temp.getBlock().setType(Material.END_STONE_BRICKS);//第一个方块放置末地石
                }else{
                    temp.getBlock().setType(Material.STONE);
                }
            }

        }
    }
}
