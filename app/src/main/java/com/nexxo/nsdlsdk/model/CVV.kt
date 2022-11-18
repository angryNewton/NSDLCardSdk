package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class CVV(
    @SerializedName("responsecode" ) var responsecode : String?       = null,
    @SerializedName("response"     ) var response     : String?       = null,
    @SerializedName("responsetime" ) var responsetime : String?       = null,
    @SerializedName("requestId"    ) var requestId    : String?       = null,
    @SerializedName("responsedata" ) var responsedata : CvvResponseData? = CvvResponseData()
)
