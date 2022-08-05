package rxf113

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class KotlinCoroutine {

    suspend fun test() = coroutineScope {
        repeat(20) {
            launch {
                Test.reqAndCountDown()
            }
        }

    }
}