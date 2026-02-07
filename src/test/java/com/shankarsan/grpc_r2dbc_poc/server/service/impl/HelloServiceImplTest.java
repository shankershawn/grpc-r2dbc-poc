package com.shankarsan.grpc_r2dbc_poc.server.service.impl;

import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloRequest;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("HelloServiceImpl Tests")
class HelloServiceImplTest {

  private HelloServiceImpl helloService;

  @Mock
  private AutoCloseable closeable;

  @Mock
  private StreamObserver<HelloResponse> responseObserver;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    helloService = new HelloServiceImpl();
  }

  @Test
  @DisplayName("should greet with provided name")
  void shouldGreetWithProvidedName() {
    HelloRequest request = HelloRequest.newBuilder().setName("Alice").build();

    helloService.sayHello(request, responseObserver);

    ArgumentCaptor<HelloResponse> responseCaptor = ArgumentCaptor.forClass(HelloResponse.class);
    verify(responseObserver).onNext(responseCaptor.capture());
    verify(responseObserver).onCompleted();

    HelloResponse response = responseCaptor.getValue();
    assertEquals("Hello, Alice!", response.getMessage());
  }

  @ParameterizedTest
  @CsvSource({
      "'', 'Hello, !'",
      "A, 'Hello, A!'",
      "'John@#$%', 'Hello, John@#$%!'",
      "José, 'Hello, José!'",
      "'John Doe', 'Hello, John Doe!'",
  })
  @DisplayName("should greet with various name inputs")
  void shouldGreetWithVariousNameInputs(String name, String expectedMessage) {
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();

    helloService.sayHello(request, responseObserver);

    ArgumentCaptor<HelloResponse> responseCaptor = ArgumentCaptor.forClass(HelloResponse.class);
    verify(responseObserver).onNext(responseCaptor.capture());
    verify(responseObserver).onCompleted();

    HelloResponse response = responseCaptor.getValue();
    assertEquals(expectedMessage, response.getMessage());
  }

  @Test
  @DisplayName("should call onCompleted after sending response")
  void shouldCallOnCompletedAfterSendingResponse() {
    HelloRequest request = HelloRequest.newBuilder().setName("Test").build();

    helloService.sayHello(request, responseObserver);

    InOrder inOrder = inOrder(responseObserver);
    inOrder.verify(responseObserver).onNext(any());
    inOrder.verify(responseObserver).onCompleted();
  }

  @Test
  @DisplayName("should not call onError on success")
  void shouldNotCallOnErrorOnSuccess() {
    HelloRequest request = HelloRequest.newBuilder().setName("Test").build();

    helloService.sayHello(request, responseObserver);

    verify(responseObserver, never()).onError(any());
  }
}

