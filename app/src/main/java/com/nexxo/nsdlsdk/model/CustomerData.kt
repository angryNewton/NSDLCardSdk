package com.nexxo.nsdlsdk.model

import com.google.gson.annotations.SerializedName


data class CustomerData (

  @SerializedName("MobileNo"                 ) var MobileNo                 : String? = "",
  @SerializedName("ExpiryDate"               ) var ExpiryDate               : String? = "",
  @SerializedName("DomesticECOM"             ) var DomesticECOM             : String? = "",
  @SerializedName("Is_PDC"                   ) var IsPDC                    : String? = "",
  @SerializedName("CustomerId"               ) var CustomerId               : String? = "",
  @SerializedName("POSLimit"                 ) var POSLimit                 : Double?    = 0.0,
  @SerializedName("EComLimit"                ) var EComLimit                : Double?    = 0.0,
  @SerializedName("IntStatus"                ) var IntStatus                : String? = "",
  @SerializedName("InternationalECOM"        ) var InternationalECOM        : String? = "",
  @SerializedName("CardStatus"               ) var CardStatus               : String? = "",
  @SerializedName("DomesticPOS"              ) var DomesticPOS              : String? = "",
  @SerializedName("CardNo"                   ) var CardNo                   : String? = "",
  @SerializedName("InternationalPOS"         ) var InternationalPOS         : String? = "",
  @SerializedName("InternationalContactless" ) var InternationalContactless : String? = "",
  @SerializedName("DomesticContactless"      ) var DomesticContactless      : String? = "",
  @SerializedName("Network"                  ) var Network                  : String? = "",
  @SerializedName("HoldRspCode"              ) var HoldRspCode              : Int?    = 0,
  @SerializedName("InternationalATM"         ) var InternationalATM         : String? = "",
  @SerializedName("DomesticATM"              ) var DomesticATM              : String? = "",
  @SerializedName("CustomerName"             ) var CustomerName             : String? = "",
  @SerializedName("ATMLimit"                 ) var ATMLimit                 : Double?    = 0.0

)