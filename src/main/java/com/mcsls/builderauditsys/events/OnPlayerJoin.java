package com.mcsls.builderauditsys.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class OnPlayerJoin implements Listener {
    private Logger mLogger;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)//玩家进入游戏的事件的处理
    {
       this.mLogger.info(event.getPlayer().getName());//FIXME
    }

    public OnPlayerJoin(Logger logger)//构造函数
    {
        this.mLogger = logger;
    }
}
