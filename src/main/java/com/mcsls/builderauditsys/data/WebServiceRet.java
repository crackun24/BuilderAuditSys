package com.mcsls.builderauditsys.data;

public class WebServiceRet {//返回给请求者的json信息
    private String Msg;//返回的json的请求后的消息
    private int ErrCode;//请求的错误码
    private String  Data;//返回给调用者的数据
    private long Time;//返回时间戳
}
