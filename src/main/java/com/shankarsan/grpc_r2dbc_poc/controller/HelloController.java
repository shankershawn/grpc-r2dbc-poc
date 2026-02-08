package com.shankarsan.grpc_r2dbc_poc.controller;

import com.google.common.util.concurrent.ListenableFuture;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloRequest;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloResponse;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/v1/api/hello")
@RequiredArgsConstructor
@Slf4j
public class HelloController {

  private final HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub;

  private final HelloServiceGrpc.HelloServiceFutureStub helloServiceFutureStub;

  @GetMapping("synchronous")
  public ResponseEntity<String> sayHelloSync(@RequestParam(defaultValue = "World") String name) {
    HelloResponse helloResponse = helloServiceBlockingStub.sayHello(HelloRequest.newBuilder().setName(name).build());
    return ResponseEntity.of(Optional.of(helloResponse).map(HelloResponse::getMessage));
  }

  @GetMapping("asynchronous")
  public DeferredResult<String> sayHelloAsync(@RequestParam(defaultValue = "World") String name) {
    log.info("Request for sayHelloAsync received with name: {}", name);
    DeferredResult<String> deferredResult = new DeferredResult<>();
    ListenableFuture<HelloResponse> helloResponse =
        helloServiceFutureStub.sayHello(HelloRequest.newBuilder().setName(name).build());
    helloResponse.addListener(() -> {
      try {
        HelloResponse response = helloResponse.get();
        log.info("Setting result in deferredResult");
        deferredResult.setResult(response.getMessage());
      } catch (InterruptedException | ExecutionException e) {
        deferredResult.setErrorResult(e);
        log.error("InterruptedException | ExecutionException encountered. Interrupting thread", e);
        Thread.currentThread().interrupt();
      }
    }, Runnable::run);
    log.info("Returning deferredResult");
    return deferredResult;
  }

}
