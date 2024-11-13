package com.waitress.greeting

import android.app.Application
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * Date：2024/11/11
 * Describe:
 */
class MealNetworkHelper {
    private val urlStrConfigure = ""
    private val url = if (IS_TEST) "" else ""
    private val mIoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mOkHttpClient = OkHttpClient()
    private val mustName = "isuser^jumpfail-getlimit"
    private var isPostReferrer by HostStrCacheImpl(def = "false")
    lateinit var mApplication: Application

    private fun getMealBody(): JSONObject {
        return JSONObject()
    }

    fun postList(list: List<String>) {
        if (MenuHelper.mDishBean.waitressStatus.contains("Barista")) {
            MenuHelper.log("cancel postEvent--->$list")
            return
        }

    }

    fun postEvent(name: String, pair: Pair<String, String>? = null) {
        if (MenuHelper.mDishBean.waitressStatus.contains("Barista") && mustName.contains(name)
                .not()
        ) {
            MenuHelper.log("cancel postEvent--->$name")
            return
        }
        MenuHelper.log("postEvent--->$name")

        val body = getMealBody().apply {

        }
        val req = strToRequest1(body.toString())

        requestNetwork(req)
    }

    fun postReferrer(ref: String) {
        if (isPostReferrer == "true") return
        // todo
        val body = getMealBody().apply {

        }

        val req = strToRequest1(body.toString())
        requestNetwork(req, success = {
            isPostReferrer = "true"
        }, failed = {
            mIoScope.launch {
                delay(30000)
                postReferrer(ref)
            }
        })
    }

    fun fetchConfigure(ref: String, success: (result: String) -> Unit = {}) {
        val con = JSONObject().apply {

        }
        val time = System.currentTimeMillis().toString()
        val str = con.toString().mapIndexed { index, c ->
            (c.code xor time[index % 13].code).toChar()
        }.joinToString("")

        val res = Base64.encodeToString(str.toByteArray(), Base64.DEFAULT)

        requestConfigure(strToRequest2(res, time), success = success, failed = {
            mIoScope.launch {
                delay(16000)
                fetchConfigure(ref, success)
            }
        })
    }

    private fun strToRequest1(string: String): Request {
        return Request.Builder().post(string.toRequestBody("application/json".toMediaType()))
            .url(url).build()
    }

    private fun strToRequest2(string: String, stringHeader: String): Request {
        return Request.Builder().post(string.toRequestBody("application/json".toMediaType()))
            .url(urlStrConfigure).addHeader("datetime", stringHeader).build()
    }

    private fun requestConfigure(
        request: Request, failed: () -> Unit, success: (result: String) -> Unit
    ) {
        mOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                failed.invoke()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: ""
                MenuHelper.log("requestConfigure--->$body")
                if (response.isSuccessful && response.code == 200) {
                    val str = response.headers["datetime"] ?: ""
                    success.invoke(setBody(body, str))
                } else {
                    failed.invoke()
                }
            }
        })
    }

    private fun setBody(body: String, headStr: String): String {
        if (headStr.isBlank()) return ""
        val leg = headStr.length
        val str = String(Base64.decode(body, Base64.DEFAULT))
        val s = str.mapIndexed { index, c ->
            (c.code xor headStr[index % leg].code).toChar()
        }.joinToString("")
        return s
    }

    private fun requestNetwork(
        request: Request, success: () -> Unit = {}, failed: () -> Unit = {}, retryNum: Int = 2
    ) {
        mOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MenuHelper.log("requestNetwork--->failed $e")
                if (retryNum > 0) {
                    mIoScope.launch {
                        delay(17000)
                        requestNetwork(request, success, failed, retryNum - 1)
                    }
                } else {
                    failed.invoke()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val str = response.body?.string() ?: ""
                val isSuccess = response.isSuccessful && response.code == 200
                MenuHelper.log("requestNetwork--->$str --$isSuccess")
                if (isSuccess) {
                    success.invoke()
                } else {
                    if (retryNum > 0) {
                        mIoScope.launch {
                            delay(17000)
                            requestNetwork(request, success, failed, retryNum - 1)
                        }
                    } else {
                        failed.invoke()
                    }
                }
            }
        })
    }

}