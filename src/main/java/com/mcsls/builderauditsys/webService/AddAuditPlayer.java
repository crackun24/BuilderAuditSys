package com.mcsls.builderauditsys.webService;

import com.mcsls.builderauditsys.AuditMgr;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AddAuditPlayer implements HttpHandler {
    AuditMgr mAuditMgr;
    @Override
    public void handle(HttpExchange exchange)//将玩家添加进审核确认对照表中,审核确认: 指玩家通过微信公众号开始申请了建筑审核
    {
       String UUID = exchange.getRequestBody().toString(); //获取请求的内容
        this.mAuditMgr.AddAuditApplyPlayer(UUID);//将玩家添加进审核确认的对照表
    }
    public AddAuditPlayer(AuditMgr auditMgr)
    {
        this.mAuditMgr = auditMgr;
    }
}
