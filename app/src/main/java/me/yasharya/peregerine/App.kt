package me.yasharya.peregerine

import android.app.Application
import me.yasharya.peregerine.di.AppContainer

class App: Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}