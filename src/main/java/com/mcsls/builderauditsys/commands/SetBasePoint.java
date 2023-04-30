package com.mcsls.builderauditsys.commands;

import com.mcsls.builderauditsys.data.Config;
import com.mcsls.builderauditsys.data.Msg;
import com.mcsls.builderauditsys.data.PluginInfo;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SetBasePoint implements CommandExecutor {
    private Config mConf;//配置文件对象

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.noPomession);
            return false;
        } else {
            if (!(sender instanceof Player))//判断执行命令的是否为玩家,如果不是玩家则直接返回
            {
                sender.sendMessage(Msg.onlyPlayerExecute);
                return false;
            }
            Player player = (Player) sender;//执行类型转换
            Location location = player.getLocation();//获取玩家的位置

            try {
                int x = (int) location.getX();
                int y = (int) location.getY();
                int z = (int) location.getZ();
                this.mConf.UpdateBaseLocation(x, y, z);//更新计算原点的坐标信息

                player.sendMessage(PluginInfo.LOGGER_PREFIX + "计算原点坐标已更新: x: " + x + " y: " + y + " z: " + z);//通知玩家坐标更新
            } catch (Exception e) {
                player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.failCommand);//通知命令的执行者命令执行错误
                e.printStackTrace();
            }
        }
        return true;
    }//设置建筑类玩家审核的计算原点

    public SetBasePoint(Config conf)//构造函数
    {
        this.mConf = conf;//设置配置文件对象
    }
}
