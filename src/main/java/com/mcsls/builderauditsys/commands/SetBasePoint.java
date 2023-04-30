package com.mcsls.builderauditsys.commands;

import com.mcsls.builderauditsys.data.Msg;
import com.mcsls.builderauditsys.data.PluginInfo;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBasePoint implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.noPomession);
            return false;
        } else {
            Location location = null;
            if (sender instanceof Player)//判断执行命令的是否为玩家
            {
                Player player = (Player) sender;//执行类型转换
                location = player.getLocation();//获取玩家的位置
            }else{
                return false;//如果执行的不是玩家则直接返回
            }

            String test = location.getWorld().getName();
            sender.sendMessage(test);
        }
        return true;
    }//设置建筑类玩家审核的计算原点

}
