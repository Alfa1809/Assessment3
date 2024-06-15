package org.d3if0050.makanandaerah.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if0050.makanandaerah.model.Food
import org.d3if0050.makanandaerah.network.ApiStatus
import org.d3if0050.makanandaerah.network.HewanApi
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Food>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userId: String) {
        viewModelScope.launch(Dispatchers.IO){
            status.value = ApiStatus.LOADING
            try {
                data.value = HewanApi.service.getHewan(userId = userId)
                status.value = ApiStatus.SUCCESS
                Log.d("MainViewModel", "${data.value}")
            }catch (e:Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(userId: String, nama: String, namaLatin: String, bitmap: Bitmap, mine: Int = 1){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = HewanApi.service.postHewan(
                    userId = userId,
                    nama = nama.toRequestBody("text/plain".toMediaTypeOrNull()),
                    namaLatin = namaLatin.toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = bitmap.toMultipartBody(),
                    mine = mine.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                )
                if (result.status == "success")
                    retrieveData(userId = userId)
                else
                    throw Exception(result.message)
            }catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deletingData(userId: String,id: Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val result = HewanApi.service.deleteHewan(userId = userId,id = id.toString())
                if (result.status == "success")
                    retrieveData(userId = userId)
                else
                    throw Exception(result.message)
            }catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part{
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG,80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(),0, byteArray.size
        )
        return MultipartBody.Part.createFormData(
            "image","image.jpg",requestBody
        )
    }

    fun clearMessage(){
        errorMessage.value = null
    }
}