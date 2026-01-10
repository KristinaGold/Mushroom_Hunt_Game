package com.example.mushroomhuntgame.callbacks

import com.example.mushroomhuntgame.database.Record

interface RecordCallback {
    fun onRecordSelected(record: Record)
}