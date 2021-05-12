package com.d2d.challenge.common


/**
 * enum class to distinguish all types of socket event
 * @param eventName name of the event
 */
enum class Event(val eventName: String) {

    BookingOpened("bookingOpened"),
    BookingClosed("bookingClosed"),
    VehicleLocationUpdated("vehicleLocationUpdated"),
    StatusUpdated("statusUpdated"),
    StoppageLocationUpdated("intermediateStopLocationsChanged")
}