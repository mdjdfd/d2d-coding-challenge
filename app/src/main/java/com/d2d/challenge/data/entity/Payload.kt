package com.d2d.challenge.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Getter and setter for data serialization and deserialization
 */
data class Payload(

    //Eligible for custom deserialization as a json string
    var statusRide: String? = null,

    //Eligible for custom deserialization as a json object
    var statusCarLocation: Data? = null,

    //Eligible for custom deserialization as a json array
    var statusStopLocations: List<IntermediateStopLocationsItem?>? = null,

    @SerializedName("event")
    val event: String
)

data class Data(

    @SerializedName("vehicleLocation")
    val vehicleLocation: VehicleLocation? = null,

    @SerializedName("dropoffLocation")
    val dropoffLocation: DropoffLocation? = null,

    @SerializedName("intermediateStopLocations")
    val intermediateStopLocations: List<IntermediateStopLocationsItem?>? = null,

    @SerializedName("pickupLocation")
    val pickupLocation: PickupLocation? = null,

    @SerializedName("status")
    val status: String? = null,


    @SerializedName("address")
    val address: String? = null,

    @SerializedName("lng")
    val lng: Double? = null,

    @SerializedName("lat")
    val lat: Double? = null


)

data class PickupLocation(

    @SerializedName("address")
    val address: String,

    @SerializedName("lng")
    val lng: Double,

    @SerializedName("lat")
    val lat: Double
)

data class VehicleLocation(

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("lng")
    val lng: Double,

    @SerializedName("lat")
    val lat: Double
)

data class IntermediateStopLocationsItem(

    @SerializedName("address")
    val address: String,

    @SerializedName("lng")
    val lng: Double,

    @SerializedName("lat")
    val lat: Double
)

data class DropoffLocation(

    @SerializedName("address")
    val address: String,

    @SerializedName("lng")
    val lng: Double,

    @SerializedName("lat")
    val lat: Double
)
