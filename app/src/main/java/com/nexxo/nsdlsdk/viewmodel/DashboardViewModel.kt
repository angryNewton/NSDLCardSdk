package com.nexxo.nsdlsdk.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.model.*
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.Utility
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response

class DashboardViewModel :
    ViewModel() {

    //constructor(private val mainRepository: MainRepository)
    private val errorMessage = MutableLiveData<String>()
    var responseCode: Int = 0
    private lateinit var apiResponse: Response<SessionModel>
    private lateinit var mpinTokenResp: Response<MpinToken>
    private lateinit var CardDetails: Response<CardDetails>
    private lateinit var enableEcom: Response<EnableEcomResp>
    private lateinit var verifyMpin: Response<VerifyMpin>
    private lateinit var CVVDetails: Response<CVV>
    private lateinit var orderCard: Response<CardBlockResp>
    private lateinit var blockResponse: Response<CardBlockResp>
    val token = MutableLiveData<SessionModel>()
    val cardDetail = MutableLiveData<CardDetails>()
    val ecomEnale = MutableLiveData<EnableEcomResp>()
    val verifyMpinResponse = MutableLiveData<VerifyMpin>()
    val mpinTokenRespData = MutableLiveData<MpinToken>()
    val CVVDATA = MutableLiveData<CVV>()
    val orderCardData = MutableLiveData<CardBlockResp>()
    val cardBlockResponse = MutableLiveData<CardBlockResp>()
    var job: Job? = null
    val loading = MutableLiveData<Boolean>()
    var retrofitService: RetrofitService = RetrofitService.getInstance()
    var mainRepository: MainRepository = MainRepository(retrofitService)


    fun getNewSession(reqData: String,headers: Map<String, String>) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {


            val response = mainRepository.getSession(headers,requestBody)
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


    fun generateTokenForMpin(reqData: String) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {


            val response = mainRepository.generateTokenForMpin(requestBody)
            withContext(Dispatchers.Main) {
                try {

                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        mpinTokenResp = response
                        mpinTokenResp.body()
                        mpinTokenRespData.postValue(response.body())
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

    fun verifyMpin(reqData: String) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {


            val response = mainRepository.verifyMpin(requestBody)
            withContext(Dispatchers.Main) {
                try {

                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        verifyMpin = response
                        verifyMpin.body()
                        verifyMpinResponse.postValue(response.body())
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



    fun getCardDetails(reqData: String,headers: Map<String, String>) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = mainRepository.getCardDetails(headers,requestBody)
            withContext(Dispatchers.Main) {
                try {
                    CardDetails = response
                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        cardDetail.postValue(response.body())
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


    fun enableEcom(reqData: String,headers: Map<String, String>) {
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
                        loading.value = false
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

    fun blockCard(reqData: String,headers: Map<String, String>) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {


            val response = mainRepository.blockCard(headers,requestBody)
            withContext(Dispatchers.Main) {
                try {

                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        blockResponse = response
                     //   apiResponse.body()
                        cardBlockResponse.postValue(response.body())
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

    fun unblockCard(reqData: String,headers: Map<String, String>) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {


            val response = mainRepository.blockCard(headers,requestBody)
            withContext(Dispatchers.Main) {
                try {

                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        blockResponse = response
                        //   apiResponse.body()
                        cardBlockResponse.postValue(response.body())
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

    fun getCVVDetails(reqData: String,headers: Map<String, String>) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = mainRepository.CVVDETAILS(headers,requestBody)
            withContext(Dispatchers.Main) {
                try {
                     CVVDetails = response
                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        CVVDATA.postValue(response.body())
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


    fun orderCard(reqData: String,headers: Map<String, String>) {
        val requestBody =
            reqData.toRequestBody("application/json".toMediaTypeOrNull())
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val response = mainRepository.orderCard(headers,requestBody)
            withContext(Dispatchers.Main) {
                try {
                    orderCard = response
                    Utility.printRetrofitResponseBodyData(response)
                    if (response.isSuccessful) {
                        orderCardData.postValue(response.body())
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





    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}