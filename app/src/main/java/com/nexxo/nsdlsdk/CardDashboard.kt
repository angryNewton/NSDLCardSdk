package com.nexxo.nsdlsdk

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.databinding.CardBackBinding
import com.nexxo.nsdlsdk.databinding.CardFrontBinding
import com.nexxo.nsdlsdk.databinding.CardLayoutBinding
import com.nexxo.nsdlsdk.dto.Appdtls
import com.nexxo.nsdlsdk.dto.Deviceidentifier
import com.nexxo.nsdlsdk.dto.MainObject
import com.nexxo.nsdlsdk.dto.Msg
import com.nexxo.nsdlsdk.fragments.CardUnBlockBottomSheetFragment
import com.nexxo.nsdlsdk.utility.Utility
import com.nexxo.nsdlsdk.fragments.CardBlockBottomSheetFragment
import com.nexxo.nsdlsdk.fragments.MpinFragment
import com.nexxo.nsdlsdk.interfaces.iBlockUnblock
import com.nexxo.nsdlsdk.interfaces.iCvvCommunitor
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.SdkConfig
import com.nexxo.nsdlsdk.viewmodel.DashboardViewModel
import org.json.JSONObject

class CardDashboard : Fragment() {
    lateinit var binding: CardLayoutBinding
    private lateinit var cardFrontBinding: CardFrontBinding
    private lateinit var cardBackBinding: CardBackBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var activity: Activity
    private var mIsBackVisible = false
    private var mSetLeftCardIn: AnimatorSet? = null

    private var mSetRightOut: AnimatorSet? = null
    private var mSetLeftIn: AnimatorSet? = null
    private var mSetLeftOut: AnimatorSet? = null
    private var mSetRightCardOut: AnimatorSet? = null
    private var mSetRightCardIn: AnimatorSet? = null
    private var mSetLeftCardOut: AnimatorSet? = null
    private var mCardBackLayout: View? = null
    private var mCardFrontLayout: View? = null
    private var isCardBlocked = false
    lateinit var mpin:MpinFragment


    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CardLayoutBinding.inflate(layoutInflater)
        DataBindingUtil.setContentView(binding.root)
        activity = getActivity()!!
        context = applicationContext
        mCardBackLayout = binding.creditCardBack
        mCardFrontLayout = binding.creditCardFront

        binding.fmCard.setOnClickListener {
                flipCard(it)
        }

        binding.cardControlsIv.setOnClickListener {
            try {
                val activity = Intent(activity, CardLimitationActivity::class.java)
                startActivity(activity)
            } catch (e: Exception) {
                e.message?.let { it1 -> Utility.logData(it1) }
            }
        }


        binding.cardBlockIv.setOnClickListener {
            try {
                Utility.logData("Card Block - $isCardBlocked clicked")
                if (!isCardBlocked) {
                    val bottomSheetblockFragment = CardBlockBottomSheetFragment(activity)
                    bottomSheetblockFragment.show(
                        supportFragmentManager,
                        bottomSheetblockFragment.tag
                    )
                } else {
                    val bottomSheetUnblockFragment =
                        CardUnBlockBottomSheetFragment(activity)
                    bottomSheetUnblockFragment.show(
                        supportFragmentManager,
                        bottomSheetUnblockFragment.tag
                    )
                }
            } catch (e: Exception) {
                e.message?.let { it1 -> Utility.logData(it1) }
            }
        }

        binding.resetPinIv.setOnClickListener {
            try {
                val activity = Intent(activity, GenerateOtp::class.java)
                startActivity(activity)
            } catch (e: Exception) {
                e.message?.let { it1 -> Utility.logData(it1) }
            }
        }


    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CardLayoutBinding.inflate(layoutInflater)

        activity = requireActivity()
        mCardBackLayout = binding.creditCardBack
        mCardFrontLayout = binding.creditCardFront
        cardFrontBinding = binding.layoutCardFront
        cardBackBinding = binding.layoutCardBack
        mpin = MpinFragment(activity)
        mpin.updater(updateCvv)
        binding.fmCard.setOnClickListener {
            flipCard(it)
        }

