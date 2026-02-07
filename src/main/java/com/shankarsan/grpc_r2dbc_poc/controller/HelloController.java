package com.shankarsan.grpc_r2dbc_poc.controller;

import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloRequest;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloResponse;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/api/hello")
@RequiredArgsConstructor
public class HelloController {

  private final HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub;

  @GetMapping("synchronous")
  public ResponseEntity<String> sayHello(@RequestParam(defaultValue = "World") String name) {
    HelloResponse helloResponse = helloServiceBlockingStub.sayHello(HelloRequest.newBuilder().setName(name).build());
    return ResponseEntity.of(Optional.of(helloResponse).map(HelloResponse::getMessage));
  }

}
