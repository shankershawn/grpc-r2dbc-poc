package com.shankarsan.grpc_r2dbc_poc.server.service.impl;

import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloServiceGrpc;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

  @Override
  public void sayHello(com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloRequest request,
                       io.grpc.stub.StreamObserver<com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloResponse> responseObserver) {
    String name = request.getName();
    String message = "Hello, " + name + "!";

    com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloResponse response =
        com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloResponse.newBuilder()
            .setMessage(message)
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();

  }
}