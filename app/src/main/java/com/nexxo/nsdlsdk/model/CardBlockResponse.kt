package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class CardBlockResponse(
    @SerializedName("cardReferenceNumber") var refrenceId: String?="",
    @SerializedName("responseMessage") var message: String = ""
)