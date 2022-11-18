package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class LimitResponseDTO(
    @SerializedName("cardReferenceNumber") var cardReferenceNumber: String? = "",
    @SerializedName("responseMsg") var responseMsg: String? = "",
)
