package com.d2d.challenge.data.entity

import com.d2d.challenge.common.trimDoubleQuotes
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * Custom json deserializer to function duplicate json field with different return types
 */
class PayloadDeserializer : JsonDeserializer<Payload> {


    /**
     * actual deserializer implementation to indicate how json will be deserialize
     * @param json nullable JsonElement object
     * @param typeOfT nullable indicator of json type [string, object, array]
     * @param context nullable JsonDeserializationContext
     * @return return object after Deserialize operation
     */
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Payload {
        val payload: Payload = Gson().fromJson(json, Payload::class.java)

        val jsonObject = json?.let {
             json.asJsonObject
        }


        // "data" field Deserialization
        if (jsonObject?.has("data") == true) {
            val elem = jsonObject["data"]
            if (elem != null && !elem.isJsonNull) {

                val valuesString = elem.toString()
                when {
                    //object
                    valuesString.startsWith("{") -> payload.statusCarLocation =
                        Gson().fromJson(valuesString, Data::class.java)
                    //array
                    valuesString.startsWith("[") -> payload.statusStopLocations = Gson().fromJson(
                        valuesString,
                        object : TypeToken<ArrayList<IntermediateStopLocationsItem?>?>() {}.type
                    )
                    //string
                    else -> {
                        payload.statusRide = trimDoubleQuotes(valuesString)
                    }
                }
            }
        }
        return payload
    }
}