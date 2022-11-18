package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class TokenDtls (

  @SerializedName("tokenkey"   ) var tokenkey   : String? = "",
  @SerializedName("pwdkey"     ) var pwdkey     : String? = "",
  @SerializedName("expiredate" ) var expiredate : String? = ""

)