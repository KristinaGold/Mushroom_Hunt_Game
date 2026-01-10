package com.example.mushroomhuntgame
import android.app.Application
import com.example.mushroomhuntgame.database.RecordManager
import com.example.mushroomhuntgame.utilities.EffectManager

class Application : Application() {


    override fun onCreate(){
        super.onCreate()

        EffectManager.init(this)
        RecordManager.init(this)
        //GameManager.init(this)


    }
}