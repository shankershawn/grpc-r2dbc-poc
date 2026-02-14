package com.shankarsan.grpc_r2dbc_poc.client;

import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class UserServiceClientConfiguration {

  @Bean
  public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(GrpcChannelFactory channelFactory) {
    return UserServiceGrpc.newBlockingStub(channelFactory.createChannel("local"));
  }

  @Bean
  public UserServiceGrpc.UserServiceFutureStub userServiceFutureStub(GrpcChannelFactory channelFactory) {
    return UserServiceGrpc.newFutureStub(channelFactory.createChannel("local"));
  }
}
