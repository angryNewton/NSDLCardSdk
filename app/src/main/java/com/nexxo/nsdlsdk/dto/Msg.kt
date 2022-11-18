package com.nexxo.nsdlsdk.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName


data class Msg (
  @field:JsonProperty("cardNo")
  var cardNo     : String? = "",
  @field:JsonProperty("expiryDate")
  var expiryDate : String? = "",
  @field:JsonProperty("sessionId")
  var sessionId  : String? = null

)