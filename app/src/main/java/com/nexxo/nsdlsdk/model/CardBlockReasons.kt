package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class CardBlockReasons(
    @SerializedName("reasonId") var reasonId: Int? = 0,
    @SerializedName("reasonMessage") var reasonMessage: String? = "",
    var isChecked: Boolean? = false

    )