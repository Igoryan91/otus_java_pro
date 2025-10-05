package ru.otus.protobuf.service;

import io.grpc.stub.StreamObserver;
import ru.otus.protobuf.NumberServiceGrpc.NumberServiceImplBase;
import ru.otus.protobuf.RequestMessage;
import ru.otus.protobuf.ResponseMessage;

@SuppressWarnings({"squid:S2142", "squid:S106"})
public class NumberServiceImpl extends NumberServiceImplBase {

    @Override
    public void processNumber(RequestMessage request, StreamObserver<ResponseMessage> responseObserver) {
        long value = request.getFirstValue();
        long endValue = request.getLastValue();

        for (long i = value; i < endValue; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }

            responseObserver.onNext(
                    ResponseMessage.newBuilder().setResultValue(i + 1).build());
        }
        responseObserver.onCompleted();
    }
}
