# 说明文档

- [开发中常用的工具推荐](./RECOMMEND.md)
- [开发流程及命名规范](./RULES.md)
- [开发人员及任务列表](./RE_DEV.md)

## 项目结构说明
- 项目`module`说明：
    - `library`存放统一的库文件，方便以后项目迁移和重构
    - `app`为实际开发module。其他module视功能进行添加
- `app`目录具体文件夹功能：
    - `./bean/`目录为统一实体类存放目录，在该目录下根据具体的模块分别建立二级目录存放bean对象。如`./bean/center`存放个人中心的bean对象。
    - `./module/`目录为数据层，其下分为三个目录：
        - `./local/`：该目录为本地逻辑处理目录，其下二级目录的建立同上`bean`目录。
        - `./logic/`：该目录为一些系统及软件本身状态处理的逻辑，为全局统一的模块，一般情况下请勿修改。
        - `./util/`：该目录为一些工具类。
    - `./ui/`目录为界面层，所有界面层的东西均按照模块放置到该目录；
- `drawable`及`xml`命名及存放见[RULES.md](./RULES.md)中说明。

## 注意事项
1. 软件中所有的统一使用模块：
    - 日志打印：`PLogger`；
    - Toast提示：`PToast`；
    - Sharedpreferences：`PSP`；
    - 请求缓存：`PCache`；
    - 消息派发：`MsgMgr`
