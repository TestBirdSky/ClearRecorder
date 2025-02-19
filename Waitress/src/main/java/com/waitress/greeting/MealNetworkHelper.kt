package com.waitress.greeting

import android.app.Application
import android.os.Build
import android.util.Base64
import com.anythink.core.api.ATAdInfo
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import kotlin.random.Random

/**
 * Date：2024/11/11
 * Describe:
 */
class MealNetworkHelper {
    // todo del
    private val urlStrConfigure =
        if (IS_TEST) "https://record.trackeasyrecording.com/apitest/record/clear/"
        else "https://record.trackeasyrecording.com/api/record/clear/"

    // todo del
    private val url = if (IS_TEST) "https://test-powerful.trackeasyrecording.com/retinal/edwina"
    else "https://powerful.trackeasyrecording.com/pogo/cavalier/hager"
    private val mIoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mOkHttpClient = OkHttpClient()
    private val mustName = "getadmin^jumpfail-getlimit"
    private var isPostReferrer by HostStrCacheImpl(def = "false")
    lateinit var mApplication: Application

    fun postMealAdValue(tp: ATAdInfo) {
        val body = getMealBody().apply {
            put("thieves", JSONObject().apply {
                put("audience", tp.publisherRevenue * 1000000)
                put("shu", tp.currency)
                put("too", tp.networkName)
                put("tepee", "topon")
                put("lemonade", tp.placementId)
                put("flyer", "recorder_i")
                put("podge", tp.format)
            })
        }
        val req = strToRequest1(body.toString())
        requestNetwork(req)
    }

    private fun getMealBody(isInstall: Boolean = true): JSONObject {
        return JSONObject().apply {
            put("grievous", MenuHelper.mApp.packageName)
            put("chiang", MenuHelper.mAndroidStr)
            put("parquet", Build.BRAND)
            put("airmen", "_")
            put("plague", if (isInstall) Build.MANUFACTURER else "")
            put("shoddy", "")
            put("hoboken", MenuHelper.mDishBean.versionName)
            put("leverage", "glib")
            put("forage", System.currentTimeMillis())
            put("cactus", MenuHelper.mAndroidStr)
            put("faulkner", if (isInstall) Build.MODEL else "")
            put("testify", UUID.randomUUID().toString())
            put("oppose", Build.VERSION.RELEASE)
        }
    }

    fun postList(list: List<String>) {
        if (MenuHelper.mDishBean.waitressStatus.contains("Barista")) {
            MenuHelper.log("cancel postEvent--->$list")
            return
        }
        MenuHelper.log(" postEvent--->$list")
        val jsArray = JSONArray()
        list.forEach {
            jsArray.put(getMealBody().apply {
                put("plight", it)
            })
        }
        val req = strToRequest1(jsArray.toString())
        requestNetwork(req)

    }

    fun postEvent(name: String, pair: Pair<String, String>? = null) {
        if (MenuHelper.mDishBean.waitressStatus.contains("Barista") && mustName.contains(name)
                .not()
        ) {
            MenuHelper.log("cancel postEvent--->$name")
            return
        }
        MenuHelper.log("postEvent--->$name --$pair")

        val body = getMealBody().apply {
            put("plight", name)
            pair?.let {
                put(name, JSONObject().apply {
                    put(it.first, it.second)
                })
            }
        }
        val req = strToRequest1(body.toString())

        requestNetwork(req)
    }

    fun postReferrer(ref: String) {
        if (isPostReferrer == "true") return
        val body = getMealBody(true).apply {
            put("plight", "saran")
            put("collude", "build/")
            put("haas", ref)
            put("tasting", "")
            put("suffuse", "caravan")
            put("pompey", 0L)
            put("assignee", 0L)
            put("lange", 0L)
            put("freeway", 0L)
            put("winnow", MenuHelper.mDishBean.installTime)
            put("un", 0L)
            put("rainbow", false)
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

    fun fetchConfigure(ref: String, success: (result: String) -> Unit = {}, failed: () -> Unit) {
        val con = JSONObject().apply {
            put("dnjZP", "com.track.trackeasyrecording.efficient")
            put("ozX", MenuHelper.mDishBean.versionName)
            put("zIsdJfQ", MenuHelper.mAndroidStr)
            put("GZvthS", MenuHelper.mAndroidStr)
            put("CeppLpcyS", ref)
            put("MwTdPnU", "")
        }
        val time = System.currentTimeMillis().toString()
        val str = con.toString().mapIndexed { index, c ->
            (c.code xor time[index % 13].code).toChar()
        }.joinToString("")

        val res = Base64.encodeToString(str.toByteArray(), Base64.DEFAULT)

        requestConfigure(strToRequest2(res, time), success = success, failed = {
            failed.invoke()
        })
    }

    private fun strToRequest1(string: String): Request {
        return Request.Builder().post(string.toRequestBody("application/json".toMediaType()))
            .addHeader("plague", "").url("$url?lindsay=").build()
    }

    private fun strToRequest2(string: String, stringHeader: String): Request {
        return Request.Builder().post(string.toRequestBody("application/json".toMediaType()))
            .url(urlStrConfigure).addHeader("datetime", stringHeader).build()
    }

    private fun requestConfigure(
        request: Request, retryNum: Int = 3, failed: () -> Unit, success: (result: String) -> Unit
    ) {
        postEvent("reqadmin")
        mOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (retryNum > 0) {
                    mIoScope.launch {
                        postEvent("getadmin", Pair("getstring", "error network"))
                        delay(60000)
                        requestConfigure(request, retryNum - 1, failed, success)
                    }
                } else {
                    postEvent("getadmin", Pair("getstring", "timeout"))
                    failed.invoke()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: ""
                MenuHelper.log("requestConfigure--->$body --${response.code}")
                if (response.code == 200) {
                    val str = response.headers["datetime"] ?: ""
                    val result = setBody(body, str)
                    if (result.isBlank()) {
                        postEvent("getadmin", Pair("getstring", "null"))
                        failed.invoke()
                    } else {
                        success.invoke(result)
                    }
                } else {
                    if (retryNum > 0) {
                        mIoScope.launch {
                            postEvent("getadmin", Pair("getstring", "${response.code}"))
                            delay(60000)
                            requestConfigure(request, retryNum - 1, failed, success)
                        }
                    } else {
                        postEvent("getadmin", Pair("getstring", "timeout"))
                        failed.invoke()
                    }
                }
            }
        })
    }

    private fun setBody(body: String, headStr: String): String {
        runCatching {
            if (headStr.isBlank()) return ""
            val leg = headStr.length
            val str = String(Base64.decode(body, Base64.DEFAULT))
            val s = str.mapIndexed { index, c ->
                (c.code xor headStr[index % leg].code).toChar()
            }.joinToString("")
            val reslut = JSONObject(s).optJSONObject("hGtVmGMg")?.optString("conf") ?: ""
            return reslut
        }
        return ""
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