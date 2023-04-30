package com.mcsls.builderauditsys.data;

import java.io.*;
import java.util.Properties;

public class Config {//线程安全的config类
    private Properties mOriginalFile;//propert

    public synchronized void LoadConfig() throws IOException {//加载配置文件信息
        InputStream input = new FileInputStream(PluginInfo.CONFIG_FILE_PATH);//读取文件
        this.mOriginalFile.load(input);//加载配置文件
    }

    public synchronized int GetBasePointX() {//获取计算原点的X
        if (!this.mOriginalFile.containsKey("baseX")) {
            throw new RuntimeException("Could not find the baseX point.");//抛出无法找到计算原点的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("baseX"));
    }

    public synchronized int GetBasePointY()//获取计算原点的Y
    {
        if (!this.mOriginalFile.containsKey("baseY")) {
            throw new RuntimeException("Could not find the baseY point.");//抛出无法找到计算原点的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("baseY"));
    }


    public synchronized int GetBasePointZ()//获取计算原点的Z
    {
        if (!this.mOriginalFile.containsKey("baseZ")) {
            throw new RuntimeException("Could not find the baseZ point.");//抛出无法找到计算原点的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("baseZ"));
    }

    public synchronized int GetPlatformSize()//获取建筑平台的大小
    {
        if (!this.mOriginalFile.containsKey("platformSize")) {
            throw new RuntimeException("Could not find the platform size.");//抛出无法找到平台大小的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("platformSize"));
    }

    public synchronized void UpdateBaseLocation(int x, int y, int z) throws IOException//更新计算原点的坐标
    {
        this.mOriginalFile.setProperty("baseX", Integer.toString(x));
        this.mOriginalFile.setProperty("baseY", Integer.toString(y));
        this.mOriginalFile.setProperty("baseZ", Integer.toString(z));

        OutputStream out = new FileOutputStream(PluginInfo.CONFIG_FILE_PATH);
        this.mOriginalFile.store(out, "config file");//写入信息到文件中
    }

    public Config() {//构造函数
        this.mOriginalFile = new Properties();//实例化对象
    }
}
