package com.mcsls.builderauditsys.webService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcsls.builderauditsys.AuditMgr;
import com.mcsls.builderauditsys.data.WebServiceRet;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.logging.Logger;

public class AddAuditPlayer implements HttpHandler {
    AuditMgr mAuditMgr;
    Logger mLogger;
    @Override
    public void handle(HttpExchange exchange)//将玩家添加进审核确认对照表中,审核确认: 指玩家通过微信公众号开始申请了建筑审核
    {
        if(!exchange.getRequestMethod().equals("POST"))
            return;//请求方法错误,直接返回函数

        Gson gson = new GsonBuilder().create();//json序列化对象
        WebServiceRet ret;
        try {
            Scanner s = new Scanner(exchange.getRequestBody(), "UTF-8").useDelimiter("\\A");
            String UUID = s.hasNext() ? s.next() : "";//读取输入流信息

            this.mAuditMgr.AddAuditApplyPlayer(UUID);//将玩家添加进审核确认的对照表
            ret = new WebServiceRet(0,"SUCCESS","");
        } catch (Exception e) {//如果添加不成功,则会抛出异常
            int errCode = Integer.parseInt(e.getMessage());//解析异常中的错误代码
            ret = new WebServiceRet(errCode,"PARSE SELF","");
        }

        try {
            String retMsg = gson.toJson(ret);//转换为json字符串
            exchange.sendResponseHeaders(200, retMsg.length());

            OutputStream os = exchange.getResponseBody();//获取体的输出流对象
            os.write(retMsg.getBytes());//写出请求体
            os.close();//关闭输出流

        }catch (Exception e)
        {
            this.mLogger.warning("An error occurred when sending the response data." + e.getMessage());
        }
    }

    public AddAuditPlayer(Logger logger,AuditMgr auditMgr) {
        this.mAuditMgr = auditMgr;
        this.mLogger = logger;
    }
}