        binding.cardControlsIv.setOnClickListener {
            try {
                val activity = Intent(activity, CardLimitActivity::class.java)
                startActivity(activity)
            } catch (e: Exception) {
                e.message?.let { it1 -> Utility.logData(it1) }
            }
        }


        binding.cardBlockIv.setOnClickListener {
            try {
                Utility.logData("Card Block - $isCardBlocked clicked")
                if (!Constants.iscardBlocked) {
                    val bottomSheetblockFragment = CardBlockBottomSheetFragment(activity)
                    bottomSheetblockFragment.updater(isblocked)
                    bottomSheetblockFragment.show(
                        childFragmentManager,
                        bottomSheetblockFragment.tag
                    )
                } else {
                    val bottomSheetUnblockFragment =
                        CardUnBlockBottomSheetFragment(activity)
                    bottomSheetUnblockFragment.updater(isblocked)
                    bottomSheetUnblockFragment.updater(isblocked)
                    bottomSheetUnblockFragment.show(
                        childFragmentManager,
                        bottomSheetUnblockFragment.tag
                    )
                }
            } catch (e: Exception) {
                e.message?.let { it1 -> Utility.logData(it1) }
            }
        }

        binding.resetPinIv.setOnClickListener {
            try {
                val activity = Intent(activity, SetPin::class.java)
                startActivity(activity)
            } catch (e: Exception) {
                e.message?.let { it1 -> Utility.logData(it1) }
            }
        }

        viewModel = ViewModelProvider(this).get(
            DashboardViewModel::class.java
        )


        viewModel.token.observe(activity as FragmentActivity) {
            Constants.SESSIONID = it.responsedata!!.SessionId.toString()
            Utility.logData("Session ID *** "+ Constants.SESSIONID)
            createCardDetailRequest()
        }

        viewModel.ecomEnale.observe(activity as FragmentActivity) {
            binding.progressDialog.visibility = View.GONE
        }

        viewModel.verifyMpinResponse.observe(activity as FragmentActivity) {
            Utility.logData("Session ID *** "+it.messageval.toString())

        }

        viewModel.CVVDATA.observe(activity as FragmentActivity) {
            Utility.logData("Session ID *** "+it.response.toString())
            binding.progressDialog.visibility = View.GONE
           // mpin.closeDialog()
            mpin.resetPin()
            cardBackBinding.scShowNumber.isChecked = true
            cardBackBinding.tvExpiryDate.text = Constants.CARD_EXPIRY_TV
            if (it.responsedata!!.CustomerData!=null && it.responsedata!!.CustomerData.size>0){
                binding.layoutCardBack.tvSecurityDate.text = it.responsedata!!.CustomerData[0].CVV
                it.responsedata!!.CustomerData[0].cardNo?.let { it1 -> setCardNumber(it1,false) }
                //  binding.layoutCardBack.tvSecurityDate.text = it.responsedata!!.CustomerData[0].CVV

            }

        }

