package com.nexxo.nsdlsdk.utility

import com.nexxo.nsdlsdk.model.CustomerData

class Constants {
    companion object{
        val MAIN_URL:String = "https://jiffyuat.nsdlbank.co.in"
        const val GET_CORPORATE_CARD_BLOCK = "wps/corporateCard/block"
        const val GET_CORPORATE_CARD_BLOCK_RESPONSE_URL = "wps/corporateCard/block/reasons"
        const val GET_CARD_UNBLOCK = "wps/corporateCard/unblock"

        const val httpStatus_WS = "httpStatus"
        const val message_WS = "message"
        const val status_WS = "status"
        const val reason_WS = "reason"
        const val type_WS = "type"
        const val statusMessage_WS = "statusMessage"
        const val exp_WS = "exp"
        var OTP_RESEND_DownTimer = 120000
        var OTP_CountDownInterval = 1000
        var isFrom = "isFrom" //1 from Dashboard my account
        var generate_OTP_WS = "otp"
        var generate_NEWPIN_WS = "newPin"
        const val errors_WS = "errors"
        const val SPLASH_SET_PIN: Long = 600
        var responseMessage_WS = "responseMessage"
        var cardReferenceNumber_WS = "cardReferenceNumber"
        var requestReferenceNumber_WS = "requestReferenceNumber"


        const val GET_CORPORATE_GENERATE_OTP = "wps/corporateCard/trigger/otp"
        const val GET_CORPORATE_GENERATE_PIN = "wps/corporateCard/pin/reset"
        const val GET_CARD_LIMITS = "wps/corporateCard/limits"
        const val GET_CORPORATE_CARD_LIMITS = "wps/corporateCard/limits"
        const val GET_TOKEN = "/v1/jarvisfinserv/PBcardserviceME"
        const val GET_CARDDETAILS = "/v1/jarvisfinserv/PBcardserviceME"
        const val SET_TRXN_TYPES = "/v1/jarvisfinserv/PBcardInternationalStatusME"
        const val MPIN_TOKEN = "/v1/jarvisfinserv/PBuserAUTHtokenME"
        const val MPIN_VALIDATION = "/v1/jarvisfinserv/PBuserAUTHvalidateME"
        const val BLOCKCARD = "/v1/jarvisfinserv/PBchangeCardStatusME"
        const val GET_CVV_DETAILS = "/v1/jarvisfinserv/PBgetCVVdetailsME"
        const val ORDER_CARD = "v1/jarvisfinserv/PBconverttoPhysDCME"
        const val TOKEN = ""
        const val ENCRYPTION_KEY = "WnZr4u7x!A%D*G-KaPdRgUkXp2s5v8y/"
      //  const val CHANNELID = "WnZr4u7x!A%D*G-KaPdRgUkXp2s5v8y/"
        const val PARTNERID = "WnZr4u7x!A%D*G-KaPdRgUkXp2s5v8y/"
        var REQUESTID = "WnZr4u7x!A%D*G-KaPdRgUkXp2s5v8y/"
        var SESSIONID = ""
        var CARDNO = ""
        var CARD_EXPIRY = ""
        var CARD_EXPIRY_TV = ""
        var USER_TYPE = "CUSTOMER"
        var ROLE = "CUST"
        var SIGNCS = "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
        var APPREGFLG = "01"
        var PUSHNKEY = "ehy_bhncSVS0mecz8aWuOI:APA91bGH5hAbvXEWzQxr51bdZUZD8Y5_3hHGmOyUyBb8rJT_HtdhoTi9-W7-dTaSN3sj459IbmHSvMrUUPYJcZh5u6p1PXbUcVI0-9A-6Z_BXQ_VVh9D-vO0vJYa3KS7lZEOAig8rIjg"
        var customerData:CustomerData?= null
        var iscardBlocked: Boolean= false
        var MAX_POS_LIMIT: Double= 50000.0
        var MAX_ECOM_LIMIT: Double= 25000.0
        var MAX_ATM_LIMIT: Double= 20000.0



    }

}