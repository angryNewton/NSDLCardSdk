package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class CorporateCardInfo(
    @SerializedName("nameOnCard") var nameOnCard: String? = "",
    @SerializedName("firstName") var firstName: String? = "",
    @SerializedName("lastName") var lastName: String? = "",
    @SerializedName("firstFourDigits") var firstFourDigits: String? = "",
    @SerializedName("lastFourDigits") var lastFourDigits: String? = "",
    @SerializedName("encryptedCardNumber") var encryptedCardNumber: String? = "",
    @SerializedName("cvv") var cvv: String? = "",
    @SerializedName("expiry") var expiry: String? = "",
    @SerializedName("cardReferenceNumber") var cardReferenceNumber: String? = "",
    @SerializedName("cardBalance") var cardBalance: String? = "",
    @SerializedName("cardStatus") var cardStatus: String? = "",
    @SerializedName("cardStatusId") var cardStatusId: String? = "",
    @SerializedName("isDormant") var isDormant: Boolean = false
)
