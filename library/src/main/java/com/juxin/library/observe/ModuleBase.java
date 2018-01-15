package com.juxin.library.observe;

/**
 * 所有逻辑模块的接口
 */
public interface ModuleBase {

    /**
     * 在模块被按需加载的时候自动调用，主线程。
     */
    void init();

    /**
     * 退出时候被调用，主线程。<br>
     * 切记别的模块可能已经release，此时不允许访问其它模块了。
     */
    void release();
}
