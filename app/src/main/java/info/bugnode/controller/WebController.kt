package info.bugnode.controller

import info.bugnode.model.Url
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class WebController {
  companion object {
    val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
    val request = Request.Builder()

    fun declareRequest(targetUrl: String, token: String) {
      request.url(Url.web(targetUrl))
      if (token.isNotEmpty()) {
        request.addHeader("Authorization", "Bearer $token")
      }
      request.addHeader("Access-Control-Allow-Origin", "*")
      request.addHeader("X-Requested-With", "XMLHttpRequest")
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun responseHandler(jsonObject: JSONObject): JSONObject {
      return when {
        jsonObject.toString().contains("errors") -> {
          JSONObject().put("code", 500).put("data", jsonObject.getJSONObject("errors").getJSONArray(jsonObject.getJSONObject("errors").names()[0].toString())[0])
        }
        jsonObject.toString().contains("message") -> {
          JSONObject().put("code", 500).put("data", jsonObject.getString("message"))
        }
        else -> {
          JSONObject().put("code", 500).put("data", jsonObject)
        }
      }
    }
  }

  class Post(private var targetUrl: String, private var token: String, private var bodyValue: FormBody.Builder) : Callable<JSONObject> {
    override fun call(): JSONObject {
      return try {
        declareRequest(targetUrl, token)
        request.post(bodyValue.build())
        val response: Response = client.newCall(request.build()).execute()
        val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
        val inputData: String = input.readLine()
        val convertJSON = JSONObject(inputData)
        return when {
          response.isSuccessful -> {
            JSONObject().put("code", 200).put("data", convertJSON)
          }
          else -> {
            responseHandler(convertJSON)
          }
        }
      } catch (e: Exception) {
        JSONObject().put("code", 500).put("data", e.message)
      }
    }
  }

  class Get(private var targetUrl: String, private var token: String) : Callable<JSONObject> {
    override fun call(): JSONObject {
      return try {
        declareRequest(targetUrl, token)
        request.method("GET", null)
        val response = client.newCall(request.build()).execute()
        val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
        val inputData: String = input.readLine()
        val convertJSON = JSONObject(inputData)
        if (response.isSuccessful) {
          when {
            convertJSON.toString().contains("message") -> {
              JSONObject().put("code", 200).put("data", convertJSON.getString("message"))
            }
            else -> {
              JSONObject().put("code", 200).put("data", convertJSON)
            }
          }
        } else {
          responseHandler(convertJSON)
        }
      } catch (e: Exception) {
        JSONObject().put("code", 500).put("data", e.message)
      }
    }
  }

  class WebView(private var targetUrl: String, private var token: String, private var bodyValue: FormBody.Builder) : Callable<JSONObject> {
    override fun call(): JSONObject {
      return try {
        declareRequest(targetUrl, token)
        request.post(bodyValue.build())
        val response: Response = client.newCall(request.build()).execute()
        val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
        return JSONObject().put("code", 200).put("data", input.readText())
      } catch (e: Exception) {
        JSONObject().put("code", 500).put("data", e.message)
      }
    }
  }
}