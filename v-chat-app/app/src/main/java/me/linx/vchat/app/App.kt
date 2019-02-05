package me.linx.vchat.app

import android.app.Application
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.leakcanary.LeakCanary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.constant.CodeMap
import me.linx.vchat.app.net.HttpTask
import me.linx.vchat.app.net.HttpWrapper
import me.linx.vchat.app.ui.sign.SignInFragment

@Suppress("unused")
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        initLog()
        initCrash()
        initLeakCanary()
        initHttpTask()
        initStetho()
    }

    /**
     *  添加登录超时处理
     */
    private fun initHttpTask() {
        HttpWrapper.addHttpTask(CodeMap.ErrorTokenFailed, object : HttpTask {
            override fun handle() {
                SPUtils.getInstance().put(AppKeys.SP_currentUserId, 0L)

                ActivityUtils.getTopActivity()?.let { activity ->
                    if (activity is AppActivity) {
                        GlobalScope.launch(Dispatchers.Main) {
                            MaterialAlertDialogBuilder(activity)
                                .setTitle(R.string.login_timeout)
                                .setMessage(R.string.login_first)
                                .setOnDismissListener {
                                    activity.supportFragmentManager.popBackStack(
                                        null,
                                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                                    )
                                    activity.supportFragmentManager.beginTransaction()
                                        .replace(
                                            R.id.fragment_container,
                                            SignInFragment(),
                                            SignInFragment::class.java.name
                                        )
                                        .commit()
                                }
                                .setPositiveButton(R.string.ok, null)
                                .show()
                        }
                    }
                }
            }
        })
    }

    /**
     * 初始化数据库浏览器(Only Chrome)
     */
    private fun initStetho() {
//        com.facebook.stetho.Stetho.initialize(
//            com.facebook.stetho.Stetho.newInitializerBuilder(this)
//                .enableDumpapp(com.facebook.stetho.Stetho.defaultDumperPluginsProvider(this))
//                .enableWebKitInspector(com.facebook.stetho.Stetho.defaultInspectorModulesProvider(this))
//                .build()
//        )
    }

    /**
     * 初始化内存泄露检查工具
     */
    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }

    // init it in ur application
    private fun initLog() {
        val isDebug = AppUtils.isAppDebug()
        val config = LogUtils.getConfig()
            .setLogSwitch(isDebug)// 设置 log 总开关，包括输出到控制台和文件，默认开
            .setConsoleSwitch(isDebug)// 设置是否输出到控制台开关，默认开
            .setLog2FileSwitch(false)// 打印 log 时是否存到文件的开关，默认关
            .setGlobalTag(null)// 设置 log 全局标签，默认为空
            // 当全局标签不为空时，我们输出的 log 全部为该 tag，
            // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
            .setLogHeadSwitch(true)// 设置 log 头信息开关，默认为开
            .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
            .setFilePrefix("")// 当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd.txt"
            .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
            .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
            .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
            .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
            .setStackDeep(1)// log 栈深度，默认为 1
            .setStackOffset(0)// 设置栈偏移，比如二次封装的话就需要设置，默认为 0
//            .setSaveDays(3)// 设置日志可保留天数，默认为 -1 表示无限时长
            // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
            .addFormatter(object : LogUtils.IFormatter<ArrayList<*>>() {
                override fun format(list: ArrayList<*>?): String {
                    return "LogUtils Formatter ArrayList { " + list.toString() + " }"
                }
            })
        LogUtils.d(config.toString())
    }

    private fun initCrash() {
        try {
            CrashUtils.init { crashInfo, _ ->
                LogUtils.e(crashInfo)
                AppUtils.relaunchApp()
            }
        } catch (e: SecurityException) {
            LogUtils.e(e)
        }
    }
}