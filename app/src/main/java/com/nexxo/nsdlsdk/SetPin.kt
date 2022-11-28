package com.nexxo.nsdlsdk

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.Window
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.Utility
import com.nexxo.nsdlsdk.apiscall.MainRepository
import com.nexxo.nsdlsdk.apiscall.RetrofitService
import com.nexxo.nsdlsdk.databinding.ActivityGenerateOtpBinding
import com.nexxo.nsdlsdk.dto.*
import com.nexxo.nsdlsdk.fragments.MpinFragment
import com.nexxo.nsdlsdk.interfaces.iCvvCommunitor
import com.nexxo.nsdlsdk.model.PinResponse
import com.nexxo.nsdlsdk.utility.SdkConfig
import com.nexxo.nsdlsdk.viewmodel.DashboardViewModel
import com.nexxo.nsdlsdk.viewmodel.SetPinViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class SetPin :AppCompatActivity() {
    lateinit var binding: ActivityGenerateOtpBinding
    private lateinit var activity: Activity
    private var isFrom: Int? = 0
    private lateinit var viewModel: SetPinViewModel
    lateinit var mpin: MpinFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this@SetPin
        viewModel = ViewModelProvider(this).get(
            SetPinViewModel::class.java
        )
        binding?.toolbar?.tvHeaderText!!.text =
            resources.getString(R.string.txt_generate_pin)
        binding?.toolbar?.llBackArrow!!.setOnClickListener(::onBackClicked)

        binding.toolbar.llBackArrow.setOnClickListener {
            finish()
        }
        mpin = MpinFragment(activity)
        mpin.updater(updateCvv)
        initXMLComponents()

    }


    private fun resendOtpClicked(view: View?) {


        createsetPinRequest()
    }

    private fun initXMLComponents() {
        intent.getIntExtra(Constants.isFrom, 0).also {
            if (it != null) {
                isFrom = it
            }
        }
        if (isFrom == 1) {
            binding.toolbar.tvHeaderText.text = getString(R.string.reset_pin)
            binding.ivLogo.setImageResource(R.drawable.ic_card_generate_otp_bg)
            binding.tvGenerateText.text = getString(R.string.txt_coroprate_generate_pin_digit_code)
        }
            binding.tvGenerateText.text = getString(R.string.txt_indipaisa_generate_pin_digit_code)

        binding.btnGenerateOtp.setOnClickListener(::verifyOtpClicked)
        binding.btnResendotp1.setOnClickListener(::resendOtpClicked)
        binding.btnActivate.setOnClickListener(::resendPinClicked)


        viewModel.token.observe(activity as FragmentActivity) {
            binding.progressDialog.visibility = View.GONE
            Utility.logData("setpin response *** "+it.response)
            showSuccessDialog(this, "Pin changed successfully")
        }

    }

    private fun resendPinClicked(view: View?) {
        if (validation()) {
            Utility.hideKeypad(activity)

            mpin.show(
                supportFragmentManager,
                mpin.tag
            )


        }

    }


    var updateCvv: iCvvCommunitor = object : iCvvCommunitor {
        override fun fetchCvv() {
            createsetPinRequest()
        }
    }

    private fun validation(): Boolean {
        if (binding.pinSet.text!!.isNullOrBlank() || binding.pinSet.text!!.length != 4) {
//            Utility.showMessageInToast(this, getString(R.string.enter_pin))
            binding.pinSet.requestFocus()
            binding.pinSet.error = getString(R.string.enter_pin)
            return false
        } else if (binding.pinConfirmSet.text!!.isNullOrBlank() || binding.pinConfirmSet.text!!.length != 4) {
//            Utility.showMessageInToast(this, getString(R.string.enter_confirm_pin))
            binding.pinConfirmSet.requestFocus()
            binding.pinConfirmSet.error = getString(R.string.enter_confirm_pin)
            return false
        } else if (!((binding.pinSet.text?.toString()
                .equals(binding.pinConfirmSet.text?.toString())))
        ) {
            binding.pinConfirmSet.requestFocus()
            binding.pinConfirmSet.error = getString(R.string.enter_pin_valid_error)
//            Utility.showMessageInToast(this, getString(R.string.enter_pin_valid))
            return false
        }
        return true;
    }



    private fun createsetPinRequest() {
        try {

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


            var msg = PinSetMsg()
            msg?.cardNo = Constants.CARDNO
            msg?.expiryDate = Constants.CARD_EXPIRY.replace("/","")
            msg?.sessionId = Constants.SESSIONID
            msg.pin = null
            msg.newPin = binding.pinSet.text.toString()


            var formattedMsg:String = gson.toJson(msg)
            var formattedMsgObject:JSONObject = JSONObject(formattedMsg)

            var jsonObject = JSONObject()
            jsonObject.put("appdtls",formattedAppDtlsObject)
            jsonObject.put("deviceidentifier",formattedDeviceIdentifierObject)
            jsonObject.put("msg",formattedMsgObject)
            jsonObject.put("channelid", SdkConfig.channelId)

            //   val decObj = jsonObject.toString().replace("\\n", "").replace("\\r", "")
            var encData = Utility.encryptpayload(jsonObject.toString(),Constants.ENCRYPTION_KEY)
          //  Utility.logData("decrypted cvv request data "+jsonObject.toString())

            var reqId = Utility.generateSequenceNumber()
            var mainObject = MainObject()
            mainObject.channelid =SdkConfig.channelId
            mainObject.signcs =Constants.SIGNCS
            mainObject.token = SdkConfig.token
            mainObject.servicetype = "PBgetCVVdetailsME"
            mainObject.requestid = reqId
            mainObject.channelid = SdkConfig.channelId
            mainObject.appid = SdkConfig.appID
            mainObject.partnerid = SdkConfig.partnerId

           // var gson = Gson()
            var json = gson.toJson(mainObject)
            //val json = ObjectMapper().writer().writeValueAsString(mainObject)
            val formattedJson = json.replace("\\n", "").replace("\\r", "")
            var jsonObjectMain = JSONObject(formattedJson)
            jsonObjectMain.put("encdata",encData)

            var headers: HashMap<String, String> = HashMap<String,String>()
            headers["Content-Type"] = "application/json"
            headers["channelid"] = SdkConfig.channelId
            headers["partnerid"] = SdkConfig.partnerId
            headers["requestid"] = reqId.toString()
            Constants.REQUESTID = reqId.toString()

            viewModel.setPin(jsonObjectMain.toString(),headers)
            binding.progressDialog.visibility = View.VISIBLE
            Utility.logData("set pin request "+jsonObjectMain.toString())


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



    private fun onBackClicked(view: View?) {

    }

    fun verifyOtpClicked(view: View?) {


    }


}
