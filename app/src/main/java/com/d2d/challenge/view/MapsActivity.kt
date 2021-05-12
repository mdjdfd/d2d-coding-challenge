package com.d2d.challenge.view

import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.d2d.challenge.BR
import com.d2d.challenge.R
import com.d2d.challenge.common.Event
import com.d2d.challenge.common.ifLatLngNotNull
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_maps.*


/**
 * Activity to load google maps and show real time location status of a vehicle, pickup location,
 * source to destination point, stoppage between source to destination point and moving direction of a vehicle.
 * AndroidEntryPoint annotation indicate class to be setup for injection using hilt dagger android component.
 */
@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var googleMap: GoogleMap
    private var initialMarker: Marker? = null

    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null
    private var isCurrentLocation = false

    private val mapsViewModel: MapsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)
        binding.executePendingBindings()


        setupObserver()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    /**
     * Observe live data received from view model. Also instantiate all UI updating
     */
    private fun setupObserver() {
        mapsViewModel.payload.observe(this, {
            it?.let {
                updateUi(it)
                updateMaps(it)
            }
        })
    }

    /**
     * Update ui element for pickup, drop off location and current ride status.
     * @param payload payload object used for extracting pickup, drop off and ride status field to update UI component
     */
    private fun updateUi(payload: Payload) {

        when (payload.event) {
            Event.BookingOpened.eventName -> {
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

            Event.BookingClosed.eventName -> {
                textview_ride_status.visibility = View.GONE
            }
        }

        payload.statusRide?.let {
            binding.setVariable(BR.payload, payload)
        }

    }

    /**
     * Update maps by extracting latitude and longitude of a location based on event occurred. This method
     * also helps to identify previous and next location.
     * @param payload payload object used for extracting vehicle current location using latitude and longitude.
     */
    private fun updateMaps(payload: Payload) {

        when (payload.event) {
            Event.BookingOpened.eventName -> {
                val lat = payload.statusCarLocation?.vehicleLocation?.lat
                val lng = payload.statusCarLocation?.vehicleLocation?.lng
                ifLatLngNotNull(lat, lng) { lat, lng ->
                    previousLatLng = LatLng(lat, lng)
                }
                previousLatLng?.let { passGeoCoordinate(it) }
                updateStoppageMarkers(payload.statusCarLocation?.intermediateStopLocations)
            }


            Event.VehicleLocationUpdated.eventName -> {
                val lat = payload.statusCarLocation?.lat
                val lng = payload.statusCarLocation?.lng
                ifLatLngNotNull(lat, lng) { lat, lng ->
                    currentLatLng = LatLng(lat, lng)
                }
                currentLatLng?.let { passGeoCoordinate(it) }
                isCurrentLocation = true
            }


            Event.StoppageLocationUpdated.eventName -> updateStoppageMarkers(payload.statusStopLocations)

            else -> Unit
        }

        if (isCurrentLocation) {
            initialMarker?.let {
                ifLatLngNotNull(previousLatLng, currentLatLng) { prev, curr ->
                    rotateMarker(initialMarker!!, prev, curr)
                    previousLatLng = currentLatLng
                }
            }
        }
    }


    /**
     * update markers in the google map based on vehicle stoppage locations. Number of marker depends on number of
     * stoppage point.
     * @param statusStopLocations list of stoppage location point used for extracting latitude and longitude of each
     * point.
     */
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
                        ).anchor(0.5f, 0.5f).title(statusStopLocations[i]?.address)
                    )
                }
            }
        }


    }

    /**
     * Method is responsible for marker customization and placing marker based on vehicle current location.
     * Also zoom google map to the updated location point.
     * @param latlng an object to detect geo location in google map contains latitude and longitude.
     */
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
     * @param googleMap object used for instantiating google map for the first time.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }


    /**
     * Rotate vehicle direction according to navigation bearing
     * @param marker marker object to place in map
     * @param previousLocation previous location LatLng of vehicle
     * @param currentLocation current location LatLng of vehicle
     */
    private fun rotateMarker(marker: Marker, previousLocation: LatLng, currentLocation: LatLng) {
        marker.rotation = navigationBearing(previousLocation, currentLocation)
    }

    /**
     * Navigate bearing based on previous and current location
     * @param previous previous location LatLng of vehicle
     * @param current current location LatLng of vehicle
     * @return return computed bearing in Float
     */
    private fun navigationBearing(previous: LatLng, current: LatLng): Float {
        val previousLocation = Location("")
        previousLocation.latitude = previous.latitude
        previousLocation.longitude = previous.longitude

        val currentLocation = Location("")
        currentLocation.latitude = current.latitude
        currentLocation.longitude = current.longitude

        return previousLocation.bearingTo(currentLocation)
    }

}
