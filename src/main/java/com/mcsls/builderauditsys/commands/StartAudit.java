package com.mcsls.builderauditsys.commands;

import com.mcsls.builderauditsys.actions.BuildPlatform;
import com.mcsls.builderauditsys.data.Config;
import com.mcsls.builderauditsys.data.Msg;
import com.mcsls.builderauditsys.data.PluginInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class StartAudit implements CommandExecutor {//开启审核执行的指令

    Logger mLogger;//日志对象
    Config mConf;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {//指令
        if (!(sender instanceof Player))//判断是否为玩家执行的指令
        {
            sender.sendMessage(Msg.onlyPlayerExecute);
            return false;//不是玩家执行的直接返回
        }

        Player player = (Player) sender;//转换类型
        mLogger.info(sender.getName() + " start builder audit.");
        player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.startAuditMsg);//发送启动审核的状态
        player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.buildingPlatform);//发送正在建筑平台的信息

        Location location = new Location(Bukkit.getWorld("world"), this.mConf.GetBasePointX(), this.mConf.GetBasePointY(), this.mConf.GetBasePointZ());

       BuildPlatform.Build(location, this.mConf.GetPlatformSize());//生成平台

        return true;
    }

    public StartAudit(Logger logger, Config conf)//构造函数
    {
        this.mLogger = logger;
        this.mConf = conf;
    }
}
