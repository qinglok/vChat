package me.linx.vchat.app

import android.os.Handler
import android.os.Looper
import com.facebook.stetho.Stetho
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import me.linx.vchat.app.common.base.BaseApplication
import me.linx.vchat.app.constant.AppConfigs
import me.linx.vchat.app.db.AppDatabase

class App : BaseApplication() {

    companion object {
        val handler = Handler(Looper.getMainLooper())
    }

    override fun onCreate() {
        super.onCreate()

        FlowManager.init(
            FlowConfig.builder(applicationContext)
                .addDatabaseConfig(
                    DatabaseConfig.builder(AppDatabase::class.java)
                        .databaseName(AppConfigs.DatabaseName)
                        .build()
                )
                .build()
        )

        initStetho()
    }

    private fun initStetho() {
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        )
    }
}