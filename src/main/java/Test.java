package rxf113;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.Job;
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

public class Test {

    static Logger logger = LoggerFactory.getLogger(Test.class);


    static ExecutorService executorService = Executors.newFixedThreadPool(1);

    static CountDownLatch countDownLatch = new CountDownLatch(20);

    public static String reqAndCountDown() {
        httpReq();
        countDownLatch.countDown();
        return "t1";
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        long be = System.currentTimeMillis();
        //线程
//        executorService.execute(() -> {
//            for (int i = 0; i < 20; i++) {
//                reqAndCountDown();
//            }
//        });

        //线程调用协程
        KotlinCoroutine kotlinCoroutine = new KotlinCoroutine();
        executorService.execute(() -> {
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
        });
        countDownLatch.await();
        logger.info(" ==== over ==== , cost time : {} ms", (System.currentTimeMillis() - be));
        System.in.read();
    }


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
//        String a = "";
//        for (int i = 0; i < 10_0000; i++) {
//            a = a.concat(Integer.toString(i));
//        }
//        System.out.println(a.length());
    }
}
