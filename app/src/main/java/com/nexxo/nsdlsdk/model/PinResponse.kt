package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class PinResponse(
    @SerializedName("otp") var otp: String? = "",
    @SerializedName("newPin") var newPin: String? = "",
    @SerializedName("cardReferenceNumber") var cardReferenceNumber: String? = "",
    @SerializedName("responseMessage") var responseMessage: String? = "",
    @SerializedName("requestReferenceNumber") var requestReferenceNumber: String? = ""
)
