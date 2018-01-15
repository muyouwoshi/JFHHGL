package com.juxin.predestinate.module.local.chat.utils;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.module.logic.config.Constant;

/**
 * 数据中心公共类
 * 客户端自己定义的消息ID
 * @author Kind
 *
 */
public class MsgIDUtils {

	// 发送消息的ID
	private long msg_id = 0;
	private long growthID = 0;

	private static MsgIDUtils msgID = null;

	public static MsgIDUtils getMsgIDUtils() {
		if (msgID == null) {
			msgID = new MsgIDUtils();
		}
		return msgID;
	}

	/**
	 * 获取消息ID
	 * @return
	 */
	public long getMsgID() {
		if (msg_id == 0) {
			growthID = PSP.getInstance().getLong(Constant.SINCE_GROWTHID, Constant.SINCE_GROWTHID_DEFAULT);
			if (growthID == -1) {// 启动软件第一次进来
				msg_id = 1;
			} else {// 启动软件进来
				msg_id = growthID + 100;
			}
			PSP.getInstance().put(Constant.SINCE_GROWTHID, msg_id);
		} else {
			msg_id++;
			if (msg_id > growthID) {
				growthID = PSP.getInstance().getLong(Constant.SINCE_GROWTHID, Constant.SINCE_GROWTHID_DEFAULT);
				growthID += 100;
				PSP.getInstance().put(Constant.SINCE_GROWTHID, growthID);
			}
		}
		return msg_id;
	}
	
	/**
	 * 还原成软件新打开的状态
	 */
	public void removeMsgID() {
		msg_id = 0;
	}
}
