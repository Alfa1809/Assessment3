package org.d3if0050.makanandaerah.model

import com.squareup.moshi.Json

data class Food(
    val id:Long,
    @Json(name = "nama") val namaMakanan:String,
    @Json(name = "asal") val asalMakanan:String,
    @Json(name = "image_id") val imageId:String
)
