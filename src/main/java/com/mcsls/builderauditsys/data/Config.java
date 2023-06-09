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

    public synchronized int GetSeparation()//获取平台之间的间隔
    {
        if (!this.mOriginalFile.containsKey("separation")) {
            throw new RuntimeException("Could not find the platform separation.");//抛出无法找到平台的分隔的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("separation"));
    }

    public synchronized int GetMaxCross()//获取最大的列数
    {
        if (!this.mOriginalFile.containsKey("maxCross")) {
            throw new RuntimeException("Could not find the platform maxCross.");//抛出无法找到最大的列数
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("maxCross"));
    }

    public synchronized void UpdateBaseLocation(int x, int y, int z) throws IOException//更新计算原点的坐标
    {
        this.mOriginalFile.setProperty("baseX", Integer.toString(x));
        this.mOriginalFile.setProperty("baseY", Integer.toString(y));
        this.mOriginalFile.setProperty("baseZ", Integer.toString(z));

        OutputStream out = new FileOutputStream(PluginInfo.CONFIG_FILE_PATH);
        this.mOriginalFile.store(out, "config file");//写入信息到文件中
    }

    public synchronized String GetDbName()//获取数据库的名字
    {
        if (!this.mOriginalFile.containsKey("dbName")) {
            throw new RuntimeException("Could not find the dbName.");//抛出无法找到数据库的名字的异常
        }

        return this.mOriginalFile.getProperty("dbName");
    }

    public synchronized String GetDbUser()//获取数据库的用户名
    {
        if (!this.mOriginalFile.containsKey("dbUser")) {
            throw new RuntimeException("Could not find the dbUser.");//抛出无法找到用户名的异常
        }

        return this.mOriginalFile.getProperty("dbUser");
    }

    public synchronized String GetDbPass()//获取数据库的密码
    {
        if (!this.mOriginalFile.containsKey("dbPass")) {
            throw new RuntimeException("Could not find the dbPass.");//抛出无法找到用密码的异常
        }

        return this.mOriginalFile.getProperty("dbPass");
    }

    public synchronized String GetDbHost()//获取数据库的IP
    {
        if (!this.mOriginalFile.containsKey("dbHost")) {
            throw new RuntimeException("Could not find the dbHost.");//抛出无法找到数据库IP的异常
        }

        return this.mOriginalFile.getProperty("dbHost");
    }

    public synchronized long GetExmeTime()//获取考试的时长
    {
        if (!this.mOriginalFile.containsKey("examTime")) {
            throw new RuntimeException("Could not find the examTime.");//抛出无法找到考试时长的异常
        }

        return Long.parseLong(this.mOriginalFile.getProperty("examTime"));
    }

    public synchronized int GetHttpServicePort()//获取要运行的http服务的端口
    {
        if (!this.mOriginalFile.containsKey("examTime")) {
            throw new RuntimeException("Could not find the webServicePort.");//抛出无法找到http服务端口的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("webServicePort"));
    }
    public synchronized String GetPassHookUrl()//获取发送审核通过的信息的URl
    {
        if (!this.mOriginalFile.containsKey("passHookUrl")) {
            throw new RuntimeException("Could not find the passHookUrl.");//抛出无法找到URL的异常
        }

        return this.mOriginalFile.getProperty("passHookUrl");
    }
    public synchronized long GetDelLoopTime()//获取线程循环删除时的间隔时间
    {
        if (!this.mOriginalFile.containsKey("delLoopTime")) {
            throw new RuntimeException("Could not find the delLoopTime.");//抛出无法找到循环时间的异常
        }

        return Integer.parseInt(this.mOriginalFile.getProperty("delLoopTime"));
    }

    public Config() {//构造函数
        this.mOriginalFile = new Properties();//实例化对象
    }
}
