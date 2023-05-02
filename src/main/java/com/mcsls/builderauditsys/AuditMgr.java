package com.mcsls.builderauditsys;

import com.mcsls.builderauditsys.data.Area;
import com.mcsls.builderauditsys.webService.AddAuditPlayer;
import com.sun.org.apache.xpath.internal.operations.Bool;
import it.unimi.dsi.fastutil.Hash;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Logger;

public class AuditMgr {//审核管理类
    private HashMap<String, Long> mAuditApplyPlayer;//已经申请了建筑审核的玩家的UUID,以及申请的时间的对照
    private HashMap<String, Long> mAuditingPlayer;//正在审核的玩家列表,以及和开始审核的时间的对照
    private HashMap<String, Area> mAreaUUIDMap;//正在审核的玩家和区域的对照
    private Connection mConn;//数据库的连接对象
    private Logger mLogger;

    public synchronized Area GetAuditingPlayerArea(String UUID)//获取玩家审核的区域的对象
    {
        return this.mAreaUUIDMap.get(UUID);
    }

    public synchronized void AddAuditApplyPlayer(String UUID)//将玩家添加进审核确认的对照表中
    {
        if (this.mAuditingPlayer.get(UUID) != null)//如果玩家已经在审核列表里面了
        {
            throw new RuntimeException("2");//抛出已经在审核列表里面的异常
        } else if (this.mAuditApplyPlayer.get(UUID) != null)//如果玩家已经在审核确认的队列中
        {
            throw new RuntimeException("1");//抛出已经在审核确认队列中的异常
        } else {
            this.mAuditingPlayer.put(UUID, System.currentTimeMillis() / 1000);//将玩家的UUID和审核确认的时间添加进审核确认的队列中
        }
    }

    public synchronized void AddAuditingPlayer(String UUID)//将玩家添加进正在审核的队列中
    {
        if (this.mAuditingPlayer.get(UUID) != null) //玩家已经在审核队列中了
        {
            this.mLogger.info("Player already in auditing list.");//控制台提示玩家已经开始审核了
            return;
        } else {
            this.mAuditingPlayer.put(UUID, System.currentTimeMillis() / 1000);//将玩家的UUID和开始审核的时间添加进正在审核的对照表中
        }
    }

    public synchronized Area GetPlayerAuditingArea(String UUID) {
        return this.mAreaUUIDMap.get(UUID);//返回玩家的区域的对象
    }//获取正在审核的玩家的区域对象

    public synchronized void AddAuditingArea(String UUID, Area area)//将玩家的UUID和区域对象添加进正在审核的区域与玩家的对照表中
    {
        if (this.mAreaUUIDMap.get(UUID) != null) //玩家的区域意外的出现在了正在审核的区域中
        {
            throw new RuntimeException("Player's area exist.");//抛出了玩家的区域存在的异常
        } else {//玩家的区域不存在
            this.mAreaUUIDMap.put(UUID, area);//将玩家的UUID和区域对象添加进区域对照表中
        }
    }

    public synchronized boolean HasPlayerApplyAudit(String UUID)//判断玩家是否已经在微信公众号上面申请了审核
    {
        return !(this.mAuditApplyPlayer.get(UUID) == null);//如果对照表中没有这个对照,则直接返回false
    }

    public AuditMgr(Logger logger, Connection conn)//构造函数
    {
        this.mLogger = logger;
        this.mConn = conn;

        this.mAuditApplyPlayer = new HashMap<String, Long>();//初始化审核确认的对照表
        this.mAuditingPlayer = new HashMap<String, Long>();//初始化正在审核的玩家的对照表
        this.mAreaUUIDMap = new HashMap<String, Area>();//初始化玩家UUID和区域对象的对照表
    }
}