2. 软件中数据库使用[sqlbrite](https://github.com/square/sqlbrite)，进行数据库操作时各自学习使用。

## 测试接口
### 1. 系统cmd消息测试接口
- [推送绑定手机](http://123.59.187.33:8681/test/TestPushPhone?fuid=1111&tuid=1112)
- [推送实名认证](http://123.59.187.33:8681/test/TestPushReal?fuid=1111&tuid=1112)
- [推送自拍认证](http://123.59.187.33:8681/test/TestPushSelf?fuid=1111&tuid=1112)
- [新类型消息测试接口](http://123.59.187.33:8681/test/PushUpdateTest?fuid=1001&tuid=1002&mct=)
- [测试推送发起视频提示](http://123.59.187.33:8681/test/TestPushVcTips?fuid=1111&tuid=1112)
- [测试送礼物系统提示消息](http://123.59.187.33:8681/test/TestPushNeedGift?fuid=1111&tuid=1112)
- [测试软件升级消息](http://123.59.187.33:8681/test/PushUpdate?fuid=110872797&tuid=110872898&mct=%E5%AF%B9%E6%96%B9%E5%8F%91%E4%BA%86%E4%B8%80%E6%9D%A1%E6%B6%88%E6%81%AF%EF%BC%8C%E6%82%A8%E7%89%88%E6%9C%AC%E8%BF%87%E4%BD%8E%EF%BC%8C%E6%97%A0%E6%B3%95%E6%9F%A5%E7%9C%8B%E3%80%82&htm=%E5%AF%B9%E6%96%B9%E5%8F%91%E4%BA%86%E4%B8%80%E6%9D%A1%E6%B6%88%E6%81%AF%EF%BC%8C%E6%82%A8%E7%89%88%E6%9C%AC%E8%BF%87%E4%BD%8E%EF%BC%8C%E6%97%A0%E6%B3%95%E6%9F%A5%E7%9C%8B%E3%80%82%3Ca%20href=%22check_update%22%3E%E7%82%B9%E5%87%BB%E5%8D%87%E7%BA%A7%3C/a%3E)
- [测试系统通知消息](http://123.59.187.33:8681/test/PushSysUpdate?fuid=9998&tuid=110872541&mct=%E5%8D%87%E7%BA%A7%E6%8F%90%E7%A4%BA&info=%E4%BD%A0%E5%BD%93%E5%89%8D%E7%89%88%E6%9C%AC%E8%BF%87%E4%BD%8E%EF%BC%8C%E4%B8%BA%E4%BA%86%E8%83%BD%E6%AD%A3%E5%B8%B8%E4%BD%BF%E7%94%A8%EF%BC%8C%E8%AF%B7%E5%8D%87%E7%BA%A7%E6%96%B0%E7%89%88%E3%80%82&btn_text=%E7%82%B9%E5%87%BB%E5%8D%87%E7%BA%A7&btn_action=check_update&pic=http://image1.yuanfenba.net/uploads/oss/avatar/201705/23/1518449570.jpg)
- [通用HTM HINT 测试接口 ](http://123.59.187.33:8681/test/TestPushHtmHint?fuid=1001&tuid=1002&mct=通用HTM消息测试&htm=送个礼物，对方立即接收既自动成为好友<a href="send_gift">点击赠送</a>)
- [送礼提示](http://123.59.187.33:8681/test/TestPushNeedGift?fuid=1111&tuid=1112)
- [跳转到小秘书聊天框](http://123.59.187.33:8681/test/TestPushJUMPKF?fuid=1111&tuid=1112)
- [视频邀请](http://123.59.187.33:8681/test/TestPushMsgInvite?fuid=1001&tuid=1002&mct=发来一个视频邀请&media_tp=1)
- [封号](http://123.59.187.33:8681/test/TestPushBan?tuid=1002&mct=由于投诉已封号&unban_tm=1497931571)
- [推送消息邀请](http://123.59.187.33:8681/test/TestPushMsgInviteTome?fuid=1001&tuid=1002&invite_id=1000&send_count=20)
- [限制登录](http://123.59.187.33:8681/userinfo/AddForbiddenGet?uid=110872731&action=1&duration=30&desc=由于您被多人举报，被限制登录)
- [开通三天后免费聊天](http://123.59.187.33:8681/test/SetFreeChatUser?uid=110872452&unlock_ycoin=1&unlock_vip=1)
- [加入工会](http://123.59.187.33:8681/test/TestPushJoinGroup?fuid=1001&tuid=1002)


### 2. 其他
- [视频邀请Show_Pop](http://go.yfb.xiaoyouapp.cn/test/TestPushVideoQun2?fuid=1001&tuid=1002&media_tp=1&price=30&show_pop=1)


## 开发定义说明文档

### 客户端--golang服务HTTP接口

新增三种接口模式(之前小友的/m/和/md/在缘分吧不再适用。xxtea加密即通过本地so进行JNI的加密，so文件已提供)。

> [X加密表示post的DATA直接xxtea加密，不验证get的hash。并且返回的数据也通过 xxtea加密](http://doc.master.yuanfenba.net/pkg/yuanfen/yfb_service/modules/test/#TestModule.X_UserCache)。xs的基本一样，只是多个cookie验证。

- [/s/：不加密只验证用户身份范例,只验证用户AUTH](http://doc.master.yuanfenba.net/pkg/yuanfen/yfb_service/modules/test/#TestModule.S_UserCache)
- [/x/：加密入口范例,加密但不验证用户AUTH](http://doc.master.yuanfenba.net/pkg/yuanfen/yfb_service/modules/test/#TestModule.X_UserCache)
- [/xs/：加密入口范例,加密且验证用户AUTH](http://doc.master.yuanfenba.net/pkg/yuanfen/yfb_service/modules/test/#TestModule.XS_UserCache)

### socket协议

- [消息文档](http://doc.dev.yuanfenba.net/pkg/yuanfen/common/msg_data/)

### Y币及VIP扣费逻辑

第一，初始用户，（未充Y币和VIP）进入APP;有一次免费发消息的机会（此时，只要用户未发出消息，任何一个女用户都是解锁状态）
第二，初始用户，与任意女用户发完第一条消息，所有女用户聊天界面上锁；
第三,  初始用户，点击任意女用户聊天界面锁，都是弹出充值Y币充值；
第四，充值完Y币（未充VIP），此时所有女用户聊天界面解锁； 用户选择任意一个女用户聊天，发完第一条消息，其他所有女用户聊天界面上锁；只有该女用户界面是解锁状态；该女用户处在信箱列表第一位；
第五，充值Y币用户，进入其他女用户界面（不是已用Y币发送消息的女用户）；点击该消息界面的锁，弹出来的都是充值VIP；
第六，充值VIP用户（已充Y币），在任意位置充值VIP，所有女用户聊天界面解锁；信箱列表随默认时间排序（此时第四状态，解除）；
第七，充值VIP用户（未充Y币），如用户，未发送第一条免费消息，所有聊天界面未上锁； 如用户，已发送第一条免费消息，所有聊天窗口，均是上锁状态，点击该锁，弹出来的是充值Y币窗口；充值 Y币，所有聊天窗口锁解除；
