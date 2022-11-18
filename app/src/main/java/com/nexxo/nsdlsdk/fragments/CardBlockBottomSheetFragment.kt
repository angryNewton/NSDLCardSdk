package com.nexxo.nsdlsdk.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.nexxo.nsdlsdk.ActivityDashboardCard
import com.nexxo.nsdlsdk.R
import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.utility.Utility
import com.nexxo.nsdlsdk.adapter.CardBlockAdatper
import com.nexxo.nsdlsdk.databinding.CardBottomSheetBlockCardBinding
import com.nexxo.nsdlsdk.interfaces.iBlockUnblock
import com.nexxo.nsdlsdk.model.CardBlockDTO
import com.nexxo.nsdlsdk.model.CardBlockReasons
import com.nexxo.nsdlsdk.model.CardBlockResponse
import com.nexxo.nsdlsdk.model.ControlItemsDTO
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.SdkConfig
import com.nexxo.nsdlsdk.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class CardBlockBottomSheetFragment(
    context: Context
) : BottomSheetDialogFragment(), CardBlockAdatper.CallBlockListner {
    private lateinit var binding: CardBottomSheetBlockCardBinding
    private lateinit var adapter: CardBlockAdatper
    private lateinit var cardBlockDTO: CardBlockDTO
    var cardResonse: ControlItemsDTO = ControlItemsDTO()
    var cardBlockResponse: CardBlockReasons? = null
    private lateinit var viewModel: DashboardViewModel
    lateinit var isUpdate:iBlockUnblock

    private var listResponse: List<CardBlockReasons> = arrayListOf()
    var reasonMessage: String? = ""

    //API Call
    private val retrofitService = RetrofitService.getInstance()
    private val mainRepository = MainRepository(retrofitService)

    override fun onStart() {
        super.onStart()
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
        } catch (e: Exception) {
            Utility.logData("Exception while on create dialog bottom sheet -${e.localizedMessage}")
            return bottomSheetDialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CardBottomSheetBlockCardBinding.inflate(inflater, container, false)
        //   getCardBlockResponse()
        viewModel = ViewModelProvider(this).get(
            DashboardViewModel::class.java
        )
        initViewsReclyerview()

        viewModel.cardBlockResponse.observe(activity as FragmentActivity) {
         Utility.logData("Card block res code ${it.responsecode}")
            binding.progressDialog.visibility = View.GONE
            Constants.iscardBlocked = true
            activity?.let {
                dialog?.dismiss()
            }
          //  (activity as ActivityDashboardCard).changeTogel(true)
           // (activity as ActivityDashboardCard).changeTogel(true)
            isUpdate.changeTogelButton(true)
        }
        return binding.root
    }

    fun updater(isBlicked: iBlockUnblock){
        this.isUpdate = isBlicked
    }


    private fun initViewsReclyerview() {

        try {
            adapter = CardBlockAdatper(this)
            binding.rVSelectingCard.adapter = adapter
            cardBlockDTO = Gson().fromJson(
                activity?.let { Utility.loadJSONFromAsset(it, "CorporateCardBlock.json") },
                CardBlockDTO::class.java
            )
            if (cardBlockDTO != null) {
                listResponse = cardBlockDTO.cardBlockReasons
            }
            activity?.let {
                adapter.updateData(requireContext(), listResponse)
            }
        } catch (e: Exception) {
            e.message.let {
                Utility.logData("Exception while initViews - $it")
            }
        }
        binding.crossIv.setOnClickListener(View.OnClickListener {
            activity?.let {
                dialog?.dismiss()
            }
        })
        binding.cancelBtn.setOnClickListener(View.OnClickListener {
            activity?.let {
                dialog?.dismiss()
            }
        })
        binding.btnBlock.setOnClickListener(View.OnClickListener {
            activity?.let {
                // call api for unblock card
                if (validation()) {
                  //  getBlockCardApiCall()
                    blockCard()
                }
            }
        })

    }

    private fun validation(): Boolean {
        if (cardBlockResponse?.reasonId == null) {
            Utility.showMessageInToast(
                requireActivity(),
                getString(R.string.txt_select_reason)
            )
            return false
        } else if (cardBlockResponse?.reasonId == 3) {
            if (binding.etReason.text.toString().isNullOrBlank()) {
                Utility.showMessageInToast(
                    requireActivity(),
                    getString(R.string.enter_reason)
                )
            } else {
               // getBlockCardApiCall()
                blockCard()
            }
            return false
        }
        return true
    }

    private fun showProgressDialogue() {
        binding.progressDialog.visibility = View.VISIBLE
    }

    private fun hideProgressDialogue() {
        binding.progressDialog.visibility = View.GONE

    }

    override fun onClick(position: Int, reason: CardBlockReasons) {
        cardBlockResponse = reason
        if (cardBlockResponse?.reasonId == 3) {
            binding.etReason.visibility = View.VISIBLE
            binding.etReason.text?.clear()
            binding.etReason.addTextChangedListener(textWatcher)

        } else {
            reasonMessage = cardBlockResponse?.reasonMessage
            binding.etReason.visibility = View.GONE
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            reasonMessage = ""
            reasonMessage = s.toString()

        }
    }

    private fun blockCard() {
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
            msgobj.put("cardNo",Constants.CARDNO)
            msgobj.put("internationalCard","2")
            msgobj.put("sessionId",Constants.SESSIONID)
            msgobj.put("status","1")
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