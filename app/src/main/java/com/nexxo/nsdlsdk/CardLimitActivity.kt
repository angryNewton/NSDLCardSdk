package com.nexxo.nsdlsdk

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.view.View
import android.view.Window
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.nexxo.nsdlsdk.databinding.ActivityCardLimitBinding
import com.nexxo.nsdlsdk.databinding.ActivityGenerateOtpBinding
import com.nexxo.nsdlsdk.dto.*
import com.nexxo.nsdlsdk.fragments.CardPermanentBlock
import com.nexxo.nsdlsdk.fragments.CardUnBlockBottomSheetFragment
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.SdkConfig
import com.nexxo.nsdlsdk.utility.Utility
import com.nexxo.nsdlsdk.viewmodel.CardLimitActivityViewModel
import com.nexxo.nsdlsdk.viewmodel.SetPinViewModel
import org.json.JSONObject
import java.util.*

class CardLimitActivity : AppCompatActivity() {
    lateinit var binding: ActivityCardLimitBinding
    lateinit var activity:Activity
    private lateinit var viewModel: CardLimitActivityViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardLimitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this@CardLimitActivity
        viewModel = ViewModelProvider(this).get(
            CardLimitActivityViewModel::class.java
        )

        if (Constants.customerData!=null) {
            binding.edtEcomLimit.text = Editable.Factory.getInstance()
                .newEditable(Utility.removeDecimal(Constants.customerData!!.EComLimit.toString()))
            binding.edtPosLimit.text = Editable.Factory.getInstance()
                .newEditable(Utility.removeDecimal(Constants.customerData!!.POSLimit.toString()))
            binding.edtAtmLimit.text = Editable.Factory.getInstance()
                .newEditable(Utility.removeDecimal(Constants.customerData!!.ATMLimit.toString()))

            binding.enableAtmWithdrawal.isChecked = Constants.customerData!!.DomesticATM.equals("Y")
            binding.enableContactless.isChecked =
                Constants.customerData!!.DomesticContactless.equals("Y")
            binding.enableEcom.isChecked = Constants.customerData!!.DomesticECOM.equals("Y")
            binding.enablePos.isChecked = Constants.customerData!!.DomesticPOS.equals("Y")
        }
        if (binding.enableContactless.isChecked){
            binding.txtLock.text = "Active"
            binding.txtLock.setTextColor(Color.GREEN)
        }else{
            binding.txtLock.text = "INACTIVE"
            binding.txtLock.setTextColor(Color.RED)
        }

        if (binding.enableAtmWithdrawal.isChecked){
            binding.txtAtmActive.text = "ACTIVE"
            binding.txtAtmActive.setTextColor(Color.GREEN)
        }else{
            binding.txtAtmActive.text = "INACTIVE"
            binding.txtAtmActive.setTextColor(Color.RED)
        }

        if (binding.enableEcom.isChecked){
            binding.txtEcomActive.text = "ACTIVE"
            binding.txtEcomActive.setTextColor(Color.GREEN)
        }else{
            binding.txtEcomActive.text = "INACTIVE"
            binding.txtEcomActive.setTextColor(Color.RED)
        }

        if (binding.enablePos.isChecked){
            binding.txtPosActive.text = "ACTIVE"
            binding.txtPosActive.setTextColor(Color.GREEN)
        }else{
            binding.txtPosActive.text = "INACTIVE"
            binding.txtPosActive.setTextColor(Color.RED)
        }



        binding.toolbar.llBackArrow.setOnClickListener {
            finish()
        }
        binding.toolbar.tvHeaderText.text = "Card Control"

        viewModel.ecomEnale.observe(activity as FragmentActivity) {
            binding.progressDialog.visibility = View.GONE
            Utility.logData("Ecom resp "+it.response)
            var atmLimit =binding.edtAtmLimit.text.toString().replace(",","").toDouble()
            var posLimit =binding.edtPosLimit.text.toString().replace(",","").toDouble()
            var ecomLimit: Double = 0.0
            if (!binding.edtEcomLimit.text.toString().replace(",","").isNullOrEmpty()){
                ecomLimit =binding.edtEcomLimit.text.toString().replace(",","").toDouble()
            }

            if (ecomLimit>Constants.MAX_ECOM_LIMIT){
                Toast.makeText(this,"Ecom max limit is INR "+Constants.MAX_ECOM_LIMIT,Toast.LENGTH_SHORT).show()
            }
            else if (posLimit>Constants.MAX_POS_LIMIT){
                Toast.makeText(this,"POS max limit is INR "+Constants.MAX_POS_LIMIT,Toast.LENGTH_SHORT).show()
            }
           else if (atmLimit>Constants.MAX_ATM_LIMIT){
                Toast.makeText(this,"Atm max limit is INR "+Constants.MAX_ATM_LIMIT,Toast.LENGTH_SHORT).show()
            }else{
                updateLimit()
            }

        }

        viewModel.updateLimitLive.observe(activity as FragmentActivity) {
            binding.progressDialog.visibility = View.GONE
            Utility.logData("Ecom resp "+it.response)
            showSuccessDialog(this,"Card limit updated successfully")
        }

