package com.mcsls.builderauditsys.events;

import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class OnEntityExplode implements Listener {//玩家点燃TNT
   @EventHandler
   public void onTntBurn(EntityExplodeEvent event)
   {
       if(event.getEntity() instanceof Creeper)//判断是否为苦力怕爆炸
       {
           Creeper creeper = (Creeper) event.getEntity();//将实体类型转换为苦力怕类型
           creeper.remove();//移除苦力怕
       }
       event.setCancelled(true);//取消实体爆炸事件
   }
}
