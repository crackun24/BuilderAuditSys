package com.mcsls.builderauditsys;

import com.mcsls.builderauditsys.commands.StartAudit;
import com.mcsls.builderauditsys.data.PluginInfo;
import com.mcsls.builderauditsys.events.OnPlayerJoin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BuilderAuditSys extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Logger logger = getLogger();
        logger.info("Loading builder audit sys.");

        getServer().getPluginManager().registerEvents(new OnPlayerJoin(logger),this);//注册事件
        getCommand("StartBuildAudit").setExecutor(new StartAudit(logger));//注册开启审核的命令
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Closing builder audit sys.");
    }
}
