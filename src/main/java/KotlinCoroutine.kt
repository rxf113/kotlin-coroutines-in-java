import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.nio.charset.StandardCharsets

class KotlinCoroutine {

    suspend fun test() = coroutineScope {
        repeat(5) {
            launch {
                Test.reqAndCountDown()
//                httpReq()
            }
        }
    }

    private fun httpReq() {
        val client = OkHttpClient.Builder().build()
        val get = Request.Builder()
            .url("http://localhost:8093/test")
            .get()
            .build()
        val call = client.newCall(get)
        try {
            call.execute().use { response ->
                if (response.isSuccessful) {
                    assert(response.body() != null)
                    val responseStr =
                        String(response.body()!!.bytes(), StandardCharsets.UTF_8)
                    Test.logger.info("下载到文件: {}", responseStr)
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}