package com.example.mushroomhuntgame.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mushroomhuntgame.R
import com.example.mushroomhuntgame.database.Record
import com.example.mushroomhuntgame.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

class FragmentMap: Fragment() , OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun zoomToRecord(record: Record) {
        val position = record.location

        if (googleMap == null) {
            return
        }
        googleMap?.apply {
            clear()
            addMarker(MarkerOptions().position(position).title(record.name))
            animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map    }
}