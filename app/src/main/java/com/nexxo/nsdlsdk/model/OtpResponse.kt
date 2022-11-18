package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class OtpResponse(
    @SerializedName("cardReferenceNumber") var cardReferenceNumber: String? = "",
    @SerializedName("uid") var uid: String? = "",
    @SerializedName("responseMessage") var responseMessage: String? = "",

    )
