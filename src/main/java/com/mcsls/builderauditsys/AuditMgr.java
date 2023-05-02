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
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.plaf.synth.Region;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class AuditMgr extends Thread {//审核管理类
    private HashMap<String, Long> mAuditApplyPlayer;//已经申请了建筑审核的玩家的UUID,以及申请的时间的对照
    private HashMap<String, Long> mAuditingPlayer;//正在审核的玩家列表,以及和开始审核的时间的对照
    private HashMap<String, Area> mAreaUUIDMap;//正在审核的玩家和区域的对照
    private Connection mConn;//数据库的连接对象
    private Logger mLogger;
    private Config mConf;//配置文件
    private Plugin mPlugin;

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

    public synchronized void SendLeftTimeToPlayer()//将剩余的时间发送给所有审核的玩家
    {
        long examTime = this.mConf.GetExmeTime();//获取审核的时间
        for (Map.Entry<String, Long> entry : this.mAuditingPlayer.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());//取得玩家的UUID
            long deltaTime = System.currentTimeMillis() / 1000 - entry.getValue();//获取审核时间

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
            if (System.currentTimeMillis() / 1000 - createTime > outdateTime) {//判断是否请求超时
                this.mAuditApplyPlayer.remove(UUID);//将玩家移除出已经申请了审核队列
                KickPlayer(UUID, Msg.applyOutDate);//踢出玩家
            }
        }

        for (Map.Entry<String, Long> entry : this.mAuditingPlayer.entrySet()) {//遍历正在审核的对照表,踢出过期的玩家
            long startTime = entry.getValue();//获取玩家开始审核的时间
            String UUID = entry.getKey();//获取玩家的UUID
            if (System.currentTimeMillis() / 1000 - startTime > outdateTime)//判断玩家的审核是否过期了
            {
                KickPlayer(UUID, Msg.auditOutDate);//踢出玩家
                ReceiveAreaBuildPermission(this.mAreaUUIDMap.get(UUID));//获取区域,并且回收权限
                this.mAreaUUIDMap.remove(UUID);//将区域移除出正在审核的对照表
                this.mAuditingPlayer.remove(UUID);//删除着呢在审核中的玩家
            }
        }
    }

    public void run() {
        long waitTime = this.mConf.GetDelLoopTime() * 1000;//获取线程循环的时间
        while (true) {
            try {
                sleep(waitTime);//线程休眠
                DeleteOutDateObj();//删除过期的对象
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}