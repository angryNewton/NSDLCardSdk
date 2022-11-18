package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class ResponseCardData(
    @SerializedName("CustomerData" ) var CustomerData : ArrayList<CustomerData> = arrayListOf(),
    @SerializedName("response"     ) var response     : String?                 = null,
    @SerializedName("respcode"     ) var respcode     : String?                 = null
)
