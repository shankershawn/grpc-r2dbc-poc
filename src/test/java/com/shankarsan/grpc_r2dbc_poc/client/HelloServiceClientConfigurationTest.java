package com.shankarsan.grpc_r2dbc_poc.client;

import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.grpc.client.GrpcChannelFactory;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HelloServiceClientConfiguration Tests")
class HelloServiceClientConfigurationTest {

  private HelloServiceClientConfiguration configuration;

  @Mock
  private GrpcChannelFactory grpcChannelFactory;

  @Mock
  private ManagedChannel channel;

  @BeforeEach
  void setUp() {
    configuration = new HelloServiceClientConfiguration();
  }

  @Test
  @DisplayName("should create HelloServiceBlockingStub bean with correct type")
  void shouldCreateHelloServiceBlockingStubBean() {
    when(grpcChannelFactory.createChannel("local")).thenReturn(channel);

    HelloServiceGrpc.HelloServiceBlockingStub stub = configuration.helloServiceBlockingStub(grpcChannelFactory);

    assertNotNull(stub);
    assertInstanceOf(HelloServiceGrpc.HelloServiceBlockingStub.class, stub);
  }

  @Test
  @DisplayName("should create new stub on each invocation")
  void shouldCreateNewStubOnEachInvocation() {
    when(grpcChannelFactory.createChannel("local")).thenReturn(channel);

    HelloServiceGrpc.HelloServiceBlockingStub stub1 = configuration.helloServiceBlockingStub(grpcChannelFactory);
    HelloServiceGrpc.HelloServiceBlockingStub stub2 = configuration.helloServiceBlockingStub(grpcChannelFactory);

    assertNotNull(stub1);
    assertNotNull(stub2);
  }
}

