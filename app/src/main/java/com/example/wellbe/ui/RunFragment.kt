package com.example.wellbe.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.wellbe.R
import com.example.wellbe.models.RunEntry
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

class RunFragment : Fragment(), OnMapReadyCallback {

    private lateinit var durationText: TextView
    private lateinit var distanceText: TextView
    private lateinit var stepsText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnStop: Button
    private lateinit var btnHistory: Button

    private lateinit var prefs: Prefs

    private var isRunning = false
    private var isPaused = false
    private var seconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning && !isPaused) {
                seconds++
                val time = String.format("%02d:%02d", seconds / 60, seconds % 60)
                durationText.text = time
                handler.postDelayed(this, 1000)
            }
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null
    private var totalDistance = 0.0
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_run, container, false)

        prefs = Prefs(requireContext())

        durationText = view.findViewById(R.id.durationText)
        distanceText = view.findViewById(R.id.distanceText)
        stepsText = view.findViewById(R.id.stepsText)
        caloriesText = view.findViewById(R.id.caloriesText)
        btnStart = view.findViewById(R.id.btnStart)
        btnPause = view.findViewById(R.id.btnPause)
        btnStop = view.findViewById(R.id.btnStop)
        btnHistory = view.findViewById(R.id.btnHistory)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapRun) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnStart.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                isPaused = false
                handler.post(timerRunnable)
                startLocationUpdates()
            }
        }

        btnPause.setOnClickListener {
            if (isRunning) {
                isPaused = !isPaused
                btnPause.text = if (isPaused) "Resume" else "Pause"
            }
        }

        btnStop.setOnClickListener {
            stopRunning()
        }

        btnHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RunHistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    if (lastLocation != null) {
                        val distance = lastLocation!!.distanceTo(location) / 1000.0
                        totalDistance += distance
                        distanceText.text = String.format("%.2f km", totalDistance)

                        caloriesText.text = String.format("%.1f kcal", totalDistance * 60)
                        stepsText.text = String.format("%.0f steps", totalDistance * 1300)

                        googleMap?.addPolyline(
                            PolylineOptions()
                                .add(
                                    LatLng(lastLocation!!.latitude, lastLocation!!.longitude),
                                    LatLng(location.latitude, location.longitude)
                                )
                                .color(requireContext().getColor(R.color.purple_500))
                                .width(8f)
                        )
                    }

                    lastLocation = location
                    googleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            17f
                        )
                    )
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopRunning() {
        if (!isRunning) return

        isRunning = false
        handler.removeCallbacks(timerRunnable)
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // ✅ Save Run History
        val runEntry = RunEntry(
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            duration = durationText.text.toString(),
            distance = String.format("%.2f km", totalDistance),
            calories = String.format("%.1f kcal", totalDistance * 60)
        )

        val runs = prefs.loadRuns()
        runs.add(runEntry)
        prefs.saveRuns(runs)

        Toast.makeText(requireContext(), "Run saved!", Toast.LENGTH_SHORT).show()

        // Reset UI
        seconds = 0
        totalDistance = 0.0
        durationText.text = "00:00"
        distanceText.text = "0.00 km"
        stepsText.text = "0 steps"
        caloriesText.text = "0.0 kcal"
    }

    override fun onPause() {
        super.onPause()
        if (isRunning) stopRunning()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timerRunnable)
    }
}
