package com.fleeca.demoapp.util

/**
 * Created by WM-206 on 9/19/2017.
 */

class MarkerDataDto {
    var lat: Double = 0.toDouble()
    var lon: Double = 0.toDouble()
    var title: String? = null
    var snippet: String? = null
    var idM: Int = 0



    constructor(lat: Double, lon: Double, idM: Int) {
        this.lat = lat
        this.lon = lon
        this.title = title
        this.snippet = snippet
        this.idM = idM
    }
}
