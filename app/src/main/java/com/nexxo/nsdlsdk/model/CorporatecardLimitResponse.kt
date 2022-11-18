package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class CorporatecardLimitResponse(
    @SerializedName("cardReferenceNumber") val cardReferenceNumber: String? = "",
    @SerializedName("dailyCnt") val dailyCnt: String = "",
    @SerializedName("dailyLim") val dailyLim: String = "",
    @SerializedName("pos") val pos: String? = "",
    @SerializedName("eComm") val eComm: String? = "",
    @SerializedName("atm") val atm: String? = "",
    @SerializedName("cashAtPos") val cashAtPos: String? = "",
    @SerializedName("contactLess") val contactLess: String? = "",
    @SerializedName("posLim") val posLim: String = "",
    @SerializedName("eCommLim") val eCommLim: String = "",
    @SerializedName("atmLim") val atmLim: String = "",
    @SerializedName("cashAtPosLim") val cashAtPosLim: String = "",
    @SerializedName("contactLessLim") val contactLessLim: String = "",
    @SerializedName("minLimit") val minLimit: String = "",
    @SerializedName("maxLimit") val maxLimit: String = "",
    @SerializedName("sliderSteps") val sliderSteps: String = "",
    @SerializedName("productSettings") val productSettings: CorporatecardProductSetting = CorporatecardProductSetting()
)

data class CorporatecardProductSetting(
    @SerializedName("domestic")
    val domestic: CorporatecardProductSettingDomestic=CorporatecardProductSettingDomestic()
)

data class CorporatecardProductSettingDomestic(
    @SerializedName("dailyCnt") val dailyCnt: String = "",
    @SerializedName("dailyLim") val dailyLim: String = "",
    @SerializedName("txnToAllow") val txnToAllow: CorporatecardProductSettingDomesticTxnToAllow = CorporatecardProductSettingDomesticTxnToAllow()
)

data class CorporatecardProductSettingDomesticTxnToAllow(
    @SerializedName("pos") val pos: String? = "",
    @SerializedName("eComm") val eComm: String? = "",
    @SerializedName("atm") val atm: String? = "",
    @SerializedName("cashAtPos") val cashAtPos: String? = "",
    @SerializedName("contactLess") val contactLess: String? = ""

)

