package com.nexxo.nsdlsdk

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nexxo.nsdlsdk.adapter.LimitCardAdapter

import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.databinding.CardLayoutBinding
import com.nexxo.nsdlsdk.databinding.CardLimitBinding
import com.nexxo.nsdlsdk.model.CorporatecardLimitResponse
import com.nexxo.nsdlsdk.model.Corporatecardlistresponse
import com.nexxo.nsdlsdk.model.LimitResponseDTO
import com.nexxo.nsdlsdk.utility.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class CardLimitationActivity : AppCompatActivity() {
        lateinit var binding: CardLimitBinding
    var dashboardInfo: CorporatecardLimitResponse = CorporatecardLimitResponse()
    private val adapter = LimitCardAdapter()
    private var listResponse: List<Corporatecardlistresponse> = arrayListOf()
    private var isLimitChanged: Boolean? = false

    //api call
    private val retrofitService = RetrofitService.getInstance()
    private val mainRepository = MainRepository(retrofitService)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CardLimitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initXml()
    }

    private fun callApi() {
        try {
            showProgressDialog()
            CoroutineScope(
                Dispatchers.IO + Utility.onErrorCoroutineExceptionHandler(
                    this
                )
            ).launch {
                val response = mainRepository.getCardLimits()
                withContext(Dispatchers.Main) {
                    try {
                        dismissProgressDialog()
                        Utility.printRetrofitResponseBodyData(response)
                        if (response.isSuccessful) {
                            dashboardInfo = response.body()!!
                            if (dashboardInfo != null) {
                                binding.rVLimitTransaction.adapter = adapter
                                this@CardLimitationActivity.let {
                                    adapter.updateData(it, listResponse, dashboardInfo)
                                }
                            } else {
                                binding.noTransactionsTitleTv.visibility = View.VISIBLE
                                binding.rVLimitTransaction.visibility = View.GONE
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                if (!Utility.handleServerSuccessErrorStatusRetrofit(
                                        this@CardLimitationActivity,
                                        jObjError.toString(),
                                        response.code()
                                    )
                                ) {
                                    this@CardLimitationActivity.let {
                                        Utility.showPopUpForHttpStatus(
                                            it,
                                            response.errorBody()!!.string(),
                                            response.code()
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                this@CardLimitationActivity.let {
                                    Utility.showAlertPopUpAndFinish(
                                        it,
                                        e.localizedMessage
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            Utility.logData(it)
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.message?.let { Utility.logData(it) }
        }
    }

    private fun initXml() {
        binding?.toolbar?.tvHeaderText!!.text = resources.getString(R.string.txt_card_controls)
        binding?.toolbar?.llBackArrow!!.setOnClickListener(::onBackClicked)
        binding.rVLimitTransaction.adapter = adapter
        try {
            val json =
                Utility.loadJSONFromAsset(
                    this@CardLimitationActivity,
                    "CardLimitation.json"
                ) // your json value here
            val gson = Gson()
            val itemType = object : TypeToken<List<Corporatecardlistresponse>>() {}.type
            listResponse = gson.fromJson<List<Corporatecardlistresponse>>(json, itemType)
         //   callApi()

        } catch (e: Exception) {
            e.message?.let { Utility.logData("Exception while initViews - $it") }
        }

        binding.btnSetLimit.setOnClickListener(::updateLimit)
    }


    private fun onBackClicked(view: View?) {

            finish()
    }

    private fun showProgressDialog() {
        binding.progressDialog.visibility = View.VISIBLE
    }

    private fun dismissProgressDialog() {
        binding.progressDialog.visibility = View.GONE
    }

    fun updateLimit(view: View?) {
        /*{
"dailyCnt": 10,
"dailyLim": 1500.0,
"pos": "Y",
"eComm": "Y",
"atm": "Y",
"cashAtPos": "Y",
"contactLess": "N",
"posLim": 1600,
"eCommLim": 1500.0,
"atmLim": 1500.0,
"cashAtPosLim": 1500.0,
"contactLessLim": 0
}*/

            val jsonObject = JSONObject()
            for (i in listResponse.indices) {
                if (listResponse[0].isEditable) {
                    isLimitChanged = true
                    jsonObject.put("atmLim", listResponse.get(0).isSeekValueChanged)
                } else {
                    jsonObject.put("atmLim", dashboardInfo?.atmLim)
                }
                if (listResponse[1].isEditable) {
                    isLimitChanged = true
                    jsonObject.put("eCommLim", listResponse.get(1).isSeekValueChanged)

                } else {
                    jsonObject.put("eCommLim", dashboardInfo?.eCommLim)
                }
                if (listResponse[2].isEditable) {
                    isLimitChanged = true
                    jsonObject.put("posLim", listResponse.get(2).isSeekValueChanged)

                } else {
                    jsonObject.put("posLim", dashboardInfo?.posLim)
                }
                if (listResponse[3].isEditable) {
                    isLimitChanged = true
                    jsonObject.put("contactLessLim", listResponse.get(3).isSeekValueChanged)

                } else {
                    jsonObject.put("contactLessLim", dashboardInfo?.contactLessLim)
                }
                if (listResponse[4].isEditable) {
                    isLimitChanged = true
                    jsonObject.put("cashAtPosLim", listResponse.get(4).isSeekValueChanged)

                } else {
                    jsonObject.put("cashAtPosLim", dashboardInfo?.cashAtPosLim)
                }
                if (listResponse[0].isValueChanged) {

                    jsonObject.put("atm", "Y")
                } else {
                    jsonObject.put("atm", "N")
                }
                if (listResponse[1].isValueChanged) {

                    jsonObject.put("eComm", "Y")
                } else {
                    jsonObject.put("eComm", "N")
                }
                if (listResponse[2].isValueChanged) {

                    jsonObject.put("pos", "Y")
                } else {
                    jsonObject.put("pos", "N")
                }
                if (listResponse[3].isValueChanged) {

                    jsonObject.put("contactLess", "Y")
                } else {
                    jsonObject.put("contactLess", "N")
                }
                if (listResponse[4].isValueChanged) {
                    jsonObject.put("cashAtPos", "Y")
                } else {
                    jsonObject.put("cashAtPos", "N")
                }
            }
            jsonObject.put("dailyCnt", dashboardInfo?.dailyCnt.toString())
            jsonObject.put("dailyLim", dashboardInfo?.dailyLim.toString())
            val jsonObjectString = jsonObject.toString()
            Utility.logData("jsonObjectString Request $jsonObjectString")
            val requestBody =
                jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
            if (isLimitChanged == true) {
                callUpdateLimit(requestBody)
            } else {
                Utility.showMessageInToast(
                    this@CardLimitationActivity,
                    getString(R.string.txt_update_limit)
                )
            }

    }

    private fun callUpdateLimit(jsonObject: RequestBody) {
        try {
            showProgressDialog()
            CoroutineScope(
                Dispatchers.IO + Utility.onErrorCoroutineExceptionHandler(
                    this
                )
            ).launch {

                val response = mainRepository.getCardUpdateLimit(jsonObject)
                withContext(Dispatchers.Main) {
                    try {
                        Utility.printRetrofitResponseBodyData(response)
                        if (response.isSuccessful) {
                            val limitResponse: LimitResponseDTO? = response.body()
                            if (limitResponse != null) {
                                Utility.showMessageInToast(
                                    this@CardLimitationActivity,
                                    limitResponse.responseMsg!!
                                )
                                finish()
                            } else {

                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                if (!Utility.handleServerSuccessErrorStatusRetrofit(
                                        this@CardLimitationActivity,
                                        jObjError.toString(),
                                        response.code()
                                    )
                                ) {
                                    this@CardLimitationActivity.let {
                                        Utility.showPopUpForHttpStatus(
                                            it,
                                            response.errorBody()!!.string(),
                                            response.code()
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                this@CardLimitationActivity.let {
                                    Utility.showAlertPopUpAndFinish(
                                        it,
                                        e.localizedMessage
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            Utility.logData(it)
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.message?.let { Utility.logData(it) }
        }
    }


}