package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class CardBlockResponsedata (

  @SerializedName("response" ) var response : String? = "",
  @SerializedName("reqresId" ) var reqresId : String? = "",
  @SerializedName("respcode" ) var respcode : String? = ""

)