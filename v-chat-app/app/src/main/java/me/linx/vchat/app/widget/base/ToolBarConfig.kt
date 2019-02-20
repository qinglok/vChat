package me.linx.vchat.app.widget.base

import androidx.appcompat.widget.Toolbar
import me.linx.vchat.app.R

class ToolBarConfig(
    // 是否显示工具栏，默认关闭
    var showDefaultToolBar: Boolean = false,
    // 是否启用工具栏后退按钮
    var enableBackOff: Boolean = false,
    // 后退按钮点击事件
    var onBackOffClick: () -> Unit = {},
    // 设置工具栏标题，默认为App Name
    var titleRes: Int = R.string.app_name,
    var title: String? = null,
    // 设置工具栏选项菜单
    var menuRes: Int = 0,
    // 设置工具栏选项点击事件
    var onMenuItemClick: Toolbar.OnMenuItemClickListener? = null
)
