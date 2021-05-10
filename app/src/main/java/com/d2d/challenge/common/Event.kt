package com.d2d.challenge.common

enum class Event(val eventName: String) {

    BookingOpened("bookingOpened"),
    BookingClosed("bookingClosed"),
    VehicleLocationUpdated("vehicleLocationUpdated"),
    StatusUpdated("statusUpdated"),
    StoppageLocationUpdated("intermediateStopLocationsChanged")
}