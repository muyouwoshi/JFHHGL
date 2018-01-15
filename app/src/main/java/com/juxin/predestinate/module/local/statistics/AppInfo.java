package com.juxin.predestinate.module.local.statistics;

/**
 * 软件基本信息实体
 * Created by ZRP on 2017/7/27.
 */
public class AppInfo {

    private String name;                // APP名称
    private boolean sys_app;            // 是否系统APP
    private String version_name;        // APP版本号
    private String package_name;        // APP包名
    private long first_install_time;    // 首次安装时间
    private long last_update_time;      // 最后更新时间

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSys_app() {
        return sys_app;
    }

    public void setSys_app(boolean sys_app) {
        this.sys_app = sys_app;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public long getFirst_install_time() {
        return first_install_time;
    }

    public void setFirst_install_time(long first_install_time) {
        this.first_install_time = first_install_time;
    }

    public long getLast_update_time() {
        return last_update_time;
    }

    public void setLast_update_time(long last_update_time) {
        this.last_update_time = last_update_time;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", sys_app=" + sys_app +
                ", version_name='" + version_name + '\'' +
                ", package_name='" + package_name + '\'' +
                ", first_install_time=" + first_install_time +
                ", last_update_time=" + last_update_time +
                '}';
    }
}