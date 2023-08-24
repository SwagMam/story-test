package com.example.submission1aplikasistory.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submission1aplikasistory.R
import com.example.submission1aplikasistory.data.Resource
import com.example.submission1aplikasistory.databinding.ActivityMapsBinding
import com.example.submission1aplikasistory.helper.UserPreferences
import com.example.submission1aplikasistory.helper.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setUpViewModel()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        enableMyLocation()
        manyMaker()
        val latLngIndonesia = LatLng(0.7893, 113.9213)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngIndonesia))
    }

    private fun manyMaker() {
        mapsViewModel.stories.observe(this) {
            when(it) {
                is Resource.Success -> {
                    showLoading(false)
                    it.data?.forEach { stories ->
                        mMap.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    stories.lat ?: 0.0,
                                    stories.lon ?: 0.0
                                )
                            ).title(
                                stories.name
                            ).snippet(
                                getDescription(
                                stories.lat ?: 0.0,
                                stories.lon ?: 0.0,
                                    stories.description ?: ""
                                )
                            )
                        )?. tag = stories
                    }
                }
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun getDescription(lat: Double, lon: Double, description: String): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = description
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun setUpViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        val factory = ViewModelFactory(pref)
        factory.setApplication(application)
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]
        fetchData()
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            mapsViewModel.getStories()
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) ==PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showLoading(state: Boolean) {
        binding.isLoading.visibility = if (state) View.VISIBLE else View.GONE
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) enableMyLocation()
        }
}