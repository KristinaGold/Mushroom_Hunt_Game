package com.example.mushroomhuntgame.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mushroomhuntgame.fragments.FragmentMap
import com.example.mushroomhuntgame.fragments.FragmentRecordList
import com.example.mushroomhuntgame.R
import com.example.mushroomhuntgame.callbacks.RecordCallback
import com.example.mushroomhuntgame.database.Record
import com.example.mushroomhuntgame.databinding.ActivityRecordsBinding
import com.example.mushroomhuntgame.utilities.hideSystemBars

class RecordsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordsBinding

    var fragmentMap = FragmentMap()
    var fragmentList = FragmentRecordList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemBars()

        val returnBtn = binding.returnBtn

        supportFragmentManager.beginTransaction()
            .add(R.id.layMap, fragmentMap)
            .add(R.id.layLst, fragmentList)
            .commit()


        val listener: RecordCallback = object : RecordCallback {
                override fun onRecordSelected(record: Record) {
                    fragmentMap.zoomToRecord(record)
                }
            }

        fragmentList.setListener(listener)

        returnBtn.setOnClickListener {
            finish()
        }
    }

}