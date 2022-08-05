import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author rxf113
 */
@SuppressWarnings("all")
public class Test {

    static Logger logger = LoggerFactory.getLogger(Test.class);

    static ExecutorService executorService = Executors.newFixedThreadPool(1);

    static CountDownLatch countDownLatch = new CountDownLatch(20);

    /**
     * 20次调用后结束
     */
    public static void reqAndCountDown() {
//        httpReq();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        countDownLatch.countDown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        long be = System.currentTimeMillis();
        //线程直接调用
        //testThread();

//        //调用kotlin协程
        testCoroutine();

        countDownLatch.await();
        logger.info(" ==== 下载完成 ==== , 耗时 : {} ms", (System.currentTimeMillis() - be));
        System.in.read();
    }

    private static void testThread() {
        executorService.execute(() -> {
            for (int i = 0; i < 20; i++) {
                reqAndCountDown();
            }
        });
    }

    /**
     * 调用kotlin协程
     */
    private static void testCoroutine() {
        KotlinCoroutine kotlinCoroutine = new KotlinCoroutine();
        executorService.execute(() -> {
            for (int i = 0; i < 4; i++) {
                kotlinCoroutine.test(new Continuation<Unit>() {
                    @NotNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NotNull Object o) {

                    }
                });
            }
        });
    }

    /**
     * http请求
     */
    private static void httpReq() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request get = new Request.Builder()
                .url("http://localhost:8093/test")
                .get()
                .build();
        Call call = client.newCall(get);
        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseStr = new String(response.body().bytes(), StandardCharsets.UTF_8);
                logger.info("下载到文件: {}", responseStr);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
