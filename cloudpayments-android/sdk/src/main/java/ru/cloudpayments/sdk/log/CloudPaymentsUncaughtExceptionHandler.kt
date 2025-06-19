package ru.cloudpayments.sdk.log

import android.app.Activity
import android.util.Log

class CloudPaymentsUncaughtExceptionHandler private constructor(val activity: Activity) : Thread.UncaughtExceptionHandler {

    companion object {

        @Volatile private var INSTANCE: CloudPaymentsUncaughtExceptionHandler? = null

        fun getInstance(activity: Activity): CloudPaymentsUncaughtExceptionHandler {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CloudPaymentsUncaughtExceptionHandler(activity).also {
                    INSTANCE = it
                }
            }
        }
    }

    override fun uncaughtException(thread: Thread, e: Throwable) {

        val message = e.message.toString()
        val stackTrace = e.stackTraceToString()

        Log.e("CP_SDK_ERROR_MESSAGE", message)
        Log.e("CP_SDK_ERROR_STACK_TRACE", stackTrace)

        CloudPaymentsSendLogHttpClient.sendErrorLog(message, stackTrace)
        Thread.sleep(4000)

        activity.finish()
    }
}