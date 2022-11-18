package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class EcomResponsedata (

  @SerializedName("CustomerData" ) var CustomerData : ArrayList<String> = arrayListOf(),
  @SerializedName("respcode"     ) var respcode     : String?           = "",
  @SerializedName("response"     ) var response     : String?           = ""

)