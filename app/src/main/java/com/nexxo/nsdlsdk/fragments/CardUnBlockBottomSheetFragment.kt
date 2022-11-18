package com.nexxo.nsdlsdk.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nexxo.nsdlsdk.ActivityDashboardCard
import com.nexxo.nsdlsdk.utility.Utility
import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.databinding.CardUnblockCardBinding
import com.nexxo.nsdlsdk.interfaces.iBlockUnblock
import com.nexxo.nsdlsdk.model.CardBlockResponse
import com.nexxo.nsdlsdk.model.ControlItemsDTO
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.SdkConfig
import com.nexxo.nsdlsdk.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class CardUnBlockBottomSheetFragment(context: Context) : BottomSheetDialogFragment() {
    private lateinit var binding: CardUnblockCardBinding
    var cardResonse: ControlItemsDTO = ControlItemsDTO()
    private lateinit var viewModel: DashboardViewModel
    lateinit var isUpdate:iBlockUnblock

    //API Call
    private val retrofitService = RetrofitService.getInstance()
    private val mainRepository = MainRepository(retrofitService)
    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CardUnblockCardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(
            DashboardViewModel::class.java
        )

        inItViews()
        setStyle(STYLE_NO_FRAME, 0)

        viewModel.cardBlockResponse.observe(activity as FragmentActivity) {
            Utility.logData("Card unblock res code ${it.responsecode}")
            binding.progressDialog.visibility = View.GONE
            Constants.iscardBlocked = false
            activity?.let {
                dialog?.dismiss()
            }
           // (activity as ActivityDashboardCard).changeTogel(false)
            isUpdate.changeTogelButton(false)
        }

        return binding.root
    }

    fun updater(isBlicked:iBlockUnblock){
        this.isUpdate = isBlicked
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        try {
//            val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
            bottomSheetDialog.setOnShowListener {
                val bottomSheet = bottomSheetDialog
                    .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

                if (bottomSheet != null) {
                    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                    behavior.isDraggable = true
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            return bottomSheetDialog
        }
        catch (e:Exception)
        {
            Utility.logData("Exception while on create dialog bottom sheet -${e.localizedMessage}")
            return bottomSheetDialog
        }
    }

    private fun inItViews() {
        binding.cancelBtn.setOnClickListener(View.OnClickListener {
            activity?.let {
                dialog?.dismiss()
            }
        })
        binding.crossIv.setOnClickListener(View.OnClickListener {
            activity?.let {
                dialog?.dismiss()
            }
        })
        binding.btnUnblock.setOnClickListener(View.OnClickListener {
            activity?.let {
                // call api for unblock card
                UnblockCard();
            }
        })
    }

    private fun callApi() {
        try {
            showProgressDialogue()
            CoroutineScope(
                Dispatchers.IO + Utility.onErrorCoroutineExceptionHandler(
                    requireActivity()
                )
            ).launch {
                val response = mainRepository.getCardUnblock()
                withContext(Dispatchers.Main) {
                    try {
                        hideProgressDialogue()
                        Utility.printRetrofitResponseBodyData(response)
                        if (response.isSuccessful) {
                            var cardResponse: CardBlockResponse? = response.body()
                            if (cardResponse != null) {
                                //     Utility.callCardInformationApi(requireContext())
                                Utility.showCrossButtonDialogDismiss(
                                    requireContext(),
                                    cardResponse?.message,
                                    true
                                )
                                dialog?.dismiss()
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                if (!activity?.let {
                                        Utility.handleServerSuccessErrorStatusRetrofit(
                                            it,
                                            jObjError.toString(),
                                            response.code()
                                        )
                                    }!!
                                ) {
                                    activity?.let {
                                        Utility.showPopUpForHttpStatus(
                                            it,
                                            response.errorBody()!!.string(),
                                            response.code()
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                activity?.let {
                                    Utility.showAlertPopUpAndFinish(
                                        it,
                                        e.localizedMessage
                                    )
                                }
                            }
                        }
                    } catch (e: java.lang.Exception) {
                    }
                }
            }
        } catch (e: Exception) {
            activity?.let {
                Utility.logData("Card Errror $it")
            }

        }
    }

    private fun showProgressDialogue() {
        binding.progressDialog.visibility = View.VISIBLE
    }

    private fun hideProgressDialogue() {
        binding.progressDialog.visibility = View.GONE

    }

    private fun UnblockCard() {
        var jObject: JSONObject = JSONObject()
        try {

            var mainObj: JSONObject = JSONObject()
            var appIdObject: JSONObject = JSONObject()
            var deviceIdentifireObject: JSONObject = JSONObject()
            appIdObject.put("appid", SdkConfig.appID)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("appregflg", "01")
            appIdObject.put(
                "pushnkey",
                "ehy_bhncSVS0mecz8aWuOI:APA91bGH5hAbvXEWzQxr51bdZUZD8Y5_3hHGmOyUyBb8rJT_HtdhoTi9-W7-dTaSN3sj459IbmHSvMrUUPYJcZh5u6p1PXbUcVI0-9A-6Z_BXQ_VVh9D-vO0vJYa3KS7lZEOAig8rIjg"
            )

            deviceIdentifireObject.put("custunqid", SdkConfig.customerUniqueId)
            deviceIdentifireObject.put("deviceid", SdkConfig.deviceID)
            deviceIdentifireObject.put("deviceunqid", SdkConfig.deviceUniqueID)

            var msgobj = JSONObject()
            msgobj.put("blockType","1")
            msgobj.put("cardNo", Constants.CARDNO)
            msgobj.put("internationalCard","2")
            msgobj.put("sessionId", Constants.SESSIONID)
            msgobj.put("status","0")
            msgobj.put("userremark","NA")

            jObject.put("appdtls", appIdObject)
            jObject.put("deviceidentifier", deviceIdentifireObject)
            jObject.put("channelid", SdkConfig.channelId)
            jObject.put("channelid", SdkConfig.channelId)
            jObject.put("msg",msgobj)
            jObject.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
            jObject.put("token", SdkConfig.token)

            var encryptedData = Utility.encryptpayload(jObject.toString(), Constants.ENCRYPTION_KEY)
            Utility.logData("Decrytped data $jObject")
            Utility.logData("Encrytped data $encryptedData")
            mainObj.put("channelid", SdkConfig.channelId)
            mainObj.put("appid", SdkConfig.appID)
            mainObj.put("partnerid", SdkConfig.partnerId)
            mainObj.put("servicetype", "PBchangeCardStatusME")
            var reqId = Utility.generateSequenceNumber()
            mainObj.put("requestid",reqId )
            mainObj.put("encdata", encryptedData)



            var headers: HashMap<String, String> = HashMap<String,String>()
            headers["Content-Type"] = "application/json"
            headers["channelid"] = SdkConfig.channelId
            headers["partnerid"] = SdkConfig.partnerId
            headers["requestid"] = reqId.toString()
            Constants.REQUESTID = reqId.toString()
            viewModel.blockCard(mainObj.toString(),headers)
            binding.progressDialog.visibility = View.VISIBLE
            Utility.logData("Block card request request "+mainObj.toString())

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}