package com.mcsls.builderauditsys.events;

import com.mcsls.builderauditsys.data.Msg;
import com.mcsls.builderauditsys.data.PluginInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OnWorldTeleport implements Listener {
    @EventHandler
    public void onPlayerTeleportWorld(PlayerTeleportEvent event) {//玩家传送世界
        //判断玩家是否传送到其他世界
        if (!event.getTo().getWorld().equals(event.getPlayer().getWorld())) {
            event.getPlayer().sendMessage(PluginInfo.LOGGER_PREFIX +  Msg.enterBanWorld);//发送警告信息
            event.setCancelled(true); //取消传送事件
        }
    }

}
