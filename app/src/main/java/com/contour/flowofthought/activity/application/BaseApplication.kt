package com.contour.flowofthought.activity.application

import android.app.Application

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
    companion object {
        lateinit var INSTANCE: Application
    }
}