package com.mcsls.builderauditsys;

import com.mcsls.builderauditsys.commands.Pass;
import com.mcsls.builderauditsys.commands.SetBasePoint;
import com.mcsls.builderauditsys.commands.StartAudit;
import com.mcsls.builderauditsys.data.Config;
import com.mcsls.builderauditsys.events.OnEntityExplode;
import com.mcsls.builderauditsys.events.OnPlayerJoin;
import com.mcsls.builderauditsys.events.OnPlayerMove;
import com.mcsls.builderauditsys.events.OnWorldTeleport;
import com.mcsls.builderauditsys.webService.AddAuditPlayer;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Logger;

public final class BuilderAuditSys extends JavaPlugin {
    private Logger mLogger;//日志记录器
    private AuditMgr mAuditMgr;//审核管理对象
    private Config mConf;//配置对象
    private Connection mConn;//数据库连接对象
    private HttpServer mHttpServer;//http服务对象

    private void init() //初始化基本信息
    {
        this.mLogger = getLogger();//获取日志记录器
        mConf = new Config();//实例化配置对象
        try {
            this.mConf.LoadConfig();//加载配置文件
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "jdbc:mysql://192.168.0.103:3306/" + this.mConf.GetDbName();//设置jdbc连接使用的地址
        try {
            Properties props = new Properties();
            props.setProperty("user", this.mConf.GetDbUser());
            props.setProperty("password", this.mConf.GetDbPass());
            props.setProperty("autoReconnect", "true");

            this.mConn = DriverManager.getConnection(url, props);//连接到数据库
            this.mConn.setAutoCommit(true);//设置自动提交
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mAuditMgr = new AuditMgr(this.mLogger, this.mConn,this.mConf,this);//初始化审核管理对象

        try {//初始化http服务器对象
            this.mHttpServer = HttpServer.create(new InetSocketAddress(7123), 0);//创建一个http服务器对象
            this.mHttpServer.createContext("/addAuditPlayer", new AddAuditPlayer(this.mLogger, this.mAuditMgr));//创建一个context
            this.mHttpServer.start();//启动http服务器
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.mAuditMgr.start();//启动线程

    }

    @Override
    public void onEnable() {
        init();//初始化插件

        // Plugin startup logic
        this.mLogger.info("Loading builder audit sys.");

        getServer().getPluginManager().registerEvents(new OnPlayerJoin(this.mLogger, this.mAuditMgr), this);//注册玩家进入游戏事件
        getServer().getPluginManager().registerEvents(new OnWorldTeleport(), this);//注册传送事件
        getServer().getPluginManager().registerEvents(new OnEntityExplode(), this);//实体爆炸事件
        getServer().getPluginManager().registerEvents(new OnPlayerMove(this.mAuditMgr),this);

        getCommand("sba").setExecutor(new StartAudit(this.mLogger, this.mConf, this.mConn, this.mAuditMgr));//注册开启审核的命令
        getCommand("sbp").setExecutor(new SetBasePoint(this.mConf));//注册设置计算原点的坐标
        getCommand("pass").setExecutor(new Pass(this.mAuditMgr));//通过审核的指令
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Closing builder audit sys.");
    }
}
