package com.mcsls.builderauditsys;

import com.mcsls.builderauditsys.commands.SetBasePoint;
import com.mcsls.builderauditsys.commands.StartAudit;
import com.mcsls.builderauditsys.data.Config;
import com.mcsls.builderauditsys.events.OnEntityExplode;
import com.mcsls.builderauditsys.events.OnPlayerJoin;
import com.mcsls.builderauditsys.events.OnWorldTeleport;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Logger;

public final class BuilderAuditSys extends JavaPlugin {
    private Logger mLogger;//日志记录器
    private Config mConf;//配置对象
    private Connection mConn;//数据库连接对象

    private void init()//初始化基本信息
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
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        init();//初始化插件
        this.mLogger.info("Loading builder audit sys.");

        getServer().getPluginManager().registerEvents(new OnPlayerJoin(this.mLogger), this);//注册玩家进入游戏事件
        getServer().getPluginManager().registerEvents(new OnWorldTeleport(), this);//注册传送事件
        getServer().getPluginManager().registerEvents(new OnEntityExplode(), this);//实体爆炸事件

        getCommand("StartBuildAudit").setExecutor(new StartAudit(this.mLogger, this.mConf,this.mConn));//注册开启审核的命令
        getCommand("SetbasePoint").setExecutor(new SetBasePoint(this.mConf));//注册设置计算原点的坐标

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Closing builder audit sys.");
    }
}
