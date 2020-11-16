package cn.leizy.base

import android.app.Application

/**
 * @author Created by wulei
 * @date 2020/11/16, 016
 * @description
 */
class App : Application() {
    companion object {
        private var instance: App? = null

        fun getInstance(): App {
            return instance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}