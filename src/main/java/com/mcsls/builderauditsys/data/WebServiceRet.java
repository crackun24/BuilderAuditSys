package com.mcsls.builderauditsys.data;

public class WebServiceRet {//返回给请求者的json信息
    private String Msg;//返回的json的请求后的消息
    private int ErrCode;//请求的错误码
    private String Data;//返回给调用者的数据
    private long Time;//返回时间戳

    public void setMsg(String msg) {
        this.Msg = msg;
    }

    public void setErrCode(int errCode) {
        this.ErrCode = errCode;
    }

    public void setData(String data) {
        this.Data = data;
    }

    public void setTime(long time) {
        this.Time = time;
    }

    public String getMsg() {
        return this.Msg;
    }

    public int getErrCode() {
        return this.ErrCode;
    }

    public String getData() {
        return this.Data;
    }

    public long getTime() {
        return this.Time;
    }

    public WebServiceRet(int errCode, String msg, String data)//构造函数
    {
        this.ErrCode = errCode;
        this.Data = data;
        this.Msg = msg;
        this.Time = System.currentTimeMillis() / 1000;//设置时间
    }
}
