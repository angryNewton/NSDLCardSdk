package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class SessionModel(
    @SerializedName("responsecode" ) var responsecode : String?       = "",
    @SerializedName("response"     ) var response     : String?       = "",
    @SerializedName("responsetime" ) var responsetime : String?       = "",
    @SerializedName("requestId"    ) var requestId    : String?       = "",
    @SerializedName("responsedata" ) var responsedata : Responsedata? = Responsedata()
)
