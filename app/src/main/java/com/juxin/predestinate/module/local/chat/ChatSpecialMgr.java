package com.juxin.predestinate.module.local.chat;

import com.juxin.library.log.PLogger;
import com.juxin.library.observe.MsgMgr;
import com.juxin.predestinate.module.local.chat.inter.ChatMsgInterface;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 特殊消息类型
 * @author Kind
 *
 */
public class ChatSpecialMgr {
	
	private static ChatSpecialMgr specialMgr = null;
	public static ChatSpecialMgr getChatSpecialMgr() {
		if (specialMgr == null) {
			specialMgr = new ChatSpecialMgr();
		}
		return specialMgr;
	}
	
	public void init() {}

	public void release() {}

	/*********私聊消息************/	
	public void onWhisperMsgUpdate(final BaseMessage message) {
		MsgMgr.getInstance().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (ChatMsgInterface.WhisperMsgListener msgListener : whisperMsgListener) {
					msgListener.onUpdateWhisper(message);
				}
			}
		});
	}
	
	private Set<ChatMsgInterface.WhisperMsgListener> whisperMsgListener = new LinkedHashSet<ChatMsgInterface.WhisperMsgListener>();
	public void attachWhisperListener(ChatMsgInterface.WhisperMsgListener listener) {
		synchronized (whisperMsgListener) {
			if (whisperMsgListener == null) {
				return;
			}
			boolean listenerExist = false;
			for (ChatMsgInterface.WhisperMsgListener item : whisperMsgListener) {
				if (item != null && item == listener) {
					listenerExist = true;
					break;
				}
			}
			if (!listenerExist) {
				whisperMsgListener.add(listener);
			}
		}
	}

	public void detachWhisperListener(ChatMsgInterface.WhisperMsgListener listener) {
		if (whisperMsgListener != null) {
			whisperMsgListener.remove(listener);
		}
	}
	
	/*****************未读角标消息*********************/

	private Set<ChatMsgInterface.UnreadReceiveMsgListener> receiveMsgListeners = new LinkedHashSet<ChatMsgInterface.UnreadReceiveMsgListener>();

	public void updateUnreadMsg(final BaseMessage message) {
		MsgMgr.getInstance().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (ChatMsgInterface.UnreadReceiveMsgListener tmp : receiveMsgListeners) {
					tmp.onUpdateUnread(message);
				}
			}
		});
	}

	public void attachUnreadMsgListener(ChatMsgInterface.UnreadReceiveMsgListener listener) {
		synchronized (receiveMsgListeners) {
			if (receiveMsgListeners == null) {
				return;
			}
			boolean listenerExist = false;
			for (ChatMsgInterface.UnreadReceiveMsgListener item : receiveMsgListeners) {
				if (item != null && item == listener) {
					listenerExist = true;
					break;
				}
			}
			if (!listenerExist) {
				receiveMsgListeners.add(listener);
			}
		}
	}

	public void detachUnreadMsgListener(ChatMsgInterface.UnreadReceiveMsgListener listener) {
		if (receiveMsgListeners != null) {
			receiveMsgListeners.remove(listener);
		}
	}

	/*****************系统消息推送*********************/
	private Set<ChatMsgInterface.SystemMsgListener> systemMsgListeners = new LinkedHashSet<ChatMsgInterface.SystemMsgListener>();

	public void setSystemMsg(final BaseMessage message) {
		MsgMgr.getInstance().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (ChatMsgInterface.SystemMsgListener tmp : systemMsgListeners) {
					tmp.onSystemMsg(message);
				}
			}
		});
	}

	public void attachSystemMsgListener(ChatMsgInterface.SystemMsgListener listener) {
		synchronized (systemMsgListeners) {
			if (systemMsgListeners == null) return;
			boolean listenerExist = false;
			for (ChatMsgInterface.SystemMsgListener item : systemMsgListeners) {
				if (item != null && item == listener) {
					listenerExist = true;
					break;
				}
			}
			if (!listenerExist) {
				systemMsgListeners.add(listener);
			}
		}
	}

	public void detachSystemMsgListener(ChatMsgInterface.SystemMsgListener listener) {
		synchronized (systemMsgListeners) {
			if (systemMsgListeners != null) {
				systemMsgListeners.remove(listener);
			}
		}
	}
}