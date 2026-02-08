package com.shankarsan.grpc_r2dbc_poc.client;

import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class HelloServiceClientConfiguration {

  @Bean
  public HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub(GrpcChannelFactory channelFactory) {
    return HelloServiceGrpc.newBlockingStub(channelFactory.createChannel("local"));
  }

  @Bean
  public HelloServiceGrpc.HelloServiceFutureStub helloServiceFutureStub(GrpcChannelFactory channelFactory) {
    return HelloServiceGrpc.newFutureStub(channelFactory.createChannel("local"));
  }
}