        viewModel.cardDetail.observe(activity as FragmentActivity) {
            binding.progressDialog.visibility = View.GONE
            Constants.customerData = it.responsedata!!.CustomerData[0]
            Constants.SESSION_ACTIVE = true
            if (it.responsedata != null) {
                var custname = it.responsedata!!.CustomerData[0].CustomerName.toString()
                Utility.logData("Customer name *** $custname")

                if (it.responsedata!!.CustomerData[0].HoldRspCode == 5){
                    Constants.iscardBlocked = true
                    binding.cardBlockIv.setImageDrawable(activity?.let {
                        ContextCompat.getDrawable(
                            it, R.drawable.ic_card_block
                        )
                    })
                }else{
                    Constants.iscardBlocked = false
                    binding.cardBlockIv.setImageDrawable(activity?.let {
                        ContextCompat.getDrawable(
                            it, R.drawable.ic_card_unblocked
                        )
                    })
                }


                var nameData = custname.split(" ")
                cardFrontBinding.tvCardFirstName.text = nameData[0]
                if (nameData[1]!=null){
                    cardFrontBinding.tvCardLastName.text = nameData[1]
                }

                var cardNumber: String? = it.responsedata!!.CustomerData[0].CardNo
                // var exp: String? = it.responsedata!!.CustomerData[0].ExpiryDate!!.split(1)

                val result1 = it.responsedata!!.CustomerData[0].ExpiryDate!!.dropLast(2) // returns abcdefghijklmnopqrstuvwxyzabcdefghi
                val result2 = it.responsedata!!.CustomerData[0].ExpiryDate!!.drop(2)

                Constants.CARDNO = cardNumber.toString()
                Constants.CARD_EXPIRY ="$result1/$result2"
                Constants.CARD_EXPIRY_TV ="$result2/$result1"
                if (it.responsedata!!.CustomerData[0].DomesticECOM.equals("N")){
                    enableEcomRequest()
                }
                setCardNumber(Constants.CARDNO,true)

                if (!it.responsedata!!.CustomerData[0].IsPDC.equals("Y")){
                    orderCard()
                }


            }
        }

        cardBackBinding.scShowNumber.setOnClickListener {

            if (cardBackBinding.scShowNumber.isChecked){
                mpin.show(
                    childFragmentManager,
                    mpin.tag
                )
                cardBackBinding.scShowNumber.isChecked = false
            }else{
                binding.layoutCardBack.tvSecurityDate.text = "***"
                cardBackBinding.tvExpiryDate.text = "XX/XX"
                setCardNumber(Constants.CARDNO,true)
            }
            Utility.logData("ischecked$$$$$$$$$$$$$$$$$ "+cardBackBinding.scShowNumber.isChecked)

        }


