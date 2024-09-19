package stupidrepo.classuncharted.managers

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import stupidrepo.classuncharted.utils.JSONUtils
import java.time.Duration


object APIManager {
    const val USER_AGENT: String = "ClassUncharted-Networking/1.0.0 (Not a bot, check me out on GitHub: github.com/StupidRepo/ClassUncharted)"

    const val BASE_URL: String = "https://www.classcharts.com/"
    const val API_URL: String = BASE_URL + "apiv2student/"

    const val TAG = "APIManager"

    val TIMEOUT = Duration.ofSeconds(12)

    private val OKHTTP_CLIENT: OkHttpClient = OkHttpClient.Builder()
        .dispatcher(Dispatcher().apply {
            maxRequests = 1
            maxRequestsPerHost = 1
        })
        .connectTimeout(TIMEOUT)
        .readTimeout(TIMEOUT)
        .writeTimeout(TIMEOUT)
        .build()

    suspend fun POST(endpoint: String, body: FormBody) : JsonObject? {
        val request = Request.Builder()
            .url(API_URL + endpoint)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", "application/json")
            .header("User-Agent", USER_AGENT)
            .post(body)
            .build()

        return DoRequest(request)
    }

    inline fun <reified T> GET(endpoint: String) : List<T> {
        val request = Request.Builder()
            .url(API_URL + endpoint)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", "application/json")
            .header("User-Agent", USER_AGENT)
            .header("Authorization", "Basic ${LoginManager.user?.session_id}")
            .build()

        val response = DoRequest(request)
        val type = TypeToken.getParameterized(List::class.java, T::class.java).type

        val json: List<T>? = JSONUtils.gson.fromJson(
            response?.get("data"),
            type
        )

        if (response != null && json != null)
            return json

        return listOf()
    }

    inline fun <reified T> GET(endpoint: String, pathParameters: List<String>) : List<T> {
        return GET(endpoint + "?" + pathParameters.joinToString("&"))
    }

    fun DoRequest(request: Request): JsonObject? {
        return runBlocking(Dispatchers.IO) {
            val response = OKHTTP_CLIENT.newCall(request).execute()

            if (response.code != 200) {
                throw Exception("APIManager: Request failed with code ${response.code}")
            }

            val body = response.body.string()
            val json = JsonParser.parseString(body).asJsonObject

            response.close()

            return@runBlocking json
        }
    }
}