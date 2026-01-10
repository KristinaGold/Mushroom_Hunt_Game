package com.example.mushroomhuntgame.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mushroomhuntgame.utilities.EffectManager
import com.example.mushroomhuntgame.database.Record
import com.example.mushroomhuntgame.database.RecordManager
import com.example.mushroomhuntgame.databinding.ActivityScoreBinding
import com.example.mushroomhuntgame.utilities.hideSystemBars
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class ScoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var name: String = ""
    private var score: Int = 0
    private var totalTime : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("BUNDLE")
        score = bundle?.getInt("SCORE") ?: 0
        totalTime = bundle?.getString("TIME") ?: "00:00"


        binding.lblScore.text = "Your Score: $score"
        binding.lblTime.text = "Time: $totalTime"

        binding.btnSave.setOnClickListener {
            name = binding.nameInput.text.toString().ifEmpty { "Anonymous" }
            requestPermission()

        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }


    private val defaultLocation = LatLng(32.0853, 34.7818) //Tel-Aviv
    private val permission = Manifest.permission.ACCESS_FINE_LOCATION

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
        } else {
            finalSave(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finalSave(true)
            } else {
                finalSave(false)
            }
        }
    }


    private fun finalSave(useActualLocation: Boolean) {
        if (useActualLocation && ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val locationLatLng =
                    if (location != null) LatLng(location.latitude, location.longitude)
                    else defaultLocation
                val newRecord = Record(name, locationLatLng, totalTime, score)
                RecordManager.getInstance().addRecord(newRecord)
                openNewWindow()
            }
        } else {
            val newRecord = Record(name, defaultLocation, totalTime, score)
            RecordManager.getInstance().addRecord(newRecord)
            showMessage()
        }

    }


    private fun showMessage(){
        AlertDialog.Builder(this)
            .setTitle("Location disabled")
            .setMessage("Record saved with default location. To enable location go to phone settings.")
            .setPositiveButton("OK") { _, _ ->
                openNewWindow()
            }
            .show()
    }


    private fun openNewWindow() {
        val intent = Intent(this, RecordsActivity::class.java)
        startActivity(intent)
        finish()
    }

}