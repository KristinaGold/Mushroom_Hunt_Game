package com.example.mushroomhuntgame.database

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecordManager(context : Context) {

    private val SP_FILE: String = "MyDBFile"
    private val KEY_RECORDS = "top_scores"
    private val gson = Gson()
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)


    companion object {

        @Volatile
        private var instance: RecordManager? = null

        fun init(context: Context): RecordManager {
            return instance ?: synchronized(this) {
                instance ?: RecordManager(context).also { instance = it }
            }
        }

        fun getInstance(): RecordManager {
            return instance ?: throw IllegalStateException(
                "DataSP must be initialized by calling init(context) before use."
            )
        }

    }

    fun getTopRecords(): List<Record> {
        val json = sharedPreferences.getString(KEY_RECORDS, null) ?: return emptyList()
        val type = object : TypeToken<List<Record>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addRecord(newRecord: Record) {
        val currentRecords = getTopRecords().toMutableList()
        currentRecords.add(newRecord)

        currentRecords.sortByDescending { it.score }
        val topTen = currentRecords.take(10)
        val json = gson.toJson(topTen)
        sharedPreferences.edit { putString(KEY_RECORDS, json) }
    }
}