        cardBackBinding.scShowNumber.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {



            }else{
                /* binding.layoutCardBack.tvSecurityDate.text = "***"
                 setCardNumber(Constants.CARDNO,true)*/
            }
        }


        return binding.root
    }

    var isblocked:iBlockUnblock = object : iBlockUnblock {
        override fun changeTogelButton(isblocked: Boolean) {
            changeTogel(isblocked)
        }
    }

    var updateCvv:iCvvCommunitor = object : iCvvCommunitor {
        override fun fetchCvv() {
            callFetchCvv()
        }
    }




    private fun setCardNumber(cardNumber: String,isMasked:Boolean){
        if (cardNumber != null) {
            for (i in cardNumber) {
                cardBackBinding.tvCardFirst1Name.text = cardNumber[0].toString()
                cardBackBinding.tvCardSecond1Name.text = cardNumber[1].toString()
                cardBackBinding.tvCardThird1Name.text = cardNumber[2].toString()
                cardBackBinding.tvCardFourth1Name.text = cardNumber[3].toString()

                /**/

                if (isMasked){
                    cardBackBinding.tvCardFirst2Name.text = "x"
                    cardBackBinding.tvCardSecond2Name.text = "x"
                    cardBackBinding.tvCardThird2Name.text = "x"
                    cardBackBinding.tvCardForth2Name.text = "x"
                    cardBackBinding.tvCardFirst3Name.text = "x"
                    cardBackBinding.tvCardSecond3Name.text = "x"
                    cardBackBinding.tvCardThird3Name.text = "x"
                    cardBackBinding.tvCardFourth3Name.text = "x"
                }else{
                    cardBackBinding.tvCardFirst2Name.text = cardNumber[4].toString()
                    cardBackBinding.tvCardSecond2Name.text = cardNumber[5].toString()
                    cardBackBinding.tvCardThird2Name.text = cardNumber[6].toString()
                    cardBackBinding.tvCardForth2Name.text = cardNumber[7].toString()
                    cardBackBinding.tvCardFirst3Name.text = cardNumber[8].toString()
                    cardBackBinding.tvCardSecond3Name.text = cardNumber[9].toString()
                    cardBackBinding.tvCardThird3Name.text = cardNumber[10].toString()
                    cardBackBinding.tvCardFourth3Name.text = cardNumber[11].toString()
                }


                cardBackBinding.tvCardFirst4Name.text = cardNumber[12].toString()
                cardBackBinding.tvCardSecond4Name.text = cardNumber[13].toString()
                cardBackBinding.tvCardThird4Name.text = cardNumber[14].toString()
                cardBackBinding.tvCardFourth4Name.text = cardNumber[15].toString()
            }
        }
    }

    private fun createSessionRequest() {
        var jObject: JSONObject = JSONObject()
        try {

            var mainObj: JSONObject = JSONObject()
            var appIdObject: JSONObject = JSONObject()
            var deviceIdentifireObject: JSONObject = JSONObject()
            appIdObject.put("appid", SdkConfig.appID)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("appregflg", "01")
            appIdObject.put(
                "pushnkey",
                "ehy_bhncSVS0mecz8aWuOI:APA91bGH5hAbvXEWzQxr51bdZUZD8Y5_3hHGmOyUyBb8rJT_HtdhoTi9-W7-dTaSN3sj459IbmHSvMrUUPYJcZh5u6p1PXbUcVI0-9A-6Z_BXQ_VVh9D-vO0vJYa3KS7lZEOAig8rIjg"
            )

            deviceIdentifireObject.put("custunqid", SdkConfig.customerUniqueId)
            deviceIdentifireObject.put("deviceid", SdkConfig.deviceID)
            deviceIdentifireObject.put("deviceunqid", SdkConfig.deviceUniqueID)

            jObject.put("appdtls", appIdObject)
            jObject.put("deviceidentifier", deviceIdentifireObject)
            jObject.put("channelid", SdkConfig.channelId)
            jObject.put("channelid", SdkConfig.channelId)
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
            mainObj.put("servicetype", "PBgetSessionME")
            var reqId = Utility.generateSequenceNumber()
            mainObj.put("requestid",reqId )
            mainObj.put("encdata", encryptedData)
            var headers: HashMap<String, String> = HashMap<String,String>()
            headers["Content-Type"] = "application/json"
            headers["channelid"] = SdkConfig.channelId
            headers["partnerid"] = SdkConfig.partnerId
            headers["requestid"] = reqId.toString()
            Constants.REQUESTID = reqId.toString()
            viewModel.getNewSession(mainObj.toString(),headers)
            binding.progressDialog.visibility = View.VISIBLE
            Utility.logData("Session request "+mainObj.toString())


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun enableEcomRequest() {
        var jObject: JSONObject = JSONObject()
        try {

            var mainObj: JSONObject = JSONObject()
            var appIdObject: JSONObject = JSONObject()
            var deviceIdentifireObject: JSONObject = JSONObject()
            appIdObject.put("appid", SdkConfig.appID)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("appregflg", "01")
            appIdObject.put(
                "pushnkey",
                "ehy_bhncSVS0mecz8aWuOI:APA91bGH5hAbvXEWzQxr51bdZUZD8Y5_3hHGmOyUyBb8rJT_HtdhoTi9-W7-dTaSN3sj459IbmHSvMrUUPYJcZh5u6p1PXbUcVI0-9A-6Z_BXQ_VVh9D-vO0vJYa3KS7lZEOAig8rIjg"
            )

            deviceIdentifireObject.put("custunqid", SdkConfig.customerUniqueId)
            deviceIdentifireObject.put("deviceid", SdkConfig.deviceID)
            deviceIdentifireObject.put("deviceunqid", SdkConfig.deviceUniqueID)

            mainObj.put("appdtls", appIdObject)
            mainObj.put("deviceidentifier", deviceIdentifireObject)
            mainObj.put("channelid", SdkConfig.channelId)
            mainObj.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
            jObject.put("token", SdkConfig.token)



            var parentObj = JSONObject()

            parentObj.put("channelid", SdkConfig.channelId)
            parentObj.put("appid", SdkConfig.appID)
            parentObj.put("partnerid", SdkConfig.partnerId)
            parentObj.put("servicetype", "PBcardInternationalStatusME")
            var reqId = Utility.generateSequenceNumber()
            mainObj.put("requestid",reqId )
            mainObj.put("atm","Y" )
            mainObj.put("contactless","N" )
            mainObj.put("ecom","Y" )
            mainObj.put("pos","Y" )
            mainObj.put("cardNo",Constants.CARDNO )
            mainObj.put("type","D" )

            var encryptedData = Utility.encryptpayload(mainObj.toString(), Constants.ENCRYPTION_KEY)
            Utility.logData("Decrytped data ecom emable $mainObj")
            Utility.logData("Encrytped data $encryptedData")


            parentObj.put("encdata", encryptedData)
            parentObj.put("encdata", encryptedData)
            // mainObj.put("encdata", encryptedData)
            var headers: HashMap<String, String> = HashMap<String,String>()
            headers["Content-Type"] = "application/json"
            headers["channelid"] = SdkConfig.channelId
            headers["partnerid"] = SdkConfig.partnerId
            headers["requestid"] = reqId.toString()
            Constants.REQUESTID = reqId.toString()
            viewModel.enableEcom(parentObj.toString(),headers)
            binding.progressDialog.visibility = View.VISIBLE
            Utility.logData("ecom request "+parentObj.toString())


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createCardDetailRequest() {
        var jObject: JSONObject = JSONObject()
        try {

            var mainObj: JSONObject = JSONObject()
            var appIdObject: JSONObject = JSONObject()
            var deviceIdentifireObject: JSONObject = JSONObject()
            appIdObject.put("appid", SdkConfig.appID)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("appregflg", "01")
            appIdObject.put(
                "pushnkey",
                "ehy_bhncSVS0mecz8aWuOI:APA91bGH5hAbvXEWzQxr51bdZUZD8Y5_3hHGmOyUyBb8rJT_HtdhoTi9-W7-dTaSN3sj459IbmHSvMrUUPYJcZh5u6p1PXbUcVI0-9A-6Z_BXQ_VVh9D-vO0vJYa3KS7lZEOAig8rIjg"
            )

            deviceIdentifireObject.put("custunqid", SdkConfig.customerUniqueId)
            deviceIdentifireObject.put("deviceid", SdkConfig.deviceID)
            deviceIdentifireObject.put("deviceunqid", SdkConfig.deviceUniqueID)

            jObject.put("appdtls", appIdObject)
            jObject.put("deviceidentifier", deviceIdentifireObject)
            jObject.put("channelid", SdkConfig.channelId)
            jObject.put("channelid", SdkConfig.channelId)
            jObject.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
            jObject.put("token", SdkConfig.token)

            var dataObj:JSONObject = JSONObject()
            dataObj.put("cardStatus","0")
            dataObj.put("cardType","6")
            dataObj.put("customerId",SdkConfig.customerId)
            dataObj.put("sessionId",Constants.SESSIONID)
            jObject.put("msg",dataObj)

            var encryptedData = Utility.encryptpayload(jObject.toString(), Constants.ENCRYPTION_KEY)
            Utility.logData("Decrytped data $jObject")
            Utility.logData("Encrytped data $encryptedData")
            mainObj.put("channelid", SdkConfig.channelId)
            mainObj.put("appid", SdkConfig.appID)
            mainObj.put("partnerid", SdkConfig.partnerId)
            mainObj.put("servicetype", "PBfetchCardDetailsME/v1")
            var reqId = Utility.generateSequenceNumber()
            mainObj.put("requestid",reqId )
            mainObj.put("encdata", encryptedData)
            var headers: HashMap<String, String> = HashMap<String,String>()
            headers["Content-Type"] = "application/json"
            headers["channelid"] = SdkConfig.channelId
            headers["partnerid"] = SdkConfig.partnerId
            headers["requestid"] = reqId.toString()
            Constants.REQUESTID = reqId.toString()
            binding.progressDialog.visibility = View.VISIBLE
            viewModel.getCardDetails(mainObj.toString(),headers)
            Utility.logData("card details request "+mainObj.toString())


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callFetchCvv(){
        createCvvDetailRequest()
    }

    fun changeTogel(isblocked:Boolean){
        if (isblocked) {
            binding.cardBlockIv.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it, R.drawable.ic_card_block
                )
            })
        }else{
            binding.cardBlockIv.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it, R.drawable.ic_unblock_card
                )
            })
        }
    }


    private fun createCvvDetailRequest() {
        var jObject: JSONObject = JSONObject()
        try {

            var mainObj: JSONObject = JSONObject()
            var appIdObject: JSONObject = JSONObject()
            var deviceIdentifireObject: JSONObject = JSONObject()
            appIdObject.put("appid", SdkConfig.appID)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("applversion", SdkConfig.appVersion)
            appIdObject.put("appregflg", "01")
            appIdObject.put(
                "pushnkey",
                "ehy_bhncSVS0mecz8aWuOI:APA91bGH5hAbvXEWzQxr51bdZUZD8Y5_3hHGmOyUyBb8rJT_HtdhoTi9-W7-dTaSN3sj459IbmHSvMrUUPYJcZh5u6p1PXbUcVI0-9A-6Z_BXQ_VVh9D-vO0vJYa3KS7lZEOAig8rIjg"
            )

            deviceIdentifireObject.put("custunqid", SdkConfig.customerUniqueId)
            deviceIdentifireObject.put("deviceid", SdkConfig.deviceID)
            deviceIdentifireObject.put("deviceunqid", SdkConfig.deviceUniqueID)

            jObject.put("appdtls", appIdObject)
            jObject.put("deviceidentifier", deviceIdentifireObject)
            jObject.put("channelid", SdkConfig.channelId)
            jObject.put("channelid", SdkConfig.channelId)
            jObject.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
            jObject.put("token", SdkConfig.token)

            var encryptedData = Utility.encryptpayload(jObject.toString(), Constants.ENCRYPTION_KEY)
            //    Utility.logData("Decrytped data $jObject")
            //    Utility.logData("Encrytped data $encryptedData")
            mainObj.put("channelid", SdkConfig.channelId)
            mainObj.put("appid", SdkConfig.appID)
            mainObj.put("partnerid", SdkConfig.partnerId)
            mainObj.put("servicetype", "PBgetSessionME")
            var reqId = Utility.generateSequenceNumber()
            mainObj.put("requestid",reqId )
            mainObj.put("encdata", encryptedData)
            var headers: HashMap<String, String> = HashMap<String,String>()
            headers["Content-Type"] = "application/json"
            headers["channelid"] = SdkConfig.channelId
            headers["partnerid"] = SdkConfig.partnerId
            headers["requestid"] = reqId.toString()
            Constants.REQUESTID = reqId.toString()

            var mainObject = MainObject()
            mainObject.channelid =SdkConfig.channelId
            mainObject.signcs =Constants.SIGNCS
            mainObject.token = SdkConfig.token
            mainObject.servicetype = "PBgetCVVdetailsME"
            mainObject.requestid = reqId
            mainObject.channelid = SdkConfig.channelId
            mainObject.appid = SdkConfig.appID
            mainObject.partnerid = SdkConfig.partnerId


            // *********** dto

            var appdtls = Appdtls()
            appdtls.appid = SdkConfig.appID
            appdtls.applversion = SdkConfig.appVersion
            appdtls.appregflg = Constants.APPREGFLG
            appdtls.pushnkey = Constants.PUSHNKEY


            var gson = Gson()
            var formattedAppDtlsJson = gson.toJson(appdtls)
            var formattedAppDtlsObject:JSONObject = JSONObject(formattedAppDtlsJson)

            //  val appDtlsJson = ObjectMapper().writer().writeValueAsString(appdtls)
            //   val formattedAppDtlsJson = appDtlsJson.replace("\\n", "").replace("\\r", "")


            var deviceidentifier = Deviceidentifier()
            deviceidentifier.deviceid = SdkConfig.deviceID
            deviceidentifier.deviceunqid = SdkConfig.deviceUniqueID
            deviceidentifier.custunqid = SdkConfig.customerUniqueId

            var formattedDeviceIdentifier = gson.toJson(deviceidentifier)
            var formattedDeviceIdentifierObject:JSONObject = JSONObject(formattedDeviceIdentifier)

            //   val deviceidentifierJson = ObjectMapper().writer().writeValueAsString(deviceidentifier)
            //   val formattedDeviceIdentifier = deviceidentifierJson.replace("\\n", "").replace("\\r", "")


            var msg = Msg()
            msg?.cardNo = Constants.CARDNO
            msg?.expiryDate = Constants.CARD_EXPIRY.replace("/","")
            msg?.sessionId = Constants.SESSIONID


            var formattedMsg:String = gson.toJson(msg)
            var formattedMsgObject:JSONObject = JSONObject(formattedMsg)
            //  val msgJson = ObjectMapper().writer().writeValueAsString(msg)
            //  val formattedMsg = msgJson.replace("\\n", "").replace("\\r", "")




            var jsonObject = JSONObject()
            jsonObject.put("appdtls",formattedAppDtlsObject)
            jsonObject.put("deviceidentifier",formattedDeviceIdentifierObject)
            jsonObject.put("msg",formattedMsgObject)
            jsonObject.put("channelid",SdkConfig.channelId)

            //   val decObj = jsonObject.toString().replace("\\n", "").replace("\\r", "")
            var encData = Utility.encryptpayload(jsonObject.toString(),Constants.ENCRYPTION_KEY)
            Utility.logData("decrypted cvv request data "+jsonObject.toString())

            var json = gson.toJson(mainObject)
          //  val json = ObjectMapper().writer().writeValueAsString(mainObject)
            val formattedJson = json.replace("\\n", "").replace("\\r", "")
            var jsonObjectMain = JSONObject(formattedJson)
            jsonObjectMain.put("encdata",encData)



            viewModel.getCVVDetails(jsonObjectMain.toString(),headers)
            binding.progressDialog.visibility = View.VISIBLE
            Utility.logData("get CVV request "+jsonObjectMain.toString())


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun flipCard(view: View?) {
        if (!mIsBackVisible) {
            showBackVisibleCard()
        } else {
            showFrontVisibleCard()
        }
    }

    private fun showBackVisibleCard() {
        if (!mIsBackVisible) {
            mSetLeftCardIn?.setTarget(mCardBackLayout)
            mSetRightOut?.setTarget(mCardFrontLayout)
            mSetLeftCardIn?.start()
            mSetRightOut?.start()
            mIsBackVisible = true
        }
    }

    fun showFrontVisibleCard() {
        if (mIsBackVisible) {
            mSetLeftCardIn?.setTarget(mCardFrontLayout)
            mSetRightOut?.setTarget(mCardBackLayout)
            mSetLeftCardIn?.start()
            mSetRightOut?.start()
            mIsBackVisible = false
        }
    }

    private fun loadAnimations() {
        mSetRightOut =
            AnimatorInflater.loadAnimator(activity, R.animator.out_animation) as AnimatorSet
        mSetLeftOut =
            AnimatorInflater.loadAnimator(activity, R.animator.in_animation) as AnimatorSet
        mSetLeftIn =
            AnimatorInflater.loadAnimator(activity, R.animator.in_animation) as AnimatorSet
        mSetRightCardOut =
            AnimatorInflater.loadAnimator(
                activity,
                R.animator.card_flip_right_out
            ) as AnimatorSet
        mSetRightCardIn =
            AnimatorInflater.loadAnimator(
                activity,
                R.animator.card_flip_right_in
            ) as AnimatorSet
        mSetLeftCardOut =
            AnimatorInflater.loadAnimator(
                activity,
                R.animator.card_flip_left_out
            ) as AnimatorSet
        mSetLeftCardIn =
            AnimatorInflater.loadAnimator(
                activity,
                R.animator.card_flip_left_in
            ) as AnimatorSet
    }

    private fun orderCard() {

        try {

            var reqId = Utility.generateSequenceNumber()

            var headers: HashMap<String, String> = HashMap<String,String>()
            headers["Content-Type"] = "application/json"
            headers["channelid"] = SdkConfig.channelId
            headers["partnerid"] = SdkConfig.partnerId
            headers["requestid"] = reqId.toString()
            Constants.REQUESTID = reqId.toString()

            // *********** dto

            var mainObject = MainObject()
            mainObject.channelid =SdkConfig.channelId
            mainObject.signcs =Constants.SIGNCS
            mainObject.token = SdkConfig.token
            //   mainObject.servicetype = "PBgetCVVdetailsME"
            mainObject.requestid = reqId
            mainObject.channelid = SdkConfig.channelId
            mainObject.appid = SdkConfig.appID
            mainObject.partnerid = SdkConfig.partnerId




            var appdtls = Appdtls()
            appdtls.appid = SdkConfig.appID
            appdtls.applversion = SdkConfig.appVersion
            appdtls.appregflg = Constants.APPREGFLG
            appdtls.pushnkey = Constants.PUSHNKEY


            var gson = Gson()
            var formattedAppDtlsJson = gson.toJson(appdtls)
            var formattedAppDtlsObject:JSONObject = JSONObject(formattedAppDtlsJson)

            var deviceidentifier = Deviceidentifier()
            deviceidentifier.deviceid = SdkConfig.deviceID
            deviceidentifier.deviceunqid = SdkConfig.deviceUniqueID
            deviceidentifier.custunqid = SdkConfig.customerUniqueId

            var formattedDeviceIdentifier = gson.toJson(deviceidentifier)
            var formattedDeviceIdentifierObject:JSONObject = JSONObject(formattedDeviceIdentifier)

            var msg = Msg()
            msg?.cardNo = Constants.CARDNO
            msg?.expiryDate = Constants.CARD_EXPIRY.replace("/","")
            msg?.sessionId = Constants.SESSIONID


            var formattedMsg:String = gson.toJson(msg)
            var formattedMsgObject:JSONObject = JSONObject(formattedMsg)

            var json = gson.toJson(mainObject)
            var jsonObject = JSONObject()
            jsonObject.put("appdtls",formattedAppDtlsObject)
            jsonObject.put("deviceidentifier",formattedDeviceIdentifierObject)
            jsonObject.put("msg",formattedMsgObject)
            jsonObject.put("channelid",SdkConfig.channelId)

            var encData = Utility.encryptpayload(jsonObject.toString(),Constants.ENCRYPTION_KEY)
            Utility.logData("decrypted ordercard request data "+jsonObject.toString())


            //  val json = ObjectMapper().writer().writeValueAsString(mainObject)
            val formattedJson = json.replace("\\n", "").replace("\\r", "")
            var jsonObjectMain = JSONObject(formattedJson)
            //  jsonObjectMain.put("encdata",encData)
            jsonObject.put("serviceremark","3")
            jsonObject.put("userremark","2")
            jsonObject.put("custremark","1")

            viewModel.orderCard(jsonObject.toString(),headers)
            binding.progressDialog.visibility = View.VISIBLE
            Utility.logData("order card request $jsonObject")


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        loadAnimations()
        if (Constants.SESSION_ACTIVE){
            createCardDetailRequest()
        }else{
            createSessionRequest()
        }
    }


}