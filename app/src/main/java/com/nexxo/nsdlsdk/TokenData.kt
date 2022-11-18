package com.nexxo.nsdlsdk

import android.content.Context
import android.text.TextUtils
import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.interfaces.iTokenData
import com.nexxo.nsdlsdk.model.MpinToken
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.SdkConfig
import com.nexxo.nsdlsdk.utility.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response

public class TokenData( context: Context) {
    var mcontext:Context = context
    var retrofitService: RetrofitService = RetrofitService.getInstance()
    var mainRepository: MainRepository = MainRepository(retrofitService)
    private lateinit var mpinTokenResp: Response<MpinToken>

    public fun createSessionRequest(iData:iTokenData) {
        var jObject: JSONObject = JSONObject()
        try {

            var mainObj: JSONObject = JSONObject()
            var appIdObject: JSONObject = JSONObject()
            var deviceIdentifireObject: JSONObject = JSONObject()
            appIdObject.put("appid", SdkConfig.appID)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("appregflg", "01")
            appIdObject.put(
                "pushnkey",
                "ehy_bhncSVS0mecz8aWuOI:APA91bGH5hAbvXEWzQxr51bdZUZD8Y5_3hHGmOyUyBb8rJT_HtdhoTi9-W7-dTaSN3sj459IbmHSvMrUUPYJcZh5u6p1PXbUcVI0-9A-6Z_BXQ_VVh9D-vO0vJYa3KS7lZEOAig8rIjg"
            )

            deviceIdentifireObject.put("custunqid", SdkConfig.customerUniqueId)
            deviceIdentifireObject.put("deviceid", SdkConfig.deviceID)
            deviceIdentifireObject.put("deviceunqid", SdkConfig.deviceUniqueID)

            jObject.put("appdtls", appIdObject)
            jObject.put("deviceidentifier", deviceIdentifireObject)
            jObject.put("channelid", SdkConfig.channelId)
            jObject.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
            mainObj.put("token", SdkConfig.token)

            var dataObj: JSONObject = JSONObject()
            dataObj.put("userid", SdkConfig.customerId)
            dataObj.put("usertype", Constants.USER_TYPE)
            dataObj.put("role", Constants.ROLE)

            mainObj.put("custcreddtls",dataObj)

            mainObj.put("deviceidentifier",deviceIdentifireObject)
            mainObj.put("appdtls",appIdObject)

            var encryptedData = Utility.encryptpayload(jObject.toString(), Constants.ENCRYPTION_KEY)
            Utility.logData("Decrytped data $jObject")
            Utility.logData("Encrytped data $encryptedData")
            mainObj.put("channelid", SdkConfig.channelId)
            var reqId = Utility.generateSequenceNumber()
            //  mainObj.put("requestid",reqId)
            mainObj.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
            // mainObj.put("encdata", encryptedData)
            mainObj.put("reqtokentype", "TXNAUTH")
            Constants.REQUESTID = reqId.toString()
           // viewModel.generateTokenForMpin(mainObj.toString())


            try {

                CoroutineScope(
                    Dispatchers.IO + Utility.onErrorCoroutineExceptionHandler(
                        mcontext
                    )
                ).launch {
                    val requestBody =
                        mainObj.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    val response = mainRepository.generateTokenForMpin(requestBody)
                    mpinTokenResp = response
                    withContext(Dispatchers.Main) {
                        try {
                            Utility.printRetrofitResponseBodyData(response)
                            //  binding.swipreRefreshLayout.isRefreshing = false
                            if (response.isSuccessful) {
                                mpinTokenResp = response
                                mpinTokenResp.body()
                                mpinTokenResp.body()!!.tokenDtls!!.pwdkey?.let {
                                    mpinTokenResp.body()!!.tokenDtls?.tokenkey?.let { it1 ->
                                        iData.getTokenData(
                                            it, it1
                                        )
                                    }
                                }

                            } else {
                                iData.getTokenError("Something went wrong")
                            }
                        } catch (e: Exception) {
                            e.message?.let { Utility.logData(it) }
                        }
                    }
                }

            } catch (e: Exception) {
                e.message?.let { Utility.logData(it) }
            }




            Utility.logData("mpin token request $mainObj")


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}