        binding.btnActivate.setOnClickListener {
            enableEcomRequest()
        }

        binding.txtChangeEcomLimit.setOnClickListener {
            if (binding.enableEcom.isChecked){

                binding.edtEcomLimit.isEnabled = true
                binding.edtEcomLimit.isClickable = true

                binding.edtEcomLimit.inputType = InputType.TYPE_CLASS_NUMBER
                binding.edtEcomLimit.requestFocus()
                var amount:String = binding.edtEcomLimit.text.toString()
                var edtAmount = Editable.Factory.getInstance().newEditable(amount)
                binding.edtEcomLimit.visibility = View.VISIBLE

                binding.edtEcomLimit.text = edtAmount

                Utility.showKeyboard(activity)
               // binding.txtEcomLimit.visibility = View.GONE

            }else{
                Utility.logData("ecom type trxn not allowed")
            }
        }

        binding.enableEcom.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Utility.logData("checked")
               // binding.edtEcomLimit.requestFocus()
                //Utility.showKeyboard(activity)
            }else{
                binding.edtEcomLimit.inputType = InputType.TYPE_NULL
                binding.edtEcomLimit.isClickable = false
                Utility.hideKeyboard(activity)
               /* binding.txtEcomLimit.visibility = View.VISIBLE
                Utility.logData("not checked")*/
            }
        }


        binding.txtChangePosLimit.setOnClickListener {
            if (binding.enablePos.isChecked){
                binding.edtPosLimit.inputType = InputType.TYPE_CLASS_NUMBER
                binding.edtPosLimit.isClickable = true
                var amount:String = binding.edtPosLimit.text.toString()
                var edtAmount = Editable.Factory.getInstance().newEditable(amount)
                binding.edtPosLimit.visibility = View.VISIBLE
                binding.edtPosLimit.isEnabled = true
                binding.edtPosLimit.text = edtAmount
                binding.edtPosLimit.requestFocus()
                Utility.showKeyboard(activity)
                // binding.txtEcomLimit.visibility = View.GONE

            }else{
                Utility.logData("ecom type trxn not allowed")
            }
        }

        binding.enablePos.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Utility.logData("checked")
                // binding.edtEcomLimit.requestFocus()
                //Utility.showKeyboard(activity)
            }else{
                binding.edtPosLimit.inputType = InputType.TYPE_NULL
                binding.edtPosLimit.isClickable = false
                Utility.hideKeyboard(activity)
                /* binding.txtEcomLimit.visibility = View.VISIBLE
                 Utility.logData("not checked")*/
            }
        }

        binding.txtChangeAtmLimit.setOnClickListener {
            if (binding.enableAtmWithdrawal.isChecked){
                binding.edtAtmLimit.inputType = InputType.TYPE_CLASS_NUMBER
                binding.edtAtmLimit.isClickable = true
                var amount:String = binding.edtAtmLimit.text.toString()
                var edtAmount = Editable.Factory.getInstance().newEditable(amount)
                binding.edtAtmLimit.visibility = View.VISIBLE
                binding.edtAtmLimit.isEnabled = true
                binding.edtAtmLimit.text = edtAmount
                binding.edtAtmLimit.requestFocus()
                Utility.showKeyboard(activity)
                // binding.txtEcomLimit.visibility = View.GONE

            }else{
                Utility.logData("ecom type trxn not allowed")
            }
        }

        binding.enableAtmWithdrawal.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Utility.logData("checked")
                // binding.edtEcomLimit.requestFocus()
                //Utility.showKeyboard(activity)
            }else{
                binding.edtAtmLimit.inputType = InputType.TYPE_NULL
                binding.edtAtmLimit.isClickable = false
                Utility.hideKeyboard(activity)
                /* binding.txtEcomLimit.visibility = View.VISIBLE
                 Utility.logData("not checked")*/
            }
        }

        binding.permanentLockSwitch.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked){
                Utility.logData("checked")
                val bottomSheetUnblockFragment =
                    CardPermanentBlock(activity)
                bottomSheetUnblockFragment.show(
                    supportFragmentManager,
                    bottomSheetUnblockFragment.tag)
            }else{

            }
        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)


    private fun updateLimit() {
        try {

            var appdtls = Appdtls()
            appdtls.appid = SdkConfig.appID
            appdtls.applversion = SdkConfig.appVersion
            appdtls.appregflg = Constants.APPREGFLG
            appdtls.pushnkey = Constants.PUSHNKEY

            var gson = Gson()
            var formattedAppDtlsJson = gson.toJson(appdtls)
            var formattedAppDtlsObject: JSONObject = JSONObject(formattedAppDtlsJson)

            var deviceidentifier = Deviceidentifier()
            deviceidentifier.deviceid = SdkConfig.deviceID
            deviceidentifier.deviceunqid = SdkConfig.deviceUniqueID
            deviceidentifier.custunqid = SdkConfig.customerUniqueId

            var formattedDeviceIdentifier = gson.toJson(deviceidentifier)
            var formattedDeviceIdentifierObject: JSONObject = JSONObject(formattedDeviceIdentifier)


            var msg = LimitSetMsg()
            msg?.cardNo = Constants.CARDNO
            msg?.ecomlimit = binding.edtEcomLimit.text.toString().replace(",","")
            msg?.atmlimit = binding.edtAtmLimit.text.toString().replace(",","")
            msg?.poslimit = binding.edtPosLimit.text.toString().replace(",","")
            msg?.sessionId = Constants.SESSIONID
          //  msg.newPin = binding.pinSet.text.toString()


            var formattedMsg:String = gson.toJson(msg)
            var formattedMsgObject: JSONObject = JSONObject(formattedMsg)

            var jsonObject = JSONObject()
            jsonObject.put("appdtls",formattedAppDtlsObject)
            jsonObject.put("deviceidentifier",formattedDeviceIdentifierObject)
            jsonObject.put("msg",formattedMsgObject)
            jsonObject.put("channelid", SdkConfig.channelId)
            jsonObject.put("signcs",Constants.SIGNCS)
            jsonObject.put("token",SdkConfig.token)

            //   val decObj = jsonObject.toString().replace("\\n", "").replace("\\r", "")
            var encData = Utility.encryptpayload(jsonObject.toString(), Constants.ENCRYPTION_KEY)
         //   Utility.logData("decrypted cvv request data "+jsonObject.toString())

            var reqId = Utility.generateSequenceNumber()
            var mainObject = MainObject()
            mainObject.channelid = SdkConfig.channelId
            mainObject.signcs = Constants.SIGNCS
            mainObject.token = SdkConfig.token
            mainObject.servicetype = "PBsetCardLimitME"
            mainObject.requestid = reqId
            mainObject.channelid = SdkConfig.channelId
            mainObject.appid = SdkConfig.appID
            mainObject.partnerid = SdkConfig.partnerId

            var json = gson.toJson(mainObject)
           // val json = ObjectMapper().writer().writeValueAsString(mainObject)
            val formattedJson = json.replace("\\n", "").replace("\\r", "")
            var jsonObjectMain = JSONObject(formattedJson)
            jsonObjectMain.put("encdata",encData)

            var headers: HashMap<String, String> = HashMap<String,String>()
            headers["Content-Type"] = "application/json"
            headers["channelid"] = SdkConfig.channelId
            headers["partnerid"] = SdkConfig.partnerId
            headers["requestid"] = reqId.toString()
            Constants.REQUESTID = reqId.toString()

            viewModel.setLimit(jsonObjectMain.toString(),headers)
            binding.progressDialog.visibility = View.VISIBLE
            Utility.logData("update limit request "+jsonObjectMain.toString())


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
            if (binding.enableEcom.isChecked){
                mainObj.put("ecom","Y" )
            }else{
                mainObj.put("ecom","N" )
            }

            if (binding.enableAtmWithdrawal.isChecked){
                mainObj.put("atm","Y" )
            }else{
                mainObj.put("atm","N" )
            }

            if (binding.enableContactless.isChecked){
                mainObj.put("contactless","Y" )
            }else{
                mainObj.put("contactless","N" )
            }

            if (binding.enablePos.isChecked){
                mainObj.put("pos","Y" )
            }else{
                mainObj.put("pos","N" )
            }

            mainObj.put("cardNo",Constants.CARDNO )
            mainObj.put("type","D" )

            var encryptedData = Utility.encryptpayload(mainObj.toString(), Constants.ENCRYPTION_KEY)
        //    Utility.logData("Decrytped data ecom emable $mainObj")
        //    Utility.logData("Encrytped data $encryptedData")


            parentObj.put("encdata", encryptedData)
            parentObj.put("encdata", encryptedData)
            // mainObj.put("encdata", encryptedData)
            var headers: HashMap<String, String> = HashMap<String,String>()
            headers["Content-Type"] = "application/json"
            headers["channelid"] = SdkConfig.channelId
            headers["partnerid"] = SdkConfig.partnerId
            headers["requestid"] = reqId.toString()
            Constants.REQUESTID = reqId.toString()
            viewModel.enableTransactions(parentObj.toString(),headers)
            binding.progressDialog.visibility = View.VISIBLE
            Utility.logData("enable transactions type  "+parentObj.toString())


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSuccessDialog(context: Context, msg: String) {
        try {
            Handler().postDelayed({
                val dialog = Dialog(context)
                Objects.requireNonNull(dialog.window)
                    ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.success_dialog)
                val tv_Messgaetext: AppCompatTextView = dialog.findViewById(R.id.tv_headertext)
                tv_Messgaetext.text = msg

                val lyt: LinearLayout = dialog.findViewById(R.id.popup_body_lyt)
                lyt.visibility = View.GONE

                val crossIV: AppCompatImageView = dialog.findViewById(R.id.btn_gst_cross)
                crossIV.setOnClickListener {
                    val activity = context as Activity
                    dialog.dismiss()
                    activity.finish()
                    activity.overridePendingTransition(R.anim.fadein, R.anim.fadeout)
                }
                dialog.show()
            }, Constants.SPLASH_SET_PIN)
        } catch (e: Exception) {
            Utility.logData("Exception while showSuccessDialog for reset pin -${e.localizedMessage}")
        }
    }
}