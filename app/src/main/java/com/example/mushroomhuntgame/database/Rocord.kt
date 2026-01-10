package com.example.mushroomhuntgame.database

import com.google.android.gms.maps.model.LatLng


data class Record (
    var name:String,
    var location: LatLng,
    var timestamp: String,
    var score: Int
)
