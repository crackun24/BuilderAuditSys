package com.mcsls.builderauditsys.commands;

import com.mcsls.builderauditsys.actions.BuildPlatform;
import com.mcsls.builderauditsys.data.Area;
import com.mcsls.builderauditsys.data.Config;
import com.mcsls.builderauditsys.data.Msg;
import com.mcsls.builderauditsys.data.PluginInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.logging.Logger;

public class StartAudit implements CommandExecutor {//开启审核执行的指令

    Logger mLogger;//日志对象
    Connection mConn;//数据库连接对象
    Config mConf;

    private int GetRandomTopicID() throws SQLException {//获取随机主题的ID
        String executeCommand = "SELECT COUNT(topicId) FROM topic_map;";
        Statement stmt = this.mConn.createStatement(); //创建查询
        ResultSet res = stmt.executeQuery(executeCommand);//执行查询
        res.next();//结构集的游标下移,指向第一个结果
        int size = res.getInt("COUNT(topicId)");//返回数据库中的行数

        Random rand = new Random();
        int topicId = rand.nextInt(size) + 1;

        return topicId;//返回数据库中的行数
    }

    private int GetTotalAreaAmount() throws SQLException {//获取总区域的数量
        String executeCommand = "SELECT COUNT(areaId) FROM area_map;";
        Statement stmt = this.mConn.createStatement(); //创建查询
        ResultSet res = stmt.executeQuery(executeCommand);//执行查询
        res.next();//结构集的游标下移,指向第一个结果
        return res.getInt("COUNT(areaId)");//返回数据库中的行数
    }

    private Area GetPlayerArea(String UUID) throws SQLException {//根据玩家的UUID获取玩家的审核区域的原点
        String executeCommand = "SELECT * FROM area_map WHERE ownerUuid ='" + UUID + "';";//拼接查询指令
        Statement stmt = this.mConn.createStatement();//创建查询
        ResultSet res = stmt.executeQuery(executeCommand);
        if (!res.next())//结果集为空
        {
            return null;//返回空对象
        }

        int x = res.getInt("rbPosX");
        int y = res.getInt("rbPosY");
        int z = res.getInt("rbPosZ");
        int topicId = res.getInt("topic");//获取区域的主题的ID
        int areaId = res.getInt("areaId");//获取区域的ID
        return new Area(new Location(Bukkit.getWorld("world"), x, y, z), topicId, areaId);//返回区域对象
    }

    private void TeleportPortPlayer(Location destination, Player target)//传送玩家到指定的位置
    {
        target.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.teleport);
        target.teleport(destination);
    }

    private String GetTopicEngName(int topicId) throws SQLException {//获取主题的英语名字
        String queryStr = "SELECT * FROM topic_map WHERE topicId = '" + topicId + "';";//构建查询字符串
        Statement stmt = this.mConn.createStatement();//创建查询
        ResultSet res = stmt.executeQuery(queryStr);//执行查询
        res.next();//游标下移到第一个结果的位置
        return res.getString("EnglishName");//返回主题的英文名字
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {//指令
        if (!(sender instanceof Player))//判断是否为玩家执行的指令
        {
            sender.sendMessage(Msg.onlyPlayerExecute);
            return false;//不是玩家执行的直接返回
        }

        Player player = (Player) sender;//转换类型
        mLogger.info(sender.getName() + " start builder audit.");//在控制台输出玩家开始审核的消息
        player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.startAuditMsg);//发送启动审核的状态

        try {
            Location teleportLocation;//玩家传送点的位置
            Area area = GetPlayerArea(player.getUniqueId().toString());//获取玩家对应对平台
            int size = this.mConf.GetPlatformSize();//获取平台的大小

            if (area == null)//玩家没有平台
            {
                player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.buildingPlatform);//发送正在建筑平台的信息

                int areaAmount = GetTotalAreaAmount();//获取区域数量的多少
                int cross = areaAmount % this.mConf.GetMaxCross();//获取列数
                int raw = areaAmount / this.mConf.GetMaxCross();//获取行数

                int separation = this.mConf.GetSeparation();//获取平台的间隔

                int distanceX = this.mConf.GetBasePointX() - size * cross - cross * separation;//计算区域x之间的间隔
                int distanceZ = this.mConf.GetBasePointZ() - size * raw - raw * separation;//计算区域z之间的间隔
                //计算区域与原点的偏移

                int x = distanceX;
                int y = this.mConf.GetBasePointY();//纵坐标不用偏移
                int z = distanceZ;

                teleportLocation = new Location(Bukkit.getWorld("world"), x, y, z);//设置新区域原点的坐标

                int topicId = GetRandomTopicID();//获取主题的ID

                String insertStr = "INSERT INTO area_map SET areaId = '" + areaAmount + "'," +
                        "ownerUuid = '" + player.getUniqueId().toString() + "'," +
                        "rbPosX = '" + x + "'," + "rbPosY = '" + y + "'," +
                        "rbPosZ = '" + z + "'," + "topic = '" + topicId + "'," +
                        "createTime = '" + System.currentTimeMillis() / 1000 + "'";

                Statement stmt = this.mConn.createStatement();//创建查询
                stmt.execute(insertStr);//执行插入语句

                BuildPlatform.Build(teleportLocation, this.mConf.GetPlatformSize());//生成平台
                player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.topicName + GetTopicEngName(topicId));//通知玩家要建筑的主题的名字

            } else {//玩家已经有平台了
                teleportLocation = area.location;//设置位置
                player.sendMessage(PluginInfo.LOGGER_PREFIX + Msg.topicName + GetTopicEngName(area.topicId));//通知玩家的建筑主题的名字
            }

            this.mLogger.info(teleportLocation.toString());//FIXME
            teleportLocation.setX(teleportLocation.getX() - size / 2);//计算平台的中心位置
            teleportLocation.setY(teleportLocation.getY() + 2);//将玩家传送的高度增加二,防止玩家调入虚空
            teleportLocation.setZ(teleportLocation.getZ() - size / 2);

            player.setBedSpawnLocation(teleportLocation);//设置重生点为建筑平台的中心
            TeleportPortPlayer(teleportLocation, player);//传送玩家到平台的中心

            this.mLogger.info(teleportLocation.toString());//FIXME
        } catch (Exception e) {
            player.kickPlayer(Msg.internalError);//发生错误的时候踢出玩家
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
