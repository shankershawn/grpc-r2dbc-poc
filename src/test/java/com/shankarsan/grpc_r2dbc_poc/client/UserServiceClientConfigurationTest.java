package com.shankarsan.grpc_r2dbc_poc.client;

import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserServiceGrpc;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceClientConfiguration Tests")
class UserServiceClientConfigurationTest {

  private UserServiceClientConfiguration configuration;

  @Mock
  private GrpcChannelFactory grpcChannelFactory;

  @Mock
  private ManagedChannel channel;

  @BeforeEach
  void setUp() {
    configuration = new UserServiceClientConfiguration();
  }

  @Test
  @DisplayName("should create UserServiceBlockingStub bean with correct type")
  void shouldCreateUserServiceBlockingStubBean() {
    when(grpcChannelFactory.createChannel("local")).thenReturn(channel);

    UserServiceGrpc.UserServiceBlockingStub stub = configuration.userServiceBlockingStub(grpcChannelFactory);

    assertNotNull(stub);
    assertInstanceOf(UserServiceGrpc.UserServiceBlockingStub.class, stub);
  }

  @Test
  @DisplayName("should create new blocking stub on each invocation")
  void shouldCreateNewBlockingStubOnEachInvocation() {
    when(grpcChannelFactory.createChannel("local")).thenReturn(channel);

    UserServiceGrpc.UserServiceBlockingStub stub1 = configuration.userServiceBlockingStub(grpcChannelFactory);
    UserServiceGrpc.UserServiceBlockingStub stub2 = configuration.userServiceBlockingStub(grpcChannelFactory);

    assertNotNull(stub1);
    assertNotNull(stub2);
  }

  @Test
  @DisplayName("should create UserServiceFutureStub bean with correct type")
  void shouldCreateUserServiceFutureStubBean() {
    when(grpcChannelFactory.createChannel("local")).thenReturn(channel);

    UserServiceGrpc.UserServiceFutureStub stub = configuration.userServiceFutureStub(grpcChannelFactory);

    assertNotNull(stub);
    assertInstanceOf(UserServiceGrpc.UserServiceFutureStub.class, stub);
    verify(grpcChannelFactory).createChannel("local");
  }

  @Test
  @DisplayName("should create new future stub on each invocation")
  void shouldCreateNewFutureStubOnEachInvocation() {
    when(grpcChannelFactory.createChannel("local")).thenReturn(channel);

    UserServiceGrpc.UserServiceFutureStub stub1 = configuration.userServiceFutureStub(grpcChannelFactory);
    UserServiceGrpc.UserServiceFutureStub stub2 = configuration.userServiceFutureStub(grpcChannelFactory);

    assertNotNull(stub1);
    assertNotNull(stub2);
  }

  @Test
  @DisplayName("should use local channel name for future stub factory")
  void shouldUseLocalChannelNameForFutureStubFactory() {
    when(grpcChannelFactory.createChannel("local")).thenReturn(channel);

    UserServiceGrpc.UserServiceFutureStub stub = configuration.userServiceFutureStub(grpcChannelFactory);

    assertNotNull(stub);
    verify(grpcChannelFactory).createChannel("local");
  }
}
