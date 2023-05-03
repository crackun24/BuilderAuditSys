package com.mcsls.builderauditsys.events;

import com.mcsls.builderauditsys.AuditMgr;
import com.mcsls.builderauditsys.data.Area;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


import static org.bukkit.Bukkit.getServer;

public class OnPlayerMove implements Listener {
    private AuditMgr mAuditMgr;//审核管理类

    public OnPlayerMove(AuditMgr auditMgr)//构造函数
    {
        this.mAuditMgr = auditMgr;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().isOp()) {//玩家为管理员,显示区域信息
            Player player = BukkitAdapter.adapt(event.getPlayer());

            int areaId = this.mAuditMgr.GetPlayerRegionId(player);//获取玩家所在的区域的ID
            if (areaId != -1) {//判断获取的区域是否有效
                Area area = this.mAuditMgr.GetArea(areaId);//获取区域
                String chineseName = this.mAuditMgr.GetTopicChiName(area.topicId);

                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7主题§0:§6" + chineseName));//发送区域的建筑主题
            }
        }

    }
}
