package com.nexxo.nsdlsdk.dto

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Appdtls (
  @field:JsonProperty("appid")
  var appid       : String? = "",
  @field:JsonProperty("applversion")
  var applversion : String? = "",
  @field:JsonProperty("appregflg")
  var appregflg   : String? = "",
  @field:JsonProperty("pushnkey")
  var pushnkey    : String? = ""

): Parcelable