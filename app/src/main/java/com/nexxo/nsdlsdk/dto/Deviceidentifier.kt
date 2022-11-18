package com.nexxo.nsdlsdk.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName


data class Deviceidentifier (
  @field:JsonProperty("custunqid")
  var custunqid   : String? = "",
  @field:JsonProperty("deviceid")
  var deviceid    : String? = "",
  @field:JsonProperty("deviceunqid")
  var deviceunqid : String? = null

)