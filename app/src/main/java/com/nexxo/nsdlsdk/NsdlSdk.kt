package com.nexxo.nsdlsdk

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class NsdlSdk: Application() {
    companion object {
        fun getInstance(): NsdlSdk? {
            return instance
        }

        fun getContext(): Context? {
            return instance
        }

        @SuppressLint("StaticFieldLeak")
        private var instance: NsdlSdk? = null

        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        fun getAppContext(): Context? {
            return mContext
        }

        fun setContext(mContext: Context?) {
            this.mContext = mContext
        }

        fun isActivityVisible(): Boolean {
            return activityVisible
        }

        fun activityResumed() {
            activityVisible = true
        }

        fun activityPaused() {
            activityVisible = false
        }

        private var activityVisible = false
    }


    override fun onCreate() {
        instance = this
        super.onCreate()
        /*val appSignatureHelper = AppSignatureHelper(this)
        appSignatureHelper.getAppSignatures()*/
    }
}