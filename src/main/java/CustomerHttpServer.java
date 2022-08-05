import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class CustomerHttpServer {

    static Logger logger = LoggerFactory.getLogger(CustomerHttpServer.class);

    public static void main(String[] args) throws IOException {
        int port = 8093;
        AtomicInteger num = new AtomicInteger(20);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(24));
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                logger.info(" ==== 接收到下载请求 ===");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                exchange.sendResponseHeaders(200, 0);
                OutputStream responseBody = exchange.getResponseBody();
                String responseStr = "success! " + num.getAndDecrement();
                responseBody.write(responseStr.getBytes(StandardCharsets.UTF_8));
                logger.info(" ==== 下载成功, 返回数据 : {}", responseStr);
                responseBody.flush();
                responseBody.close();
            }
        });
        logger.info(" ==== server started port : {}", port);
        server.start();
    }
}
