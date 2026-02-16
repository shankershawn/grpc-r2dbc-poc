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
    when(grpcChannelFactory.createChannel(UserServiceClientConfiguration.LOCAL)).thenReturn(channel);

    UserServiceGrpc.UserServiceBlockingStub stub = configuration.userServiceBlockingStub(grpcChannelFactory);

    assertNotNull(stub);
    assertInstanceOf(UserServiceGrpc.UserServiceBlockingStub.class, stub);
    verify(grpcChannelFactory).createChannel(UserServiceClientConfiguration.LOCAL);
  }

  @Test
  @DisplayName("should create UserServiceFutureStub bean with correct type")
  void shouldCreateUserServiceFutureStubBean() {
    when(grpcChannelFactory.createChannel(UserServiceClientConfiguration.LOCAL)).thenReturn(channel);

    UserServiceGrpc.UserServiceFutureStub stub = configuration.userServiceFutureStub(grpcChannelFactory);

    assertNotNull(stub);
    assertInstanceOf(UserServiceGrpc.UserServiceFutureStub.class, stub);
    verify(grpcChannelFactory).createChannel(UserServiceClientConfiguration.LOCAL);
  }

  @Test
  @DisplayName("should create UserServiceStub bean with correct type")
  void shouldCreateUserServiceStubBean() {
    when(grpcChannelFactory.createChannel(UserServiceClientConfiguration.LOCAL)).thenReturn(channel);

    UserServiceGrpc.UserServiceStub stub = configuration.userServiceStub(grpcChannelFactory);

    assertNotNull(stub);
    assertInstanceOf(UserServiceGrpc.UserServiceStub.class, stub);
    verify(grpcChannelFactory).createChannel(UserServiceClientConfiguration.LOCAL);
  }
}
