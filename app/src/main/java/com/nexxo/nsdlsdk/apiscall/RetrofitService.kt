package com.nexxo.nsdlsdk.apiscall

import com.nexxo.nsdlsdk.model.*
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.Constants.Companion.PARTNERID
import com.nexxo.nsdlsdk.utility.Constants.Companion.REQUESTID
import com.nexxo.nsdlsdk.utility.SdkConfig
import com.nexxo.nsdlsdk.utility.SdkConfig.Companion.channelId
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import java.util.concurrent.TimeUnit
import retrofit2.http.Header as Header1

interface RetrofitService {


    @PUT(Constants.GET_CORPORATE_CARD_BLOCK)
    suspend fun getCardBlock(
        @Body map: RequestBody
    ): Response<CardBlockResponse>


    @GET(Constants.GET_CORPORATE_CARD_BLOCK_RESPONSE_URL)
    suspend fun getCardBlockResponse(
    ): Response<CardBlockDTO>

    @PUT(Constants.GET_CARD_UNBLOCK)
    suspend fun getUnblockCard(): Response<CardBlockResponse>

    @POST(Constants.GET_CORPORATE_GENERATE_OTP)
    suspend fun generateCardOtp(
    ): Response<OtpResponse>


    @POST(Constants.GET_CORPORATE_GENERATE_PIN)
    suspend fun getCorporteCardPin(
        @Body map: HashMap<String, Int>
    ): Response<PinResponse>

    @PUT(Constants.GET_CARD_LIMITS)
    suspend fun getCardUpdateLimit(
        @Body map: RequestBody
    ): Response<LimitResponseDTO>

    @GET(Constants.GET_CARD_LIMITS)
    suspend fun getCardLimits(
    ): Response<CorporatecardLimitResponse>


    /*@Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "channelid: $CHANNELID",
        "partnerid: $PARTNERID",
        "requestid: $REQUESTID")*/
    @POST(Constants.GET_TOKEN)
    suspend fun getSession(
        @HeaderMap headers: Map<String, String>,
        @Body map: RequestBody): Response<SessionModel>

    @POST(Constants.GET_CARDDETAILS)
    suspend fun getCardDetails(
        @HeaderMap headers: Map<String, String>,
        @Body map: RequestBody): Response<CardDetails>

    @POST(Constants.GET_CARDDETAILS)
    suspend fun enableEcom(
        @HeaderMap headers: Map<String, String>,
        @Body map: RequestBody): Response<EnableEcomResp>

    @POST(Constants.MPIN_TOKEN)
    suspend fun generateTokenForMpin(
        @Body map: RequestBody): Response<MpinToken>

    @POST(Constants.MPIN_VALIDATION)
    suspend fun verifyMpin(
        @Body map: RequestBody): Response<VerifyMpin>

    @POST(Constants.GET_CARDDETAILS)
    suspend fun blockCard(
        @HeaderMap headers: Map<String, String>,
        @Body map: RequestBody): Response<CardBlockResp>

    @POST(Constants.GET_CARDDETAILS)
    suspend fun CVVDetails(
        @HeaderMap headers: Map<String, String>,
        @Body map: RequestBody): Response<CVV>

    @POST(Constants.GET_CARDDETAILS)
    suspend fun setPin(
        @HeaderMap headers: Map<String, String>,
        @Body map: RequestBody): Response<SetPinResp>

    @POST(Constants.GET_CARDDETAILS)
    suspend fun updateLimit(
        @HeaderMap headers: Map<String, String>,
        @Body map: RequestBody): Response<SessionModel>

    @POST(Constants.ORDER_CARD)
    suspend fun orderCard(
        @HeaderMap headers: Map<String, String>,
        @Body map: RequestBody): Response<CardBlockResp>



    companion object {
        private lateinit var interceptor: HttpLoggingInterceptor
        private lateinit var okHttpClient: OkHttpClient
        private var retrofitService: RetrofitService? = null

        fun getInstance(): RetrofitService {
            interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            interceptor.level = HttpLoggingInterceptor.Level.HEADERS
            okHttpClient = OkHttpClient.Builder()
//                .addInterceptor(RetrofitService.interceptor)
                .addInterceptor(interceptor.setLevel(interceptor.level))
                /*.connectionSpecs(Arrays.asList(
                        ConnectionSpec.MODERN_TLS,
                        ConnectionSpec.COMPATIBLE_TLS))*/
                .connectionSpecs(
                    Arrays.asList(
                        ConnectionSpec.MODERN_TLS,
                        ConnectionSpec.CLEARTEXT,
                        ConnectionSpec.COMPATIBLE_TLS
                    )
                )
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .cache(null)
                .apply {
                    addInterceptor(
                        Interceptor { chain ->
                            val builder = chain.request().newBuilder()
                            builder.header("Accept-Language", "en")
                            builder.header("User-Agent", "agent")
                            builder.header("X-Channel", "Android")
                            builder.header("Content-Type", "application/json")

                            return@Interceptor chain.proceed(builder.build())
                        }
                    )
                }.build()
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.MAIN_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }


}