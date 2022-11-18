package com.nexxo.nsdlsdk.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.model.SessionModel
import com.nexxo.nsdlsdk.model.SetPinResp
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.Utility
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response

class SetPinViewModel:
    ViewModel() {
    var job: Job? = null
    private val errorMessage = MutableLiveData<String>()
    var responseCode: Int = 0
    val loading = MutableLiveData<Boolean>()
    var retrofitService: RetrofitService = RetrofitService.getInstance()
    var mainRepository: MainRepository = MainRepository(retrofitService)
    private lateinit var apiResponse: Response<SetPinResp>
    val token = MutableLiveData<SetPinResp>()

    fun setPin(reqData: String,headers: Map<String, String>) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {


            val response = mainRepository.setPin(headers,requestBody)
            withContext(Dispatchers.Main) {
                try {

                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        apiResponse = response
                        apiResponse.body()
                        token.postValue(response.body())
                        loading.value = false
                    } else {
//                        onError("Error : ${response.message()} ")
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val message: String =
                                jObjError.optString(Constants.statusMessage_WS, "")
                            val strMessage: String = jObjError.optString(Constants.message_WS, "")
                            if (!TextUtils.isEmpty(strMessage)) {
                                onError(strMessage, response.code())
                            } else if (!TextUtils.isEmpty(message)) {
                                onError(message, response.code())
                            } else {
                                onError(jObjError.toString(), response.code())
                            }
                            Utility.logData("jObjError :$jObjError")
                        } catch (e: java.lang.Exception) {
                            onError(e.message.toString(), 0)
                        }
                    }
                } catch (e: Exception) {
                    e.message?.let { Utility.logData(it) }
                }
            }
        }

    }

    @DelicateCoroutinesApi
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        GlobalScope.launch {
            delay(1000)     // 1 sec delay

            // do some background task

            withContext(Dispatchers.Main) {
                // call to UI thread
                onError("Exception handled: ${throwable.localizedMessage}", 0)
            }
        }
    }

    private fun onError(message: String, responseCodeAPI: Int) {
        try {
            Utility.logData(message)
            responseCode = responseCodeAPI
            errorMessage.value = message
            loading.value = false
        } catch (e: Exception) {
            responseCode = responseCodeAPI
            errorMessage.value = e.message
            loading.value = false
        }
    }


}