package com.example.lynx_workout.application

import android.app.Application
import com.example.lynx_workout.koin.AppModules
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.startKoin

class LynxApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, AppModules.appModules)
        Hawk.init(this).build()

    }
}