package com.example.matrixcalculator

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.Intent

class WifiRssLogger : AppCompatActivity() {

    private lateinit var locationSpinner: Spinner
    private lateinit var logButton: Button
    private lateinit var resultText: TextView
    private lateinit var wifiManager: WifiManager
    private lateinit var dataLogger: DataLogger
    private val handler = Handler(Looper.getMainLooper())
    private var isScanning = false
    private val locations = listOf("Home", "Office", "Lab")
    private val samplesPerLocation = 100
    private val scanIntervalMs = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Remove the title bar for consistency
        setContentView(R.layout.activity_wifi_rss)

        // Initialize UI components
        locationSpinner = findViewById(R.id.locationSpinner)
        logButton = findViewById(R.id.logButton)
        resultText = findViewById(R.id.resultText)
        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        dataLogger = DataLogger()

        // Setup Spinner
        ArrayAdapter(this, android.R.layout.simple_spinner_item, locations).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = adapter
        }

        // Request permissions
        requestPermissions()

        // Back button click
        findViewById<Button>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish() // Close this Activity
        }

        // Log button click
        logButton.setOnClickListener {
            if (!isScanning) {
                val location = locations[locationSpinner.selectedItemPosition]
                startLogging(location)
            } else {
                stopLogging()
            }
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE
        )
        val neededPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (neededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toTypedArray(), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            resultText.text = "Permissions denied. Cannot scan WiFi."
        }
    }

    private fun startLogging(location: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            resultText.text = "Location permission required to scan WiFi"
            requestPermissions()
            return
        }

        isScanning = true
        logButton.text = "Stop Logging"
        resultText.text = "Logging RSS for $location..."
        dataLogger.clearData(location)

        var sampleCount = 0
        val scanRunnable = object : Runnable {
            override fun run() {
                if (ContextCompat.checkSelfPermission(this@WifiRssLogger, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    stopLogging()
                    resultText.text = "Location permission lost during scanning"
                    return
                }

                try {
                    wifiManager.startScan()
                    val scanResults = wifiManager.scanResults
                    if (scanResults.isNotEmpty()) {
                        val apData = scanResults.map { it.BSSID to it.level }
                        dataLogger.logSample(location, apData)
                        sampleCount++
                        resultText.text = "Logged $sampleCount/$samplesPerLocation samples for $location"
                    } else {
                        resultText.text = "No WiFi APs found for $location"
                    }

                    if (sampleCount < samplesPerLocation && isScanning) {
                        handler.postDelayed(this, scanIntervalMs)
                    } else {
                        stopLogging()
                        displayResults()
                    }
                } catch (e: SecurityException) {
                    stopLogging()
                    resultText.text = "Error: Permission denied during WiFi scan"
                } catch (e: Exception) {
                    stopLogging()
                    resultText.text = "Error during scan: ${e.message}"
                }
            }
        }
        handler.post(scanRunnable)
    }

    private fun stopLogging() {
        isScanning = false
        logButton.text = "Start Logging"
        handler.removeCallbacksAndMessages(null)
    }

    private fun displayResults() {
        val results = dataLogger.getResults()
        val builder = StringBuilder()
        results.forEach { (location, apData) ->
            builder.append("$location:\n")
            apData.forEach { (bssid, samples) ->
                if (samples.isNotEmpty()) {
                    val minRss = samples.minOrNull() ?: 0
                    val maxRss = samples.maxOrNull() ?: 0
                    builder.append("AP $bssid: RSS Range: $minRss to $maxRss dBm\n")
                }
            }
            builder.append("\n")
        }
        resultText.text = if (builder.isEmpty()) "No data logged" else builder.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLogging()
    }
}

class DataLogger {
    private val data = mutableMapOf<String, MutableMap<String, MutableList<Int>>>()

    fun logSample(location: String, apData: List<Pair<String, Int>>) {
        val locationData = data.getOrPut(location) { mutableMapOf() }
        apData.forEach { (bssid, rss) ->
            val samples = locationData.getOrPut(bssid) { mutableListOf() }
            samples.add(rss)
        }
    }

    fun clearData(location: String) {
        data[location]?.clear()
    }

    fun getResults(): Map<String, Map<String, List<Int>>> {
        return data
    }
}