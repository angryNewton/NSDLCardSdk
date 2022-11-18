package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class CustomerCVVData(
    @SerializedName("CVV"    ) var CVV    : String? = "",
    @SerializedName("cardNo" ) var cardNo : String? = ""
)