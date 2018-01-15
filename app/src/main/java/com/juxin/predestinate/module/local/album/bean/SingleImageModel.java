package com.juxin.predestinate.module.local.album.bean;

import java.io.Serializable;

/**
 * 单个图片文件信息
 */
public class SingleImageModel implements Serializable {
    public String path;       // 文件路径
    public boolean isPicked;  // 当前图片的选中状态
    public long date;         // 图片日期
    public long id;           // 图片ID

    public SingleImageModel(String path, boolean isPicked, long date, long id){
        this.path = path;
        this.isPicked = isPicked;
        this.date = date;
        this.id = id;
    }
    public SingleImageModel(){
    }

    /**
     * 对比两张图片是否为同一张
     *
     * @return  true 同一张图片
     */
    public boolean isThisImage(String path){
        return this.path.equalsIgnoreCase(path);
    }
}
