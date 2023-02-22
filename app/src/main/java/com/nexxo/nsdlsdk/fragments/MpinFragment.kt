package com.nexxo.nsdlsdk.fragments

import Decoder.BASE64Encoder
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.nexxo.nsdlsdk.ActivityDashboardCard
import com.nexxo.nsdlsdk.databinding.LayoutMpinBinding
import com.nexxo.nsdlsdk.interfaces.iBlockUnblock
import com.nexxo.nsdlsdk.interfaces.iCvvCommunitor
import com.nexxo.nsdlsdk.interfaces.iTokenData
import com.nexxo.nsdlsdk.model.CredBean
import com.nexxo.nsdlsdk.utility.Constants
import com.nexxo.nsdlsdk.utility.SdkConfig
import com.nexxo.nsdlsdk.utility.Utility
import com.nexxo.nsdlsdk.viewmodel.DashboardViewModel
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.json.JSONObject
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class MpinFragment( context: Context) : BottomSheetDialogFragment() {
    private lateinit var binding: LayoutMpinBinding
    private lateinit var viewModel: DashboardViewModel
    var tokenKey:String = ""
    var pwdKey:String = ""
    var credVal:String = ""
    var ivSpec:String = ""
    var isExternalCall:Boolean = false
    lateinit var fetchCvv:iCvvCommunitor

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        try {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = LayoutMpinBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(
            DashboardViewModel::class.java
        )
        dialog!!.setCanceledOnTouchOutside(false)
        binding.crossIv.setOnClickListener {
            dialog!!.dismiss()
        }
        binding.mpin.text = "".toEditable()

        binding.mpin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length == 6) {
                    hideKeypad()

                }
            }
        })

        binding.confirmMpin.setOnClickListener {
            if (binding.mpin.length()==6){
                createSessionRequest()
            }else{
                Toast.makeText(context,"Please enter your Mpin",Toast.LENGTH_SHORT).show()
            }

        }

        viewModel.mpinTokenRespData.observe(activity as FragmentActivity) {
            Utility.logData("***** MpinToken ***** "+it.tokenDtls!!.tokenkey)
            tokenKey = it.tokenDtls!!.tokenkey.toString()
            pwdKey = it.tokenDtls!!.pwdkey.toString()
            if (isExternalCall){

            }else{
                createBlock(binding.mpin.text.toString())
            }

           // createCardDetailRequest()
        }


        viewModel.verifyMpinResponse.observe(activity as FragmentActivity) {
            Utility.logData("verify MPIN response "+it.messageval.toString())
           // (activity as ActivityDashboardCard).callFetchCvv()
            try{
                dialog!!.dismiss()
                if (it.respcode == "99"){
                    Toast.makeText(context,it.response,Toast.LENGTH_SHORT).show()
                }else{
                    fetchCvv.fetchCvv()
                }

            }catch (e:Exception){
                e.printStackTrace()
            }

        }

        return binding.root
    }

    /*public fun getTokenData(iTokenData:iTokenData,context: Context){
        iData = iTokenData
        isExternalCall = true
        viewModel = ViewModelProvider(MpinFragment(context)).get(
            DashboardViewModel::class.java
        )
        createSessionRequest()
    }*/
    private fun hideKeypad() {
        try {
            val windowToken = dialog!!.window!!.decorView.rootView
            val imm =
                dialog!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (ex: java.lang.Exception) {
            Utility.logData(ex.toString())
        }
    }

    fun updater(isBlicked: iCvvCommunitor){
        this.fetchCvv = isBlicked
    }

    fun closeDialog(){
       binding.crossIv.performClick()
    }

    fun resetPin(){
        binding.mpin.text = "".toEditable()
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    private fun createSessionRequest() {
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

            jObject.put("appdtls", appIdObject)
            jObject.put("deviceidentifier", deviceIdentifireObject)
            jObject.put("channelid", SdkConfig.channelId)
            jObject.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
            mainObj.put("token", SdkConfig.token)

            var dataObj:JSONObject = JSONObject()
            dataObj.put("userid",SdkConfig.customerId)
            dataObj.put("usertype",Constants.USER_TYPE)
            dataObj.put("role",Constants.ROLE)

            mainObj.put("custcreddtls",dataObj)

            mainObj.put("deviceidentifier",deviceIdentifireObject)
            mainObj.put("appdtls",appIdObject)

            var encryptedData = Utility.encryptpayload(jObject.toString(), Constants.ENCRYPTION_KEY)
          //  Utility.logData("Decrytped data $jObject")
          //  Utility.logData("Encrytped data $encryptedData")
            mainObj.put("channelid", SdkConfig.channelId)
            var reqId = Utility.generateSequenceNumber()
          //  mainObj.put("requestid",reqId)
            mainObj.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
           // mainObj.put("encdata", encryptedData)
            mainObj.put("reqtokentype", "TXNAUTH")
            Constants.REQUESTID = reqId.toString()
            viewModel.generateTokenForMpin(mainObj.toString())

            Utility.logData("mpin token request $mainObj")


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun validateMpinRequest() {
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

            jObject.put("channelid", SdkConfig.channelId)
            jObject.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
            mainObj.put("token", SdkConfig.token)

            var dataObj:JSONObject = JSONObject()
            dataObj.put("userid",SdkConfig.customerId)
            dataObj.put("usertype",Constants.USER_TYPE)
            dataObj.put("role",Constants.ROLE)

            mainObj.put("custcreddtls",dataObj)

            var tlsObject:JSONObject = JSONObject()
            tlsObject.put("credtype","MPIN")
            tlsObject.put("credval",credVal)
            tlsObject.put("credcategory","LOGIN")
           // tlsObject.put("credsaltkey",SdkConfig.token)
            tlsObject.put("credsaltkey",tokenKey.subSequence(0,128))
            mainObj.put("creddtls",tlsObject)
            mainObj.put("ivspec",ivSpec)

            mainObj.put("deviceidentifier",deviceIdentifireObject)
            mainObj.put("appdtls",appIdObject)

            mainObj.put("channelid", SdkConfig.channelId)
            var reqId = Utility.generateSequenceNumber()
            //  mainObj.put("requestid",reqId)
            mainObj.put(
                "signcs",
                "kDwuijxuzMA6AesXWv5zSSoGATyKD6a4TmUsJcI5QBeaXtXuF9tOO7D0hiz+Rd6tua5+OeT/A5TDo9MZskn5Fw=="
            )
           // mainObj.put("reqtokentype", "TXNAUTH")
            Constants.REQUESTID = reqId.toString()
            viewModel.verifyMpin(mainObj.toString())

            Utility.logData(" mpin verification request  $mainObj")


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBlock(strMpin: String) {
        try {
            Utility.logData("plain mpin $strMpin")
            var calculatedHash = calculateHash(strMpin, pwdKey)

            val passkey: String = tokenKey.substring(128, 256)

            val secreteData: Array<String> =
                getEncryptedTextFor(calculatedHash, passkey)

            Utility.logData("RSA Plain: "+ secreteData[0])

            val rsaEncryptedData = encryptFinal(secreteData[0])
            Utility.logData("RSA Encrypted: "+rsaEncryptedData!!)

            val credBean = CredBean()
            credBean.credId = "NJDCWIKHIWUEDINN"
            credBean.credBlock = rsaEncryptedData

            //val ow = ObjectMapper().writer().withDefaultPrettyPrinter()
            var gson = Gson()
            var json = gson.toJson(credBean)
            //val json = ObjectMapper().writer().writeValueAsString(credBean)
            val formattedJson = json.replace("\\n", "").replace("\\r", "")

            val encoder: Base64.Encoder = Base64.getEncoder()
            val encodedBase64CredVal: String =
                encoder.encodeToString(formattedJson.toByteArray())

            credVal = encodedBase64CredVal
            ivSpec = secreteData[1]

            Utility.logData("CREDVAL*******  $credVal")
            Utility.logData("ivSpec*******  $ivSpec")

            validateMpinRequest()


        } catch (ex: Exception) {
            ex.printStackTrace()
            val intent = Intent()
            intent.putExtra("credVal", "")
            intent.putExtra("ivSpec", "")
            intent.putExtra("status", "fail")
            intent.putExtra("reasondtl", ex.message)

        }
    }

    fun calculateHash(data: String, salt: String): String {
        return String(Hex.encodeHex(DigestUtils.sha512(data + salt)))
    }


    fun getEncryptedTextFor(calculatedHash: String, passKey: String): Array<String> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keyBytes = ByteArray(16)



        val b: ByteArray = passKey.substring(0, 16).toByteArray()



        var len = b.size
        if (len > keyBytes.size) len = keyBytes.size
        System.arraycopy(b, 0, keyBytes, 0, len)



        val keySpec = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(keyBytes, 0, 16)
        val iv_spec = ivSpec.iv
        val base64Encoder1 = BASE64Encoder()
        val encoded_iv: String = base64Encoder1.encode(iv_spec)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val charSet = "UTF-8"
        val `in` = calculatedHash.toByteArray(charset(charSet))
        val out = cipher.doFinal(`in`)
        val base64Encoder = BASE64Encoder()
        val encStr: String = base64Encoder.encode(out)
        //Log.d("AESEnstr",encStr)



        return arrayOf(encStr, encoded_iv)
        //return arrayOf(encStr.replace("\n",""), encoded_iv)
    }

    fun encryptFinal(txt: String): String? {
        //UAT
        var publicKey:String = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuAYLmTNUrk/bqqjtOdkGC54/acNTVPQybWO8/YpXVOFnRt48sY5Gl+UrGh7VuA3v4JJTtQFHHT7NWzJsl3MH9SoDvQ//dgqIBiI9GQ09E1+n6+TwruEGLpjCdXsAnTaO8Eo0FbKP/0NiU5WJAxwJ/ZL2GCRj9Cv5MizlHoF6RD9NFDVdywl3WV88VVVYEzNHgBXFMVIHne7kaI4q8PwYNyByTU/h5ek6vFLpsryCWNqWnDpHVZOfn1snOFazVGB4NzYtUxrk59xxeV3b9qf0sw5JCPf/vsGF7lT7ECUQh+LQFTwsfcxe/aRoRIMK183vjFMkH1AXZFWiV69iJLqhOQIDAQAB";
        //PROD
       // val publicKey =
        //    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh2X2E8XUxZ4TAawnb3UjjodIbAXxLqY7PqscDL/9A3mT4Qn4cZCkYyz70++nfxo88AYp0Lyreg/3S3c8XgGV5IIcP915vIYaZBnUUU/IzUb/Ze/KGkHrRBFxxqd4xUmmioFF889A/hVdb5i1dlV8V0s7CC+ma0wVwkTj6Lb5c1PQYOeFwXioQPmd0FZdLRV2FL5DFEmz+Y2QwCeThQRe7nVrvFdVcXQRH+vqOluMWo0F1/1uJCizK8gfV1SL60yOEffnBoI0diGTR3H8cOPQrT8Ps7r7CG+1pzFZ9g99zvfkR1YrZ2crXIUq7osK4PuzX2PFDJBQXaDfeDcRcc+NMwIDAQAB"
        var encoded = ""
        val encrypted: ByteArray
        try {
            val publicBytes: ByteArray = android.util.Base64.decode(publicKey, android.util.Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val pubKey: PublicKey = keyFactory.generatePublic(keySpec)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING") //or try with "RSA"
            cipher.init(Cipher.ENCRYPT_MODE, pubKey)
            encrypted = cipher.doFinal(txt.toByteArray())
            encoded = android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return encoded.replace("\n", "")
    }

    override fun onResume() {
        super.onResume()
        binding.mpin.text = "".toEditable()
    }

}