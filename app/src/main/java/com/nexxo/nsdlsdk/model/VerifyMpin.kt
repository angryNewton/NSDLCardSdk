package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class VerifyMpin(
    @SerializedName("messageval" ) var messageval : String?   = "",
    @SerializedName("ldapdtls"   ) var ldapdtls   : Ldapdtls? = Ldapdtls(),
    @SerializedName("tokenkey"   ) var tokenkey   : String?   = "",
    @SerializedName("response"   ) var response   : String?   = "",
    @SerializedName("respcode"   ) var respcode   : String?   = ""
)
