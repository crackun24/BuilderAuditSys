package com.mcsls.builderauditsys.events;

import com.mcsls.builderauditsys.AuditMgr;
import com.mcsls.builderauditsys.data.Msg;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class OnPlayerJoin implements Listener {
    private Logger mLogger;
    private AuditMgr mAuditMgr;//审核管理对象

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)//玩家进入游戏的事件的处理
    {
        Player player = event.getPlayer();//获取玩家对象
        String UUID = event.getPlayer().getUniqueId().toString();//获取玩家的UUID
        if (!this.mAuditMgr.IsPlayerApplyAudit(UUID) && !this.mAuditMgr.IsPlayerAuditing(UUID)) {//判断玩家是否已经在微信公众号上面申请了审核,或者玩家为正在审核
            player.kickPlayer(Msg.noApply);//将玩家踢出,并告知要去微信公众号上面申请审核
        }
    }

    public OnPlayerJoin(Logger logger, AuditMgr auditMgr)//构造函数
    {
        this.mLogger = logger;
        this.mAuditMgr = auditMgr;
    }
}
