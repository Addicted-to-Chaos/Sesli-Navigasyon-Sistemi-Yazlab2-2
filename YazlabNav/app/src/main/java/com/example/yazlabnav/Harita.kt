package com.example.yazlabnav

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class Harita : AppCompatActivity(), OnMapReadyCallback {

    private var mGoogleMap:GoogleMap?=null
    private lateinit var autocompleteFragment:AutocompleteSupportFragment
    private val FINE_PERMISSION_CODE: Int = 1
    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
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
                val add=place.address
                val id=place.id
                val latLng=place.latLng!!
                val marker=addMarker(latLng)
                marker.title="$add"
                marker.snippet="$id"
                zoomOnMap(latLng)
            }
        })
        val mapFragment=supportFragmentManager.findFragmentById(R.id.haritaFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }
private fun zoomOnMap(latLng: LatLng){
    val newLatLngZoom=CameraUpdateFactory.newLatLngZoom(latLng,12f)
    mGoogleMap?.animateCamera(newLatLngZoom)
}
    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap=googleMap

        //add simple marker

        //Draggable marker
        mGoogleMap?.addMarker(MarkerOptions()
            .position(LatLng(12.234,12.543))
            .title("Draggable Marker")
            .draggable(true)
        )

        //Custom marker
        mGoogleMap?.addMarker(MarkerOptions()
            .position(LatLng(12.987,14.345))
            .title("Custom Marker")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_marker))
        )
        currentLocation?.let { location ->
            val sydney = LatLng(location.latitude, location.longitude)
            mGoogleMap?.addMarker(MarkerOptions().position(sydney).title("My location"))
            mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

    }
    private fun addMarker(position:LatLng): Marker
    {
        val marker=mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Marker")
        )
        return marker!!
    }
    private fun getLastLocation() {
         if(ActivityCompat.checkSelfPermission(
                this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_PERMISSION_CODE)
            return
        }
        val task: Task<Location> = fusedLocationProviderClient!!.lastLocation
            fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val mapFragment=supportFragmentManager.findFragmentById(R.id.haritaFragment) as SupportMapFragment
                mapFragment.getMapAsync {
                    // Harita hazır olduğunda yapılacak işlemler buraya yazılabilir
                }
            }
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Konum Erişimi Reddedildi.", Toast.LENGTH_SHORT).show()
            }
        }
    }



}