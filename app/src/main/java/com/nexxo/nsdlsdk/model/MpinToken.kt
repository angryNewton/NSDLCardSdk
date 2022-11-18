package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class MpinToken(
    @SerializedName("response"   ) var response  : String?    = "",
    @SerializedName("token_dtls" ) var tokenDtls : TokenDtls? = TokenDtls(),
    @SerializedName("respcode"   ) var respcode  : String?    = ""
)
