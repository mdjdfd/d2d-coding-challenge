package com.d2d.challenge.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.d2d.challenge.BR
import com.d2d.challenge.R
import com.d2d.challenge.common.Event
import com.d2d.challenge.data.entity.IntermediateStopLocationsItem
import com.d2d.challenge.data.entity.Payload
import com.d2d.challenge.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_maps.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var initialMarker: Marker? = null
    var isMarkerRotating = false

    private val mapsViewModel: MapsViewModel by viewModels()

    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)


        setupObserver()

        binding.executePendingBindings()


//         Obtain the SupportMapFragment and get notified when the map is ready to be used.
        var mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    private fun setupObserver() {
        mapsViewModel.payload.observe(this, {
            it?.let {
                updateUi(it)
                updateMaps(it)
            }
        })
    }

    private fun updateUi(payload: Payload) {

        if (payload.event.equals(Event.BookingOpened.eventName, true)) {
            textview_source.text = String.format(
                resources.getString(R.string.text_pickup),
                payload.statusCarLocation?.pickupLocation?.address
            )
            textview_destination.text = String.format(
                resources.getString(R.string.text_dropoff),
                payload.statusCarLocation?.dropoffLocation?.address
            )
            binding.setVariable(BR.payload, payload)
        }

        payload.statusRide?.let {
            binding.setVariable(BR.payload, payload)
        }

    }


    lateinit var firstLatLng: LatLng
    lateinit var secondLatLng: LatLng
    var flag = false
    private fun updateMaps(payload: Payload) {

        when (payload.event) {
            Event.BookingOpened.eventName -> {
                val lat = payload.statusCarLocation?.vehicleLocation?.lat
                val lng = payload.statusCarLocation?.vehicleLocation?.lng
                ifLatLngNotNull(lat, lng) { lat, lng ->
                    firstLatLng = LatLng(lat, lng)
                }
                passGeoCoordinate(firstLatLng)
                updateStoppageMarkers(payload.statusCarLocation?.intermediateStopLocations)
            }


            Event.VehicleLocationUpdated.eventName -> {
                val lat = payload.statusCarLocation?.lat
                val lng = payload.statusCarLocation?.lng
                ifLatLngNotNull(lat, lng) { lat, lng ->
                    secondLatLng = LatLng(lat, lng)
                }
                passGeoCoordinate(secondLatLng)
                flag = true
            }


            Event.StoppageLocationUpdated.eventName -> updateStoppageMarkers(payload.statusStopLocations)

            else -> Unit
        }

        if (flag) {
            initialMarker?.let {
                rotateMarker(initialMarker!!, firstLatLng, secondLatLng)
                firstLatLng = secondLatLng
            }
        }
    }

    private fun updateStoppageMarkers(statusStopLocations: List<IntermediateStopLocationsItem?>?) {
        statusStopLocations?.let {
            for (i in statusStopLocations.indices) {

                val lat = statusStopLocations[i]?.lat
                val lng = statusStopLocations[i]?.lng

                ifLatLngNotNull(lat, lng) { lat, lng ->
                    googleMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                lat, lng
                            )
                        ).icon(
                            BitmapDescriptorFactory.fromResource(
                                R.drawable.ic_stoppage
                            )
                        ).anchor(0.5f, 0.5f)
                    )
                }
            }
        }


    }

    private fun passGeoCoordinate(latlng: LatLng) {
        googleMap?.let {

            if (initialMarker == null) {
                initialMarker = googleMap.addMarker(
                    MarkerOptions().position(latlng).icon(
                        BitmapDescriptorFactory.fromResource(
                            R.drawable.ic_car
                        )
                    ).anchor(0.5f, 0.5f)
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14F))
            } else {
                initialMarker?.position = latlng
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }


    private fun rotateMarker(marker: Marker, startPos: LatLng, stopPos: LatLng) {
        val toRotation = navigationBearing(startPos, stopPos)

        if (!isMarkerRotating) {
            val handler = Handler(Looper.getMainLooper())
            val start = SystemClock.uptimeMillis()
            val startRotation = marker.rotation
            val duration: Long = 2000
            val interpolator: Interpolator = LinearInterpolator()
            handler.post(object : Runnable {
                override fun run() {
                    isMarkerRotating = true
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                    val rot = t * toRotation.toFloat() + (1 - t) * startRotation
                    marker.rotation = if (-rot > 180) rot / 2 else rot
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, duration)
                    } else {
                        isMarkerRotating = false
                    }
                }
            })
        }

    }


    fun navigationBearing(sourceLatLng: LatLng, destinationLatLng: LatLng): Float {
        return SphericalUtil.computeHeading(sourceLatLng, destinationLatLng).toFloat()
    }


}

inline fun <A, B, R> ifLatLngNotNull(a: A?, b: B?, action: (A, B) -> R) {
    if (a != null && b != null) {
        action(a, b)
    }
}