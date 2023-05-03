package com.mcsls.builderauditsys;

import com.mcsls.builderauditsys.data.Area;
import com.mcsls.builderauditsys.data.Config;
import com.mcsls.builderauditsys.data.Msg;
import com.mcsls.builderauditsys.data.PluginInfo;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import jdk.internal.net.http.HttpClientBuilderImpl;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import sun.net.www.http.HttpClient;
import sun.util.resources.cldr.ext.LocaleNames_qu;

import javax.swing.plaf.synth.Region;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class AuditMgr extends Thread {//审核管理类
    private HashMap<String, Long> mAuditApplyPlayer;//已经申请了建筑审核的玩家的UUID,以及申请的时间的对照
    private HashMap<String, Long> mAuditingPlayer;//正在审核的玩家列表,以及和开始审核的时间的对照
    private HashMap<String, Area> mAreaUUIDMap;//正在审核的玩家和区域的对照
    private HashMap<Integer, Area> mAllAreaMap;//所有的区域的ID的对照
    private Connection mConn;//数据库的连接对象
    private Logger mLogger;
    private Config mConf;//配置文件
    private Plugin mPlugin;
    private HashMap<Integer, String> mTopicNameMap;//主题和主题ID的对照

    private void ReceiveAreaBuildPermission(Area area) throws StorageException//回收该区域的建筑所有权
    {
        World world = Bukkit.getWorld("world");//获取世界对象
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion(Integer.toString(area.areaId));//根据区域的ID获取区域对象
        region.getOwners().clear();//清除所有的拥有者

        regionManager.save();//保存更改
    }

    private void KickPlayer(String UUIDStr, String reason)//根据玩家的UUID踢出玩家
    {
        UUID uuid = UUID.fromString(UUIDStr);
        Player player = Bukkit.getServer().getPlayer(uuid);//根据玩家的UUID获取玩家对象
        if (player != null)//判断玩家对象是否为空,玩家是否在线
        {
            Bukkit.getScheduler().runTask(this.mPlugin, new Runnable() {
                public void run() {
                    player.kickPlayer(reason);
                }
            });//踢出玩家
        }
    }

    private synchronized void AuditingLeftTimeDel() throws StorageException//处理正在审核的玩家
    {
        long examTime = this.mConf.GetExmeTime();//获取审核的持续时间

        for (Map.Entry<String, Long> entry : this.mAuditingPlayer.entrySet()) {//遍历正在审核的对照表,踢出过期的玩家
            long startTime = entry.getValue();//获取玩家开始审核的时间
            String UUIDStr = entry.getKey();//获取玩家的UUID

            long deltaTime = System.currentTimeMillis() / 1000 - startTime;//获取审核持续时间
            long leftTime = examTime - deltaTime;//计算剩余的时间

            int min = (int) leftTime / 60;//获取剩余的分钟数
            int sec = (int) leftTime - min * 60;//获取剩余的秒数

            Player player = Bukkit.getServer().getPlayer(UUID.fromString(UUIDStr));//通过玩家的UUID获取玩家对象

            if (player != null) {//判断是否可以获取到玩家,玩家是否在线
                String minStr = Integer.toString(min);
                String secStr = Integer.toString(sec);

                if (min < 10)//补齐数字
                    minStr = "0" + minStr;
                if (sec < 10)
                    secStr = "0" + secStr;


                String leftTimeStr = "§4剩余时间: §e" + minStr + "§6:§e" + secStr;//剩余时间的字符串
                Bukkit.getScheduler().runTask(this.mPlugin, new Runnable() {
                    public void run() {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(leftTimeStr));//发送剩余时间
                    }
                });
            }

            if (deltaTime > examTime)//判断玩家的审核是否过期了
            {
                KickPlayer(UUIDStr, Msg.auditOutDate);//踢出玩家
                ReceiveAreaBuildPermission(this.mAreaUUIDMap.get(UUIDStr));//获取区域,并且回收权限
                this.mAreaUUIDMap.remove(UUIDStr);//将区域移除出正在审核的对照表
                this.mAuditingPlayer.remove(UUIDStr);//删除着呢在审核中的玩家
            }
        }
    }

    public synchronized Area GetAuditingPlayerArea(String UUID)//获取玩家审核的区域的对象
    {
        if (!this.mAreaUUIDMap.containsKey(UUID))//判断对照表中是否有值
            return null;
        else return this.mAreaUUIDMap.get(UUID);
    }

    public synchronized void AddAuditApplyPlayer(String UUID)//将玩家添加进审核确认的对照表中
    {
        if (IsPlayerAuditing(UUID))//如果玩家已经在审核列表里面了
        {
            throw new RuntimeException("2");//抛出已经在审核列表里面的异常
        } else if (this.mAuditApplyPlayer.containsKey(UUID))//如果玩家已经在审核确认的队列中
        {
            throw new RuntimeException("1");//抛出已经在审核确认队列中的异常
        } else {
            this.mAuditApplyPlayer.put(UUID, System.currentTimeMillis() / 1000);//将玩家的UUID和审核确认的时间添加进审核确认的队列中
        }
    }

    public synchronized void PlayerPass(int areaId) throws SQLException, IOException {//添加通过的玩家
        String queryStr = "SELECT * FROM area_map WHERE areaId='" + areaId + "';";//构建查询语句
        Statement stmt = this.mConn.createStatement();//创建查询
        ResultSet res = stmt.executeQuery(queryStr);//查询
        res.next();//获取第一个结果

        URL url = new URL(this.mConf.GetPassHookUrl());//设置通过后回调的URL
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        String passPlayerUuid = res.getString("ownerUuid");
        out.writeBytes(passPlayerUuid);//通过审核的玩家的UUID
        out.flush();
        out.close();
        this.mLogger.info(passPlayerUuid);//FIXME
    }


    public synchronized void AddAuditingPlayer(String UUID)//将玩家添加进正在审核的队列中
    {
        if (this.mAuditingPlayer.containsKey(UUID)) //玩家已经在审核队列中了
        {
            this.mLogger.info("Player already in auditing list.");//控制台提示玩家已经开始审核了
        } else {
            this.mAuditingPlayer.put(UUID, System.currentTimeMillis() / 1000);//将玩家的UUID和开始审核的时间添加进正在审核的对照表中
        }
    }

    public synchronized void AddAuditingArea(String UUID, Area area)//将玩家的UUID和区域对象添加进正在审核的区域与玩家的对照表中
    {
        if (this.mAreaUUIDMap.containsKey(UUID)) //玩家的区域意外的出现在了正在审核的区域中
        {
            throw new RuntimeException("Player's area exist.");//抛出了玩家的区域存在的异常
        } else {//玩家的区域不存在
            this.mAreaUUIDMap.put(UUID, area);//将玩家的UUID和区域对象添加进区域对照表中
            AddAreaToAllMap(area);//如果玩家审核则为创建了一个新的区域,添加进总区域对照中
        }
    }

    public synchronized boolean IsPlayerApplyAudit(String UUID)//判断玩家是否已经在微信公众号上面申请了审核
    {
        return this.mAuditApplyPlayer.containsKey(UUID);//如果对照表中没有这个对照,则直接返回false
    }

    public synchronized boolean IsPlayerAuditing(String UUID) //判断玩家是否正在审核中
    {
        return this.mAuditingPlayer.containsKey(UUID);
    }

    public AuditMgr(Logger logger, Connection conn, Config conf, Plugin plugin)//构造函数
    {
        this.mLogger = logger;
        this.mConn = conn;
        this.mConf = conf;
        this.mPlugin = plugin;

        this.mAuditApplyPlayer = new HashMap<String, Long>();//初始化审核确认的对照表
        this.mAuditingPlayer = new HashMap<String, Long>();//初始化正在审核的玩家的对照表
        this.mAreaUUIDMap = new HashMap<String, Area>();//初始化玩家UUID和区域对象的对照表
    }

    private synchronized void DeleteOutDateObj() throws StorageException//删除过期的对象信息
    {
        long outdateTime = this.mConf.GetExmeTime();//获取最大的等待时间

        for (Map.Entry<String, Long> entry : this.mAuditApplyPlayer.entrySet())//遍历整个对照表
        {
            long createTime = entry.getValue();
            String UUID = entry.getKey();//获取玩家的UUID
            if (System.currentTimeMillis() / 1000 - createTime > outdateTime && !this.mAuditingPlayer.containsKey(UUID)) {//判断是否请求超时,并且玩家不在审核队列中
                this.mAuditApplyPlayer.remove(UUID);//将玩家移除出已经申请了审核队列
                KickPlayer(UUID, Msg.auditOutDate);//踢出玩家
            }
        }
    }

    private synchronized void GetAllAreaMapFromDb() throws SQLException//从数据库中添加所有的区域对照
    {
        String queryStr = "SELECT * FROM area_map;";
        Statement stmt = this.mConn.createStatement();//创建查询
        ResultSet res = stmt.executeQuery(queryStr);//获取所有的结果

        while (res.next())//遍历结果集
        {
            Location location = new Location(Bukkit.getWorld("world"), res.getInt("rbPosX"),
                    res.getInt("rbPosY"), res.getInt("rbPosZ"));//获取区域的原点位置

            int areaId = res.getInt("areaId");
            Area area = new Area(location, res.getInt("topic"), areaId);//构造新的区域对象
            this.mAllAreaMap.put(areaId, area);//插入对照
        }
    }

    private synchronized void GetAllTopicMapFromDb() throws SQLException//从数据库中获取所有的主题ID和主题的对照
    {
        String queryStr = "SELECT * FROM topic_map;";
        Statement stmt = mConn.createStatement();//创建查询
        ResultSet res = stmt.executeQuery(queryStr);//执行查询

        while (res.next())//遍历结果集
        {
            this.mTopicNameMap.put(res.getInt("topicId"), res.getString("ChineseName"));//插入到主题的对照表中
        }
    }


    public int GetPlayerRegionId(com.sk89q.worldedit.entity.Player player) {//获取玩家的区域ID
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();//获取区域的容器
        RegionQuery query = container.createQuery();//创建查询

        com.sk89q.worldedit.util.Location location = player.getLocation();
        ApplicableRegionSet regions = query.getApplicableRegions(location);

        String regionId = null;
        for (ProtectedRegion region : regions) {
            regionId = region.getId();
        }
        if (regionId == null) {
            return -1;//无法解析,玩家不在任何一个区域内
        }
        return Integer.parseInt(regionId);
    }

    public synchronized Area GetArea(int areaId)//获取区域
    {
        return this.mAllAreaMap.get(areaId);
    }

    public synchronized String GetTopicChiName(int topicId)//获取主题的中文名
    {
        return this.mTopicNameMap.get(topicId);
    }

    public synchronized void AddAreaToAllMap(Area area)//将区域添加进所有的对照中
    {
        this.mAllAreaMap.put(area.areaId, area);
    }

    public void run() {
        this.mTopicNameMap = new HashMap<Integer, String>();//初始化容器
        this.mAllAreaMap = new HashMap<Integer, Area>();

        long waitTime = this.mConf.GetDelLoopTime() * 1000;//获取线程循环的时间
        try {
            GetAllAreaMapFromDb();//从数据库中获取所有的区域的对照
            GetAllTopicMapFromDb();//从数据库中获取所有的主题中文名字对照
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                sleep(waitTime);//线程休眠

                AuditingLeftTimeDel();//处理剩余时间
                DeleteOutDateObj();//删除过期的对象
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}