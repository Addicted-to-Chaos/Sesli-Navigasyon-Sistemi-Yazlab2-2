package com.example.yazlabnav

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.Locale


class Harita : AppCompatActivity(), OnMapReadyCallback, TextToSpeech.OnInitListener {

    private var mGoogleMap:GoogleMap?=null
    private lateinit var autocompleteFragment:AutocompleteSupportFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var startLat:Double=0.0
    var endLat:Double=0.0
    var startLng:Double=0.0
    var endLng:Double=0.0


    private var textToSpeech: TextToSpeech? = null

    @SuppressLint("MissingInflatedId")
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
                showToastAndSpeak("Ararken bir sorun oluştu!")
            }

            override fun onPlaceSelected(place: Place) {
                val add=place.address
                val id=place.id
                val latLng=place.latLng!!
                val marker=addMarker(latLng)
                marker.title="$add"
                marker.snippet="$id"
                zoomOnMap(latLng)
                endLat=place.latLng.latitude
                endLng=place.latLng.longitude
            }
        })
        val mapFragment=supportFragmentManager.findFragmentById(R.id.haritaFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()

        //region rota için
        var rotaButton: Button = findViewById(R.id.rotaOlustur)
        rotaButton.setOnClickListener {
            if(endLat==0.0){

               showToastAndSpeak("Lütfen gidilecek lokasyonu seçiniz.")

            }
            else{
                getDirections(endLat,endLng)

            }
        }
        //endregion

        textToSpeech = TextToSpeech(this, this)
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
        getCurrentLocation()
    }
    private fun addMarker(position:LatLng): Marker
    {
        val marker=mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Buradasın")
        )
        startLat=position.latitude
        startLng=position.longitude
        return marker!!
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_LOCATION
            )
            return
        } else {
            // İzin zaten verilmişse, mevcut konumu al
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        addMarker(currentLatLng)
                        zoomOnMap(currentLatLng)
                    }
                }
                .addOnFailureListener { e ->
                    showToastAndSpeak("Konum alınamadı.")
                }
        } catch (securityException: SecurityException) {
            showToastAndSpeak("Konum izni reddedildi.")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Konum izni verildi, mevcut konumu al
                getCurrentLocation()
            } else {
                showToastAndSpeak("Konum izni reddedildi.")
            }
        }}
    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 100
    }

//region Harita tipi seçmek için

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_harita,menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId

        if(id == R.id.normalMap){
         mGoogleMap?.mapType=GoogleMap.MAP_TYPE_NORMAL
        }

        if(id == R.id.hibritMap){
            mGoogleMap?.mapType=GoogleMap.MAP_TYPE_HYBRID
        }

        if(id == R.id.fizikselMap){
            mGoogleMap?.mapType=GoogleMap.MAP_TYPE_TERRAIN
        }

        if(id == R.id.uyduHarita){
            mGoogleMap?.mapType=GoogleMap.MAP_TYPE_SATELLITE
        }
        return super.onOptionsItemSelected(item)
    }

//endregion
// region Rota oluşturmak için

    private fun getDirections( endLat: Double, endLng: Double) {
        try {

            val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$endLat,$endLng")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    //endregion

    //region Kullanıcılar için TextToSpeech
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Dil değiştirmek isterseniz alttaki tr yi başka dil koduyla değiştirin //Kaan
            val result = textToSpeech?.setLanguage(Locale("tr"))

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Toast.makeText(this, "Bu dil desteklenmiyor.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "TextToSpeech başlatılamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

// showToastAndSpeak metodunu ekrana yazı yazdırken kullanın. hem sesli de söyleyecektir //Kaan
    private fun showToastAndSpeak(message: String) {
        Toast.makeText(this@Harita, message, Toast.LENGTH_LONG).show()
        speak(message)
    }
    //endregion

}