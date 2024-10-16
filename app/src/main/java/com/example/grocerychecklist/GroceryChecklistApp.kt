package com.example.grocerychecklist

import android.app.Application
import com.example.grocerychecklist.di.AppModule
import com.example.grocerychecklist.di.AppModuleImpl

class GroceryChecklistApp: Application() {

    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}