package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName

data class Corporatecardlistresponse(
    @SerializedName("id") val id: Int=0,
    @SerializedName("name") val name: String="",
    @SerializedName("terminalId") val terminalId: String="",
    @SerializedName("isSelected") val isSelected: Boolean=false,
    @SerializedName("isConfirmed") val isConfirmed: Boolean=false,
    var isSeekValueChanged: String = "",
    var isEditable: Boolean = false,
    var isCardLimtShow: Boolean = false,
    var isValueChanged: Boolean = false
)
