package com.nexxo.nsdlsdk.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PinSetMsg(

    @field:JsonProperty("cardNo")
    var cardNo     : String? = "",
    @field:JsonProperty("expiry")
    var expiryDate : String? = "",
    @field:JsonProperty("pin")
    var pin  : String? = null,
    @field:JsonProperty("newPin")
    var newPin  : String? = null,
    @field:JsonProperty("sessionId")
    var sessionId  : String? = null
)
