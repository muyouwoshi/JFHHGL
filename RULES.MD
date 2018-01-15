# 开发规范

**代码风格与样式规范。开发必读**：
- [google面向贡献者的代码样式指南](https://source.android.com/source/code-style)
- [android代码风格](https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md)，上面内容的英文版，并有所补充。

### 命名规范
- java class文件命名遵守大驼峰命名规范。
- java方法名遵循小驼峰命名规范，首字母小写。
- 资源文件名和id等以下划线`_`分割。
- xml及资源命名以当前开发版本代号[alias]开头，具体别名开发时确定

#### class命名规则
- activity、fragment等页面命名：模块+Activity/Fragment。如：`RankDetailActivity、GameRankFragment`
- 工具类命名：功能+Util。如：`ImageUtil、TimeUtil`
- bean对象命名：直接按照功能(+本身属性命名)。如：`User、GameRankList`
- 中间层管理类：功能+Mgr/Manager。如：`CenterMgr、TipsManager`

#### 资源文件命名规则
1. xml：版本号+模块名+类型(+状态)
    - 图片：如`alias_game_title_bg、alias_center_user_icon`
    - drawable：如`alias_center_item_selector、alias_common_btn_bg`
1. anim：按照功能命名。如：`scale_in、flip_bottom_in`

#### 资源id命名
1. dimens
    - 固定写一套转换值，如`px30_dp、px45_sp`，这块勿动。其他地方重写name并对以上固定转换进行引用
    - 重写的dimens命名规则：模块+功能+属性(宽/高)。如：`center_marginTop、mail_sendicon_height`
1. color
    - 固定一套基础色值，如：`color_fffaaa、color_fc615c`，这块勿动。其他地方重写name并对以上色值进行引用
    - 重写的color命名规则：模块+功能+属性(颜色/背景)。如：`center_text_gray、game_container_bg`
1. styles：具体使用见`style.xml`文件
    - 使用的小部件style样式，一级style只能进行添加，不要进行修改
    - 二级与三级分别继承自父级，命名按照“一级_二级_三级”的规范进行，三级在继承二级的再次细分情况下可自由命名
1. 控件id:功能(+控件类型)。如：`send_button/send_btn/send、delete_textview/delete_tv/delete`
