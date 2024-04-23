package com.example.yazlabnav

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class Harita : AppCompatActivity(), OnMapReadyCallback {

    private var mGoogleMap:GoogleMap?=null
    private lateinit var autocompleteFragment:AutocompleteSupportFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_harita)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        Places.initialize(applicationContext,"AIzaSyA_EgLyHP5svhK0Vh1g8-zhP-EbkrED_q0")
        autocompleteFragment=supportFragmentManager.findFragmentById((R.id.autocomplete_fragment)) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID,Place.Field.ADDRESS,Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object:PlaceSelectionListener{
            override fun onError(p0: Status) {
                Toast.makeText(this@Harita,"Ararken Hata Oluştu!",Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
                val latLng=place.latLng!!
                zoomOnMap(latLng)
            }
        })
        val mapFragment=supportFragmentManager.findFragmentById(R.id.haritaFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
private fun zoomOnMap(latLng: LatLng){
    val newLatLngZoom=CameraUpdateFactory.newLatLngZoom(latLng,12f)
    mGoogleMap?.animateCamera(newLatLngZoom)
}
    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap=googleMap
    }
}