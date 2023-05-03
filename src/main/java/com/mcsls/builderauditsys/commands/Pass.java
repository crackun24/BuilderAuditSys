package com.mcsls.builderauditsys.commands;

import com.mcsls.builderauditsys.AuditMgr;
import com.mcsls.builderauditsys.data.Area;
import com.mcsls.builderauditsys.data.Config;
import com.mcsls.builderauditsys.data.Msg;
import com.mcsls.builderauditsys.data.PluginInfo;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;

public class Pass implements CommandExecutor {
    private AuditMgr mAuditMgr;
    private Connection mConn;//数据库连接对象

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {//执行通过指令的函数
        if (sender instanceof Player)//判断是否为玩家执行
        {
            Player player = (Player) sender;//转换为玩家对象
            if (player.isOp())//判断玩家是否有权限
            {
                int areaId = this.mAuditMgr.GetPlayerRegionId(BukkitAdapter.adapt(player));//获取执行命令的玩家的脚下的区域的ID
                if (areaId != -1)//判断这片区域是否存在
                {
                    try {
                        this.mAuditMgr.PlayerPass(areaId);  //设置这片区域的玩家已经通过审核了
                    } catch (Exception e) {
                        player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.internalError);//发送错误信息给玩家
                    }
                } else {//这片区域不存在
                    player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.areaNotFount);
                }
            } else {
                player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.noPomession);//发送没有权限的信息给玩家
            }
        }
        return false;
    }

    public Pass(AuditMgr auditMgr) {
        this.mAuditMgr = auditMgr;

    }
}
