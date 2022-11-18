package com.nexxo.nsdlsdk.interfaces

interface iTokenData {
    fun getTokenData(pwdKey:String,tokenKey:String)
    fun getTokenError(error:String)
}