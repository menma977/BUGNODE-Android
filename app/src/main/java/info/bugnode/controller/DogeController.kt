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

class DogeController {
  companion object {
    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
    private val request = Request.Builder()

    fun declareRequest() {
      request.url(Url.doge())
      request.addHeader("Access-Control-Allow-Origin", "*")
      request.addHeader("X-Requested-With", "XMLHttpRequest")
      request.addHeader("Connection", "close")
      request.header("Connection", "close")
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun responseHandler(jsonObject: JSONObject): JSONObject {
      return when {
        jsonObject.toString().contains("ChanceTooHigh") -> {
          JSONObject().put("code", 500).put("data", "Chance Too High")
        }
        jsonObject.toString().contains("ChanceTooLow") -> {
          JSONObject().put("code", 500).put("data", "Chance Too Low")
        }
        jsonObject.toString().contains("InsufficientFunds") -> {
          JSONObject().put("code", 500).put("data", "Insufficient Funds")
        }
        jsonObject.toString().contains("NoPossibleProfit") -> {
          JSONObject().put("code", 500).put("data", "No Possible Profit")
        }
        jsonObject.toString().contains("MaxPayoutExceeded") -> {
          JSONObject().put("code", 500).put("data", "Max Payout Exceeded")
        }
        jsonObject.toString().contains("999doge") -> {
          JSONObject().put("code", 500).put("data", "Invalid request On Server Wait 5 minute to try again")
        }
        jsonObject.toString().contains("error") -> {
          JSONObject().put("code", 500).put("data", "Invalid request")
        }
        jsonObject.toString().contains("TooFast") -> {
          JSONObject().put("code", 500).put("data", "Too Fast")
        }
        jsonObject.toString().contains("TooSmall") -> {
          JSONObject().put("code", 500).put("data", "Too Small")
        }
        jsonObject.toString().contains("LoginRequired") -> {
          JSONObject().put("code", 500).put("data", "Login Required")
        }
        else -> {
          JSONObject().put("code", 500).put("data", "Unstable connection / Response Not found")
        }
      }
    }
  }

  class Post(private var bodyValue: FormBody.Builder) : Callable<JSONObject> {
    override fun call(): JSONObject {
      return try {
        declareRequest()
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
        JSONObject().put("code", 500).put("data", e.message.toString().replace("999doge", "WEB"))
      }
    }
  }
}