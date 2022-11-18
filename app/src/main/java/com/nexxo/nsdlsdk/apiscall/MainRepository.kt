package com.nexxo.nsdlsdk.apiscall


import okhttp3.RequestBody

class MainRepository constructor(private val retrofitService: RetrofitService) {

    //for card block
    suspend fun getCorporateCardBlock(map: RequestBody) = retrofitService.getCardBlock(map)
    //corporateCard block response
    suspend fun getCorporateblockResponse() = retrofitService.getCardBlockResponse()

    suspend fun getCardUnblock() = retrofitService.getUnblockCard()

    suspend fun generateOtp() = retrofitService.generateCardOtp()

    suspend fun generatePin(map: HashMap<String, Int>) =
        retrofitService.getCorporteCardPin(map)

    suspend fun getCardUpdateLimit(map: RequestBody) =
        retrofitService.getCardUpdateLimit(map)

    //get limits
    suspend fun getCardLimits() = retrofitService.getCardLimits()

    suspend fun getSession(headers: Map<String, String>,map: RequestBody) = retrofitService.getSession(headers,map)

    suspend fun getCardDetails(headers: Map<String, String>,map: RequestBody) = retrofitService.getCardDetails(headers,map)

    suspend fun enableEcom(headers: Map<String, String>,map: RequestBody) = retrofitService.enableEcom(headers,map)

    suspend fun generateTokenForMpin(map: RequestBody) = retrofitService.generateTokenForMpin(map)
    suspend fun verifyMpin(map: RequestBody) = retrofitService.verifyMpin(map)

    suspend fun blockCard(headers: Map<String, String>,map: RequestBody) = retrofitService.blockCard(headers,map)
    suspend fun CVVDETAILS(headers: Map<String, String>,map: RequestBody) = retrofitService.CVVDetails(headers,map)
    suspend fun setPin(headers: Map<String, String>,map: RequestBody) = retrofitService.setPin(headers,map)
    suspend fun updateLimit(headers: Map<String, String>,map: RequestBody) = retrofitService.updateLimit(headers,map)
    suspend fun orderCard(headers: Map<String, String>,map: RequestBody) = retrofitService.orderCard(headers,map)

}
