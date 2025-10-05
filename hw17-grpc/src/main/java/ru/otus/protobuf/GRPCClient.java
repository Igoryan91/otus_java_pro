package ru.otus.protobuf;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"squid:S106", "squid:S2142"})
public class GRPCClient {

    private static final Logger log = LoggerFactory.getLogger(GRPCClient.class.getName());

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;

    private static final int START = 0;
    private static final int END = 50;

    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        var stub = NumberServiceGrpc.newStub(channel);
        var reqNumberMsg =
                RequestMessage.newBuilder().setFirstValue(0).setLastValue(30).build();

        var latch = new CountDownLatch(1);
        AtomicLong serverValue = new AtomicLong(0);

        stub.processNumber(reqNumberMsg, new StreamObserver<ResponseMessage>() {
            @Override
            public void onNext(ResponseMessage respNumberMsg) {
                serverValue.set(respNumberMsg.getResultValue());
                log.info("serverValue: {}", respNumberMsg.getResultValue());
            }

            @Override
            public void onError(Throwable t) {
                log.info("Провал: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("Я все!");
                latch.countDown();
            }
        });

        long currentValue = 0;

        for (int i = START; i <= END; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }

            currentValue = currentValue + serverValue.getAndSet(0) + 1;
            log.info("currentValue: {}", currentValue);
        }

        latch.await();

        channel.shutdown();
    }
}
