package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class CvvResponseData(

    @SerializedName("CustomerData" ) var CustomerData : ArrayList<CustomerCVVData> = arrayListOf(),
    @SerializedName("response"     ) var response     : String?                 = null,
    @SerializedName("respcode"     ) var respcode     : String?                 = null
)
