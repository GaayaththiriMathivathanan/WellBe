package com.example.wellbe.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.wellbe.R
import com.example.wellbe.models.CycleEntry
import com.example.wellbe.storage.Prefs
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class CyclingFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var googleMap: GoogleMap? = null

    private var lastLocation: Location? = null
    private var distance = 0.0 // km
    private var steps = 0
    private var calories = 0.0

    private lateinit var txtDistance: TextView
    private lateinit var txtSteps: TextView
    private lateinit var txtCalories: TextView
    private lateinit var chronometer: Chronometer
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnStop: Button
    private lateinit var btnHistory: Button

    private lateinit var prefs: Prefs
    private var isTracking = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cycling, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI elements
        txtDistance = view.findViewById(R.id.txtDistance)
        txtSteps = view.findViewById(R.id.txtSteps)
        txtCalories = view.findViewById(R.id.txtCalories)
        chronometer = view.findViewById(R.id.chronometer)
        btnStart = view.findViewById(R.id.btnStart)
        btnPause = view.findViewById(R.id.btnPause)
        btnStop = view.findViewById(R.id.btnStop)
        btnHistory = view.findViewById(R.id.btnCyclingHistory)

        prefs = Prefs(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000L // update every 2 sec
        ).build()

        // callback for location updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateMetrics(location)
                    updateMap(location)
                }
            }
        }

        // setup Map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // button actions
        btnStart.setOnClickListener { startTracking() }
        btnPause.setOnClickListener {
            if (isTracking) {
                pauseTracking()
                btnPause.text = "Resume"
            } else {
                resumeTracking()
                btnPause.text = "Pause"
            }
        }
        btnStop.setOnClickListener { stopTracking() }
        btnHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CyclingHistoryFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "Location permission required", Toast.LENGTH_SHORT).show()
            return
        }

        distance = 0.0
        steps = 0
        calories = 0.0
        lastLocation = null

        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        isTracking = true

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun pauseTracking() {
        chronometer.stop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isTracking = false
    }

    @SuppressLint("MissingPermission")
    private fun resumeTracking() {
        chronometer.start()
        isTracking = true
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun stopTracking() {
        chronometer.stop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isTracking = false

        val duration = (SystemClock.elapsedRealtime() - chronometer.base) / 1000
        val durationStr = String.format("%02d:%02d", duration / 60, duration % 60)

        // ✅ Save Ride
        val entry = CycleEntry(
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            duration = durationStr,
            distance = String.format("%.2f km", distance),
            calories = String.format("%.1f kcal", calories)
        )
        val rides = prefs.loadCycling()
        rides.add(entry)
        prefs.saveCycling(rides)

        Toast.makeText(requireContext(), "Ride saved! Distance: ${String.format("%.2f", distance)} km", Toast.LENGTH_LONG).show()
    }

    private fun updateMetrics(location: Location) {
        if (lastLocation != null) {
            val delta = lastLocation!!.distanceTo(location) / 1000.0 // km
            distance += delta
            steps += (delta * 1300).toInt()
            calories = distance * 30
        }
        lastLocation = location

        txtDistance.text = String.format("%.2f km", distance)
        txtSteps.text = "$steps steps"
        txtCalories.text = String.format("%.1f kcal", calories)
    }

    private fun updateMap(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        googleMap?.apply {
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            lastLocation?.let {
                addPolyline(
                    PolylineOptions().add(LatLng(it.latitude, it.longitude), latLng)
                        .width(5f)
                        .color(android.graphics.Color.BLUE)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
        }
    }
}
