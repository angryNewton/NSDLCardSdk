package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class SetPinResp(
    @SerializedName("response" ) var response : String?       = "",
    @SerializedName("respcode" ) var respcode : String?       = "",
)
