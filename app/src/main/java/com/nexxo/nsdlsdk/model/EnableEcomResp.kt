package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class EnableEcomResp (

  @SerializedName("response"     ) var response     : String?       = "",
  @SerializedName("responsecode" ) var responsecode : String?       = "",
  @SerializedName("responsedata" ) var responsedata : EcomResponsedata? = EcomResponsedata(),
  @SerializedName("responsetime" ) var responsetime : String?       = ""

)