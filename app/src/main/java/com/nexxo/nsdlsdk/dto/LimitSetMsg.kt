package com.nexxo.nsdlsdk.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LimitSetMsg(
    @field:JsonProperty("cardNo")
    var cardNo     : String? = "",
    @field:JsonProperty("atmlimit")
    var atmlimit : String? = "",
    @field:JsonProperty("ecomlimit")
    var ecomlimit  : String? = null,
    @field:JsonProperty("poslimit")
    var poslimit  : String? = null,
    @field:JsonProperty("sessionId")
    var sessionId  : String? = null
)
