package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class CardBlockDTO(

    @SerializedName("cardReferenceNumber") var cardReferenceNumber: String? = "",
    @SerializedName("responseMessage") var responseMessage: String? = "",
    @SerializedName("cardBlockReasons") var cardBlockReasons: ArrayList<CardBlockReasons> = arrayListOf()

)