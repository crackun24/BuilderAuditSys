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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class StartAudit implements CommandExecutor {//开启审核执行的指令

    Logger mLogger;//日志对象
    Connection mConn;//数据库连接对象
    Config mConf;

    private int GetTotalAreaAmount() throws SQLException {//获取总区域的数量
        String executeCommand = "SELECT COUNT(areaId) FROM area_map;";

        Statement stmt = this.mConn.createStatement(); //创建查询
        ResultSet res = stmt.executeQuery(executeCommand);//执行查询
        res.next();//结构集的游标下移,指向第一个结果
        return res.getInt("COUNT(areaId)");//返回数据库中的行数
    }

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

        try {
            int areaAmount = GetTotalAreaAmount();//获取区域数量的多少
            int cross = areaAmount % this.mConf.GetMaxCross();//获取列数
            int raw = areaAmount / this.mConf.GetMaxCross();//获取行数

            int size = this.mConf.GetPlatformSize();//获取平台的大小
            int separation = this.mConf.GetSeparation();//获取平台的间隔

            int distanceX = this.mConf.GetBasePointX() - size * cross - cross * separation;//计算区域x之间的间隔
            int distanceZ = this.mConf.GetBasePointZ() - size * raw - raw * separation;//计算区域z之间的间隔
            //计算区域与原点的偏移

            int x = distanceX;
            int y = this.mConf.GetBasePointY();//纵坐标不用偏移
            int z = distanceZ;

            Location location = new Location(Bukkit.getWorld("world"), x, y, z);//设置新区域原点的坐标

            BuildPlatform.Build(location, this.mConf.GetPlatformSize());//生成平台
        } catch (Exception e) {
            player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.internalError);//发送错误信息到玩家
            e.printStackTrace();
        }
        return true;
    }

    public StartAudit(Logger logger, Config conf, Connection conn)//构造函数
    {
        this.mLogger = logger;
        this.mConn = conn;//
        this.mConf = conf;
    }
}
