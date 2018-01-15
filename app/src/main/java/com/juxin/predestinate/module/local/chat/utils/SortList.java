package com.juxin.predestinate.module.local.chat.utils;

import android.annotation.SuppressLint;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * list排序
 * @author Kind
 *
 */
public class SortList {
	
	// 排序
	@SuppressLint("UseValueOf")
	public  static void sortListView(List<BaseMessage> baseMessages) {
		if (baseMessages == null || baseMessages.size() < 2) {
			return;
		}

		Collections.sort(baseMessages, new Comparator<BaseMessage>() {
			@Override
			public int compare(BaseMessage arg0, BaseMessage arg1) {
				return new Long(arg0.getId()).compareTo(arg1.getId());
			}
		});
	}
	
	// 时间排序
	@SuppressLint("UseValueOf")
	public static void sortTimeListView(List<BaseMessage> baseMessages) {
		if (baseMessages == null || baseMessages.size() < 2) {
			return;
		}

		Collections.sort(baseMessages, new Comparator<BaseMessage>() {
			@Override
			public int compare(BaseMessage arg0, BaseMessage arg1) {
				return new Long(arg0.getTime()).compareTo(arg1.getTime());
			}
		});
	}

	/**
	 * 权重+时间排序 消息列表专用
	 * @param baseMessages
     */
	public static void sortWeightTimeListView(List<BaseMessage> baseMessages) {
		if (baseMessages == null || baseMessages.size() < 2) {
			return;
		}

		Collections.sort(baseMessages, new Comparator<BaseMessage>() {
			@Override
			public int compare(BaseMessage arg0, BaseMessage arg1) {
				int i = Integer.valueOf(arg1.getWeight()).compareTo(arg0.getWeight());
				if (i == 0) {
					return (arg1.getTime() + "").compareTo(arg0.getTime() + "");
				} else {
					return i;
				}
			}
		});
	}
}
