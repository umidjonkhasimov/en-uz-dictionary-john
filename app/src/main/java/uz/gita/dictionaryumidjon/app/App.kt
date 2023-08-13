package uz.gita.dictionaryumidjon.app

import android.app.Application
import uz.gita.dictionaryumidjon.database.MyDBHelper

class App : Application() {
    override fun onCreate() {
        MyDBHelper.initialize(this)
        super.onCreate()
    }
}