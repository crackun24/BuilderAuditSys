package com.mcsls.builderauditsys.commands;

import com.mcsls.builderauditsys.data.Msg;
import com.mcsls.builderauditsys.data.PluginInfo;
import jdk.tools.jlink.plugin.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public class StartAudit implements CommandExecutor {//开启审核执行的指令

    Logger mLogger;//日志对象

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {//指令
        mLogger.info(sender.getName() + "Start build audit.");
        sender.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.startAuditMsg);//发送启动审核的状态
        return true;
    }

    public StartAudit(Logger logger)//构造函数
    {
        this.mLogger = logger;
    }
}
