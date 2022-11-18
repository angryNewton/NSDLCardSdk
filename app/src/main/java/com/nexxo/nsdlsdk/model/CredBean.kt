package com.nexxo.nsdlsdk.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CredBean(
    @field:JsonProperty("credid")
    var credId: String = "",
    @field:JsonProperty("credblock")
    var credBlock: String = ""
) : Parcelable