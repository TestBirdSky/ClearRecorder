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
    private val urlStrConfigure = "https://voiapi.clearsoundvoice.com/api/recorder/clear/"

    // todo del
    private val url = if (IS_TEST) "https://test-jurassic.clearsoundvoice.com/sinter/hera/wrestle"
    else "https://jurassic.clearsoundvoice.com/bismarck/seedy/id"
    private val mIoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mOkHttpClient = OkHttpClient()
    private val mustName = "isuser^jumpfail-getlimit"
    private var isPostReferrer by HostStrCacheImpl(def = "false")
    lateinit var mApplication: Application

    fun postMealAdValue(tp: ATAdInfo) {
        val body = getMealBody().apply {
            put("winy", JSONObject().apply {
                put("credit", tp.publisherRevenue * 1000000)
                put("abode", tp.currency)
                put("include", tp.networkName)
                put("kinky", "topon")
                put("drably", tp.placementId)
                put("rheostat", "recorder_i")
                put("malay", tp.format)
            })
        }
        val req = strToRequest1(body.toString())
        requestNetwork(req)
    }

    private fun getMealBody(isInstall: Boolean = false): JSONObject {
        return JSONObject().apply {
            put("riddle", MenuHelper.mAndroidStr)
            put("floodlit", MenuHelper.mAndroidStr)
            put("floc", if (isInstall) Build.MANUFACTURER else "")
            put("panicked", if (isInstall) Build.MODEL else "")
            put("cannel", "")
            put("winfield", MenuHelper.mDishBean.versionName)
            put("karp", "bannock")
            put("penny", "")
            put("pinxter", "_")
            put("belch", Build.VERSION.RELEASE)
            put("unite", System.currentTimeMillis())
            put("abigail", MenuHelper.mApp.packageName)
            put("precise", UUID.randomUUID().toString())

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
                put("dietetic", it)
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
        MenuHelper.log("postEvent--->$name")

        val body = getMealBody().apply {
            put("dietetic", name)
            pair?.let {
                put("${it.first}>mantrap", it.second)
            }
        }
        val req = strToRequest1(body.toString())

        requestNetwork(req)
    }

    fun postReferrer(ref: String) {
        if (isPostReferrer == "true") return
        val body = getMealBody(true).apply {
            put("ingram", JSONObject().apply {
                put("eucre", "build/")
                put("lorinda", ref)
                put("teasel", "")
                put("beard", "")
                put("apposite", "juan")
                put("ts", 0L)
                put("british", 0L)
                put("bronze", 0L)
                put("admix", 0L)
                put("sheppard", 0L)
                put("pleiades", 0L)
                put("rainbow", false)
            })
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
            put("MpIBlrDsLh", "com.clear.sound.voice.recorder.management")
            put("huPibK", MenuHelper.mDishBean.versionName)
            put("SgEMmMDzRT", MenuHelper.mAndroidStr)
            put("HpxIEhJvYu", MenuHelper.mAndroidStr)
            put("yjatP", ref)
            put("vStvNo", "")
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
            .addHeader("penny", "").url("$url?karp=bannock").build()
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
                MenuHelper.log("requestConfigure--->$body --${response.code}")
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
        runCatching {
            if (headStr.isBlank()) return ""
            val leg = headStr.length
            val str = String(Base64.decode(body, Base64.DEFAULT))
            val s = str.mapIndexed { index, c ->
                (c.code xor headStr[index % leg].code).toChar()
            }.joinToString("")
            val reslut = JSONObject(s).optJSONObject("AsqF")?.optString("conf") ?: ""
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