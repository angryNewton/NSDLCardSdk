package com.nexxo.nsdlsdk.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class MainObject (
  @field:JsonProperty("channelid")
  var channelid        : String?           = "",
  @field:JsonProperty("signcs")
  var signcs           : String?           = "",
  @field:JsonProperty("token")
  var token            : String?           = "",
  @field:JsonProperty("servicetype")
  var servicetype            : String?           = "",
  @field:JsonProperty("appid")
  var appid            : String?           = "",
  @field:JsonProperty("partnerid")
  var partnerid            : String?           = "",
  @field:JsonProperty("requestid")
  var requestid            : String?           = ""

)