package com.mcsls.builderauditsys;

import com.mcsls.builderauditsys.commands.SetBasePoint;
import com.mcsls.builderauditsys.commands.StartAudit;
import com.mcsls.builderauditsys.data.PluginInfo;
import com.mcsls.builderauditsys.events.OnPlayerJoin;
import com.mcsls.builderauditsys.events.OnWorldTeleport;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BuilderAuditSys extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Logger logger = getLogger();
        logger.info("Loading builder audit sys.");

        getServer().getPluginManager().registerEvents(new OnPlayerJoin(logger),this);//注册玩家进入游戏事件
        getServer().getPluginManager().registerEvents(new OnWorldTeleport(),this);//注册传送事件
        getCommand("StartBuildAudit").setExecutor(new StartAudit(logger));//注册开启审核的命令
        getCommand("SetbasePoint").setExecutor(new SetBasePoint());//注册设置计算原点的坐标

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Closing builder audit sys.");
    }
}
