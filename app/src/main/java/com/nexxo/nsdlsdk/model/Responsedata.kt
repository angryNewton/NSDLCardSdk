package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class Responsedata (

  @SerializedName("response"  ) var response  : String? = "",
  @SerializedName("respcode"  ) var respcode  : String? = "",
  @SerializedName("SessionId" ) var SessionId : String? = ""

)