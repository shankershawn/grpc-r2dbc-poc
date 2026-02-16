package com.shankarsan.grpc_r2dbc_poc.client;

import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class UserServiceClientConfiguration {

  public static final String LOCAL = "local";

  @Bean
  public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(GrpcChannelFactory channelFactory) {
    return UserServiceGrpc.newBlockingStub(channelFactory.createChannel(LOCAL));
  }

  /* UserServiceFutureStub only supports unary RPCs. It does not support server‑streaming, client‑streaming, or bidi streaming. */
  @Bean
  public UserServiceGrpc.UserServiceFutureStub userServiceFutureStub(GrpcChannelFactory channelFactory) {
    return UserServiceGrpc.newFutureStub(channelFactory.createChannel(LOCAL));
  }

  @Bean
  public UserServiceGrpc.UserServiceStub userServiceStub(GrpcChannelFactory channelFactory) {
    return UserServiceGrpc.newStub(channelFactory.createChannel(LOCAL));
  }
}
