package com.mcsls.builderauditsys.data;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {//线程安全的config类
    private Properties mOriginalFile;//properties 的原始文件信息
    private int baseX;
    private int baseY;
    private int baseZ;

    public synchronized void LoadConfig() throws IOException {//加载配置文件信息
        InputStream input = new FileInputStream(PluginInfo.CONFIG_FILE_PATH);//读取文件
        this.mOriginalFile.loadFromXML(input);//读取文件中的配置信息
    }

    public synchronized int GetBasePointX() {//获取计算原点的X
        if (!this.mOriginalFile.contains("baseX")) {
            throw new RuntimeException("Could not find the baseX point.");//抛出无法找到计算原点的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("baseX"));
    }

    public synchronized int GetBasePointY()//获取计算原点的Y
    {
        if (!this.mOriginalFile.contains("baseY")) {
            throw new RuntimeException("Could not find the baseY point.");//抛出无法找到计算原点的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("baseY"));
    }


    public synchronized int GetBasePointZ()//获取计算原点的Z
    {

        if (!this.mOriginalFile.contains("baseZ")) {
            throw new RuntimeException("Could not find the baseZ point.");//抛出无法找到计算原点的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("baseZ"));
    }

}
