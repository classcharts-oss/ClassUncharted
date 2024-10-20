package stupidrepo.classuncharted.managers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.Dispatcher
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import stupidrepo.classuncharted.JSON
import stupidrepo.classuncharted.setContentLoading
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

    fun POST(endpoint: String, body: FormBody, withAuth: Boolean = false) : JsonElement {
        val request = Request.Builder()
            .url(API_URL + endpoint)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", "application/json")
            .header("User-Agent", USER_AGENT)
            .header("Authorization", if(withAuth) "Basic ${LoginManager.user?.session_id}" else "")
            .post(body)
            .build()

        val response = DoRequest(request)

        if(response.jsonObject.containsKey("error")) {
            throw Exception("POST failed! ClassCharts services responded with the following message: ${response.jsonObject["error"]}")
        }

        return response
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

        if(response.jsonObject.containsKey("error")) {
            throw Exception("ClassCharts services responded with the following message: ${response.jsonObject["error"]}")
        }

        val json: List<T> = JSON.decodeFromString<List<T>>(response.jsonObject["data"].toString())

        return json
    }

    inline fun <reified T> GET(endpoint: String, pathParameters: List<String>) : List<T> {
        return GET(endpoint + "?" + pathParameters.joinToString("&"))
    }

    fun DoRequest(request: Request): JsonElement {
        setContentLoading(true)

        return runBlocking(Dispatchers.IO) {
            try {
                val response = OKHTTP_CLIENT.newCall(request).execute()

                if (response.code != 200) {
                    throw Exception("APIManager: Request failed with code ${response.code}")
                }

                val body = response.body.string()
                val json = JSON.parseToJsonElement(body)

                response.close()

                return@runBlocking json
            } finally {
                setContentLoading(false)
            }
        }
    }
}