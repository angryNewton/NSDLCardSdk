package com.nexxo.nsdlsdk.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.model.EnableEcomResp
import com.nexxo.nsdlsdk.model.SessionModel
import com.nexxo.nsdlsdk.model.SetPinResp
import com.nexxo.nsdlsdk.model.UpdateLimitResp
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.Utility
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response

class CardLimitActivityViewModel:
    ViewModel() {
    var job: Job? = null
    private val errorMessage = MutableLiveData<String>()
    var responseCode: Int = 0
    private lateinit var enableEcom: Response<EnableEcomResp>
    val ecomEnale = MutableLiveData<EnableEcomResp>()
    val updateLimitLive = MutableLiveData<SessionModel>()
    val loading = MutableLiveData<Boolean>()
    var retrofitService: RetrofitService = RetrofitService.getInstance()
    var mainRepository: MainRepository = MainRepository(retrofitService)
    private lateinit var apiResponse: Response<SetPinResp>
    private lateinit var updateLimit: Response<SessionModel>
    val token = MutableLiveData<SetPinResp>()

    fun enableTransactions(reqData: String,headers: Map<String, String>) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = mainRepository.enableEcom(headers,requestBody)
            withContext(Dispatchers.Main) {
                try {
                    enableEcom = response
                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        ecomEnale.postValue(response.body())
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

    fun setLimit(reqData: String,headers: Map<String, String>) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {


            val response = mainRepository.updateLimit(headers,requestBody)
            withContext(Dispatchers.Main) {
                try {

                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        updateLimit = response
                       // apiResponse.body()
                        updateLimitLive.postValue(response.body())
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