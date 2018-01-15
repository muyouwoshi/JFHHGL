package com.juxin.predestinate.module.local.album.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 单个相册目录详细信息
 * Created by Su on 2016/12/22.
 */

public class SingleDirectoryModel implements Serializable {

    private List<SingleImageModel> images;  // 当前目录下图片list

    public SingleDirectoryModel() {
        images = new ArrayList<>();
    }


    // ********************************** 获取图片信息 ***********************************************

    /**
     * 获取当前目录下的图片列表
     */
    public List<SingleImageModel> getImages() {
        return images;
    }

    /**
     * 获取当前目录下图片数量
     */
    public int getImageCounts() {
        return images.size();
    }

    /**
     * 获取指定文件路径
     */
    public String getImagePath(int position) {
        return images.get(position).path;
    }

    /**
     * 获取图片选中状态
     */
    public boolean getImagePickOrNot(int position) {
        return images.get(position).isPicked;
    }


    // ********************************** 设置图片信息 ***********************************************

    /**
     * 添加图片到当前目录
     */
    public void addImage(String path, long date, long id) {
        SingleImageModel image = new SingleImageModel(path, false, date, id);
        images.add(image);
    }

    public void addImage(SingleImageModel model) {
        images.add(model);
    }

    /**
     * 从当前目录中删除图片
     */
    public void removeImage(String path) {
        for (SingleImageModel image : images) {
            if (image.isThisImage(path)) {
                images.remove(image);
                break;
            }
        }
    }

    /**
     * 选中图片
     */
    public void setImage(String path) {
        for (SingleImageModel image : images) {
            if (image.isThisImage(path)) {
                image.isPicked = true;
                break;
            }
        }
    }

    /**
     * 不选中该图片
     */
    public void unsetImage(String path) {
        for (SingleImageModel image : images) {
            if (image.isThisImage(path)) {
                image.isPicked = false;
                break;
            }
        }
    }

    /**
     * 转变图片的选中状态
     */
    public void toggleSetImage(int position) {
        SingleImageModel model = images.get(position);
        model.isPicked = !model.isPicked;
    }

    /**
     * 转变图片的选中状态
     */
    public void toggleSetImage(String path) {
        for (SingleImageModel model : images) {
            if (model.path.equalsIgnoreCase(path)) {
                model.isPicked = !model.isPicked;
                break;
            }
        }
    }
}
