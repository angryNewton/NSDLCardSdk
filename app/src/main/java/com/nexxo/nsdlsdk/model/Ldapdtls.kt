package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class Ldapdtls (

  @SerializedName("response" ) var response : String? = "",
  @SerializedName("respcode" ) var respcode : String? = ""

)