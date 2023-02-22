package com.nexxo.nsdlsdk.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getSystemService
import com.android.volley.*
import com.google.gson.Gson
import com.nexxo.nsdlsdk.R
import kotlinx.coroutines.CoroutineExceptionHandler
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class Utility {

    companion object {
        fun logData(logData: String) {
          //  Log.v("logData", logData);
        }

        fun ParseDouble(strNumber: String?): Double {
            return if (strNumber != null && strNumber.length > 0) ({
                try {
                    strNumber.toDouble()
                } catch (e: java.lang.Exception) {
                    0 // or some value to mark this field is wrong. or make a function validates field first ...
                }
            }) as Double else 0.0
        }


    fun onErrorCoroutineExceptionHandler(mContext: Context): CoroutineExceptionHandler {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            onErrorResponse(throwable, mContext)
        }
        return exceptionHandler
    }

        fun printRetrofitResponseBodyData(response: Response<*>) {
            try {
                var responseData = ""
                responseData = Gson().toJson(response.body())
                logData("request url \n" + response.raw().request.url)
                logData("response\n$responseData")
                responseData = ""
                responseData = Gson().toJson(response.raw().request.body)
                logData("requestBody\n$responseData")
                responseData = ""
                responseData = Gson().toJson(response.raw().request.url)
                logData("raw.request.url\n$responseData")
                responseData = ""
                responseData = Gson().toJson(response.raw().body)
                logData("\nAPI raw.body\n$responseData")
            } catch (er: Exception) {
                logData(
                    """
                API responseData Exception
                ${er.message}
                """.trimIndent()
                )
            }
        }

        fun isNetworkAvailable(context: Context): Boolean {
            return try {
                val cm =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = Objects.requireNonNull(cm).activeNetworkInfo
                netInfo != null && netInfo.isConnected
            } catch (er: java.lang.Exception) {
                er.message?.let { logData(it) }
                false
            }
        }

    @SuppressLint("MissingPermission")
    private fun onErrorResponse(error: Throwable, mContext: Context) {
        Handler(Looper.getMainLooper()).post {
            try {
                logData("onErrorResponse " + error.message)
                if (Utility.isNetworkAvailable(mContext)) {
                    if (error is NoConnectionError) {
                        val cm = mContext
                            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        var activeNetwork: NetworkInfo? = null
                        if (cm != null) {
                            activeNetwork = cm.activeNetworkInfo
                        }
                        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
                            showPopUp(
                                mContext,
                                mContext.getString(com.nexxo.nsdlsdk.R.string.txt_Server_is_not_connected_to_internet)
                            )
                        } else {
                            showPopUp(
                                mContext,
                                mContext.getString(com.nexxo.nsdlsdk.R.string.txt_Your_device_is_not_connected_to_internet)
                            )
                        }
                    } else if (error is NetworkError || error.cause is ConnectException
                        || (error.cause!!.message != null
                                && error.cause!!.message!!.contains("connection"))
                    ) {
                        showPopUp(
                            mContext,
                            mContext.getString(R.string.txt_Your_device_is_not_connected_to_internet)
                        )
                    } else if (error.cause is MalformedURLException) {
                        showPopUp(mContext, mContext.getString(R.string.txt_Bad_Request))
                    } else if (error is ParseError || error.cause is IllegalStateException
                        || error.cause is JSONException
                        || error.cause is XmlPullParserException
                    ) {
                        showPopUp(
                            mContext,
                            mContext.getString(R.string.txt_Parse_Error_because_of_invalid_json_or_xml)
                        )
                    } else if (error.cause is OutOfMemoryError) {
                        showPopUp(
                            mContext,
                            mContext.getString(R.string.txt_Out_Of_Memory_Error)
                        )
                    } else if (error is AuthFailureError) {
                        showPopUp(
                            mContext,
                            mContext.getString(R.string.txt_server_couldnt_find_the_authenticated_request)
                        )
                    } else if (error is ServerError || error.cause is ServerError) {
                        showPopUp(
                            mContext,
                            mContext.getString(R.string.txt_Server_is_not_responding)
                        )
                    } else if (error is TimeoutError || error.cause is SocketTimeoutException
                        || error.cause is ConnectTimeoutException
                        || error.cause is SocketException
                        || (error.cause!!.message != null
                                && error.cause!!.message!!.contains("Connection timed out"))
                    ) {
                        showPopUp(
                            mContext,
                            mContext.getString(R.string.txt_Connection_timeout_error)
                        )
                    } else {
                        showPopUp(
                            mContext,
                            mContext.getString(R.string.txt_An_unknown_error_occurred)
                        )
                    }
                } else {
                    showPopUp(mContext, mContext.resources.getString(R.string.no_internet))
                }
            } catch (er: java.lang.Exception) {
                showPopUp(mContext, er.message)
                er.message?.let { logData(it) }
            }
        }
    }


        fun showAPIErrorInPopUp(
            activity: Activity,
            responseString: String,
            httpStatus: Int
        ) {
            if (!Utility.handleServerSuccessErrorStatusRetrofit(
                    activity,
                    responseString,
                    httpStatus
                )
            ) {
                showPopUpForHttpStatus(
                    activity,
                    responseString,
                    httpStatus
                )
            }
        }

        /* Method for showing the alert popup box */
        fun showPopUp(context: Context, str: String?) {
            try {
                val activity: Activity = context as Activity
                if (!activity.isFinishing) {
                    val alertDialog2 = AlertDialog.Builder(context)

                    // Setting Dialog Title
                    alertDialog2.setTitle(context.getString(R.string.app_name))
                    alertDialog2.setCancelable(false)

                    // Setting Dialog Message
                    alertDialog2.setMessage(str)

                    // Setting Positive "Yes" Btn
                    alertDialog2.setPositiveButton(
                        "OK"
                    ) { dialog, which -> dialog.dismiss() }

                    // Showing Alert Dialog
                    alertDialog2.show()

                }
            } catch (e: Exception) {
                e.message?.let { Utility.logData(it) }
            }
        }


    fun showCrossButtonDialogDismiss(
        context: Context,
        messageText: String?,
        showCrossIcon: Boolean
    ) {
        val dialog = Dialog(context)
        Objects.requireNonNull(dialog.window)
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cross_button_finish_dialogue)
        val tv_Messgaetext: AppCompatTextView = dialog.findViewById(R.id.tv_Messgaetext)
        tv_Messgaetext.text = messageText
        val btn_cross: ImageView = dialog.findViewById(R.id.btn_cross)
        if (showCrossIcon) {
            btn_cross.visibility = View.VISIBLE
        } else
            btn_cross.visibility = View.GONE
        dialog.show()
        btn_cross.setOnClickListener { // Close dialog
            dialog.dismiss()

        }
    }


        fun handleServerSuccessErrorStatusRetrofit(
            context: Context,
            error: String,
            responseCode: Int
        ): Boolean {
            var statusCode = 0
            val returnStatusCode = false
            var statusMessage = ""
            var strMessage = ""
            return try {
                val `object` = JSONObject(error)
                statusCode = `object`.optInt(Constants.httpStatus_WS, responseCode)
                if (statusCode == 400) {
                    Utility.showOneButtonDialogDismiss(
                        context as Activity,
                        context.getString(R.string.txt_Error),
                        Utility.createStringFromJsonString(
                            context, `object`.toString()
                        ),
                        context.getString(R.string.ok)
                    )
                    true
                } else if (statusCode == 401) {
                    Utility.createStringFromJsonString(
                        context as Activity, `object`.toString()
                    )?.let {
                        Utility.showOneButtonDialogDismissGotoLogin(
                            context as Activity,
                            context.getString(R.string.txt_Error),
                            it,
                            context.getString(R.string.ok)
                        )
                    }
                    true
                } else if (statusCode != 0) {
                    Utility.logData("data response -- > $error")
                    statusMessage = `object`.optString(Constants.statusMessage_WS, "")
                    strMessage = `object`.optString(Constants.message_WS, "")
                    val errorShow = `object`.optBoolean("error", false)
                    if (TextUtils.isEmpty(error)) {
                        returnStatusCode
                    } else {
                        if (!TextUtils.isEmpty(strMessage)) {
                            Utility.showPopUpForHttpStatus(
                                context,
                                strMessage,
                                statusCode
                            )
                            true
                        } else if (!TextUtils.isEmpty(statusMessage)) {
                            Utility.showPopUpForHttpStatus(
                                context,
                                statusMessage,
                                statusCode
                            )
                            true
                        } else if (!TextUtils.isEmpty(error)) {
                            Utility.showPopUpForHttpStatus(
                                context,
                                error, statusCode
                            )
                            true
                        } else if (errorShow) {
                            Utility.showPopUpForHttpStatus(
                                context,
                                error, statusCode
                            )
                            true
                        } else {
                            returnStatusCode
                        }
                    }
                } else {
                    Utility.showOneButtonDialogDismiss(
                        context as Activity,
                        context.getString(R.string.txt_Error),
                        Utility.createStringFromJsonString(
                            context, `object`.toString()
                        ),
                        context.getString(R.string.ok)
                    )
                    returnStatusCode
                }
            } catch (er: java.lang.Exception) {
                er.message?.let { logData(it) }
                returnStatusCode
            }
        }

        /* Method for showing the Alert popup */
        fun showAlertPopUpAndFinish(context: Context, string: String?) {
            val alertDialog = AlertDialog.Builder(context, R.style.MyDialogTheme)
            alertDialog.setTitle(context.getString(R.string.app_name))
            alertDialog.setMessage(string)
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton(
                context.getString(R.string.ok)
            ) { dialog, which ->
                dialog.dismiss()

                    (context as Activity).finish()

            }
            alertDialog.show()
        }


        fun createHashMapFromJsonString(json: String?): HashMap<String, Any>? {
            val map2 = HashMap<String, Any>()
            return try {
                val jsonObject = JSONObject(json)
                val it = jsonObject.keys()
                while (it.hasNext()) {
                    val key = it.next()
                    map2[key] = jsonObject[key]
                }
                val sb = java.lang.StringBuilder()
                for (key in map2.keys) {
                    Log.i("JsonTest", key + ": " + map2[key])
                    if (map2[key] is JSONArray) {
                        val jsonArray = JSONArray(map2[key].toString())
                        for (j in 0 until jsonArray.length()) {
                            Log.v("result--", jsonArray.getString(j))
                            sb.append(
                                """
                                      ${jsonArray.getString(j).trim { it <= ' ' }}
                                      
                                      """.trimIndent()
                            )
                        }
                    }
                }
                logData(sb.toString())
                map2
            } catch (er: java.lang.Exception) {
                er.message?.let { logData(it) }
                map2
            }
        }


        fun loadJSONFromAsset(activity: Activity, fileName: String?): String? {
            var json: String? = null
            json = try {
                val `is` = activity.assets.open(fileName!!)
                val size = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()
                String(buffer, StandardCharsets.UTF_8)
            } catch (ex: IOException) {
                ex.message?.let { logData(it) }
                return null
            }
            return json
        }

        fun hideKeypad(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            Objects.requireNonNull(imm).hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showMessageInToast(activity: Activity, stringMessgae: String) {
            try {
                Toast.makeText(activity, stringMessgae, Toast.LENGTH_SHORT).show();
            } catch (e: Exception) {
                logData("showMessageInToast " + e.localizedMessage)
            }
        }


        fun showOneButtonDialogDismissGotoLogin(
            context: Context,
            headerText: String,
            messageText: String,
            btnText: String?
        ) {
            val dialog = Dialog(context)
            Objects.requireNonNull(dialog.window)
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.one_button_finsh_dialog)
            val tv_headertext = dialog.findViewById<AppCompatTextView>(R.id.tv_headertext)
            if (TextUtils.isEmpty(headerText)) {
                tv_headertext.visibility = View.GONE
                tv_headertext.text = headerText
            } else {
                tv_headertext.visibility = View.VISIBLE
                tv_headertext.text = headerText
            }
            val tv_Messgaetext = dialog.findViewById<AppCompatTextView>(R.id.tv_Messgaetext)
            tv_Messgaetext.text = messageText
            if (headerText.equals(context.getString(R.string.txt_Error), ignoreCase = true)) {
                tv_Messgaetext.gravity =
                    View.TEXT_ALIGNMENT_CENTER or View.TEXT_ALIGNMENT_TEXT_START
            }
            val btn_Ok = dialog.findViewById<AppCompatButton>(R.id.btn_Ok)
            btn_Ok.text = btnText
            dialog.show()
            btn_Ok.setOnClickListener {
                // Close dialog
                val activity = context as Activity
                dialog.dismiss()

                //                callNextActivityClearTop(activity, EnterPasscodeActivity.class);
            }
        }


        /* Method for showing the alert popup box */
        fun showPopUpForHttpStatus(context: Context, str: String, httpStatus: Int) {
            val alertDialog2 = AlertDialog.Builder(context)
            // Setting Dialog Title
            alertDialog2.setTitle(context.getString(R.string.app_name))
            alertDialog2.setCancelable(false)

            // Setting Dialog Message
            alertDialog2.setMessage(str)

            // Setting Positive "Yes" Btn
            alertDialog2.setPositiveButton(
                "OK"
            ) { dialog, which ->
                dialog.dismiss()

            }

            // Showing Alert Dialog
            alertDialog2.show()
        }


        fun createStringFromJsonString(activity: Activity, json: String?): String? {
            return try {
                val map2 = HashMap<String, Any>()
                val jsonObject = JSONObject(json)
                val it = jsonObject.keys()
                while (it.hasNext()) {
                    val key = it.next()
                    map2[key] = jsonObject[key]
                }
                val sb = StringBuilder()
                for (key in map2.keys) {
                    logData("JsonTest" + key + ": " + map2[key])
                    if (map2[key] is JSONArray) {
                        val jsonArray = JSONArray(map2[key].toString())
                        for (j in 0 until jsonArray.length()) {
                           Utility.logData(
                                """
                                     result--
                                     ${jsonArray.getString(j)}
                                     """.trimIndent()
                            )
                            //                        sb.append(String.format("%s\u25CF ", jsonArray.getString(j).trim()) + "\n");
                            sb.append(
                                """
                                     ● ${jsonArray.getString(j).trim { it <= ' ' }}
                                     
                                     """.trimIndent()
                            )
                        }
                    }
                }
                if (TextUtils.isEmpty(sb)) {
                    // Check is key exists in the Map
                    if (map2.containsKey(Constants.exp_WS)) {
                        //                    sb.append("\u25CF " + map2.get(Constants.message_WS));
                        sb.append(activity.resources.getString(R.string.txt_your_session_has_expired_please_log_in_again))
                    } else if (map2.containsKey(Constants.message_WS)) {
                        sb.append("\u25CF " + map2[Constants.message_WS])
                    } else if (map2.containsKey(Constants.statusMessage_WS)) {
                        sb.append("\u25CF " + map2[Constants.statusMessage_WS])
                    } else {
                        return json
                    }
                }
                sb.toString().trim { it <= ' ' }
            } catch (er: java.lang.Exception) {
                er.message?.let { logData(it) }
                json
            }
        }


        fun showOneButtonDialogDismiss(
            context: Context,
            headerText: String?,
            messageText: String?,
            btnText: String?
        ) {
            try {

                val dialog = Dialog(context)
                Objects.requireNonNull(dialog.window)
                    ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.one_button_finsh_dialog)
                val tv_headertext: AppCompatTextView = dialog.findViewById(R.id.tv_headertext)
                if (TextUtils.isEmpty(headerText)) {
                    tv_headertext.visibility = View.GONE
                    tv_headertext.text = headerText
                } else {
                    tv_headertext.visibility = View.VISIBLE
                    tv_headertext.text = headerText
                }
                val tv_Messgaetext: AppCompatTextView = dialog.findViewById(R.id.tv_Messgaetext)
                tv_Messgaetext.text = messageText
                val btn_Ok: AppCompatButton = dialog.findViewById(R.id.btn_Ok)
                btn_Ok.text = btnText
                dialog.show()
                btn_Ok.setOnClickListener { // Close dialog
                    val activity = context as Activity
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                logData("Exception while showOneButtonDialogDismiss - ${e.localizedMessage}")
            }
        }


        fun encryptpayload(value: String, key: String): String? {
            var encryptedData: String? = null
            val ALGORITHM = "AES/CBC/PKCS5PADDING"
            encryptedData = try {
                val iv = IvParameterSpec(key.substring(0, 16).toByteArray(charset("UTF-8")))
                val skeySpec = SecretKeySpec(key.substring(0, 32).toByteArray(charset("UTF-8")), "AES")
                val cipher = Cipher.getInstance(ALGORITHM)
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
                val encrypted = cipher.doFinal(value.toByteArray())
                android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT).toString();
               // Base64.getEncoder().encodeToString(encrypted)

            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                null
            }
            return encryptedData
        }




        fun generateSequenceNumber(): String? {
            var seqno = UUID.randomUUID().toString().replace("-", "")
            seqno = "NSD$seqno"
            return seqno
        }

        fun showKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
        }

        /* fun hideKeyboard(activity: Activity) {
             val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
             imm?.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }*/

        fun hideKeyboard(activity: Activity) {
            try {
                val inputManager = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val currentFocusedView = activity.currentFocus
                if (currentFocusedView != null) {
                    inputManager.hideSoftInputFromWindow(
                        currentFocusedView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun removeDecimal(value:String):String{
            var d:Double = value.toDouble()
            return String.format("%.0f", d)
        }




    }





}