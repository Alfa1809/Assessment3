package org.d3if0050.makanandaerah.network

import com.squareup.moshi.Moshi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if0050.makanandaerah.model.Food
import org.d3if0050.makanandaerah.model.OpStatus
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val BASE_URL = "https://rest-api-alfa1809-foods.000webhostapp.com/api/"

private val moshi = Moshi.Builder()
    .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()
interface FoodApiService {
    //@GET("static-api.json")
    @GET("foods.php")
    suspend fun getFood(
        @Header("Authorization") userId: String
    ):List<Food>

    @Multipart
    @POST("foods.php")
    suspend fun postFood(
        @Header("Authorization") userId: String,
        @Part("nama") nama: RequestBody,
        @Part("asal") asal: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @FormUrlEncoded
    @POST("deleteFoods.php")
    suspend fun deleteFood(
        @Header("Authorization") userId: String,
        @Field("id") id: String
    ) : OpStatus
}

object FoodApi{
    val service: FoodApiService by lazy {
        retrofit.create(FoodApiService::class.java)
    }
    fun getFoodUrl(imageId: String): String{
        //return "$BASE_URL$imageId.jpg"
        return "${BASE_URL}image.php?id=$imageId"
    }
}

enum class ApiStatus{
    LOADING, SUCCESS, FAILED
}
