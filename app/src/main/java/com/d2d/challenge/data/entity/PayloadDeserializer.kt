package com.d2d.challenge.data.entity

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class PayloadDeserializer : JsonDeserializer<Payload> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Payload {
        var payload: Payload = Gson().fromJson(json, Payload::class.java)

        val jsonObject = json?.let {
             json.asJsonObject
        }


        if (jsonObject?.has("data") == true) {
            val elem = jsonObject["data"]
            if (elem != null && !elem.isJsonNull) {

                val valuesString = elem.toString()
                when {
                    valuesString.startsWith("{") -> payload.statusCarLocation =
                        Gson().fromJson(valuesString, Data::class.java)
                    valuesString.startsWith("[") -> payload.statusStopLocations = Gson().fromJson(
                        valuesString,
                        object : TypeToken<ArrayList<IntermediateStopLocationsItem?>?>() {}.type
                    )
                    else -> payload.statusRide = valuesString
                }
            }
        }
        return payload
    }
}