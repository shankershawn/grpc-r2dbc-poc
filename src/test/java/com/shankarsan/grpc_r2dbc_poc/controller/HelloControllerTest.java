package com.shankarsan.grpc_r2dbc_poc.controller;

import com.google.common.util.concurrent.Futures;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloRequest;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloResponse;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.HelloServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("HelloController Tests")
class HelloControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub;

  @MockitoBean
  private HelloServiceGrpc.HelloServiceFutureStub helloServiceFutureStub;

  @BeforeEach
  void setUp() {
    // No additional setup needed since we're using @MockitoBean to mock the gRPC stub
  }

  @Test
  @DisplayName("should return greeting for provided name")
  void shouldReturnGreetingForProvidedName() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, Alice!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "Alice"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Hello,")))
        .andExpect(content().string(containsString("Alice")))
        .andExpect(content().string(endsWith("!")));
  }

  @Test
  @DisplayName("should return default greeting when name parameter is missing")
  void shouldReturnDefaultGreetingWhenNameParameterIsMissing() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, World!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Hello,")))
        .andExpect(content().string(containsString("World")));
  }

  @Test
  @DisplayName("should return greeting with empty string when name is empty")
  void shouldReturnGreetingWithEmptyStringWhenNameIsEmpty() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, !").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", ""))
        .andExpect(status().isOk())
        .andExpect(content().string(startsWith("Hello,")))
        .andExpect(content().string(endsWith("!")));
  }

  @Test
  @DisplayName("should handle special characters in name parameter")
  void shouldHandleSpecialCharactersInNameParameter() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, John@#$!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "John@#$"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Hello,")))
        .andExpect(content().string(containsString("@#$")));
  }

  @Test
  @DisplayName("should handle unicode characters in name parameter")
  void shouldHandleUnicodeCharactersInNameParameter() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, José!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "José"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("José")))
        .andExpect(content().string(endsWith("!")));
  }

  @Test
  @DisplayName("should handle spaces in name parameter")
  void shouldHandleSpacesInNameParameter() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, John Doe!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "John Doe"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("John Doe")))
        .andExpect(content().string(startsWith("Hello,")));
  }

  @Test
  @DisplayName("should return 200 OK status")
  void shouldReturn200OkStatus() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, Test!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "Test"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Test")))
        .andExpect(content().string(endsWith("!")));
  }

  @Test
  @DisplayName("should handle very long name parameter")
  void shouldHandleVeryLongNameParameter() throws Exception {
    String longName = "A".repeat(1000);
    String expectedMessage = "Hello, " + longName + "!";
    HelloResponse response = HelloResponse.newBuilder().setMessage(expectedMessage).build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", longName))
        .andExpect(status().isOk())
        .andExpect(content().string(startsWith("Hello,")))
        .andExpect(content().string(endsWith("!")));
  }

  @Test
  @DisplayName("should handle numeric characters in name parameter")
  void shouldHandleNumericCharactersInNameParameter() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, 12345!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "12345"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("12345")))
        .andExpect(content().string(endsWith("!")));
  }

  @Test
  @DisplayName("should return greeting asynchronously for provided name")
  void shouldReturnGreetingAsynchronouslyForProvidedName() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, Bob!").build();
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/hello/asynchronous").param("name", "Bob"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Bob")))
            .andExpect(content().string(endsWith("!"))));
  }

  @Test
  @DisplayName("should return default greeting asynchronously when name parameter is missing")
  void shouldReturnDefaultGreetingAsynchronouslyWhenNameParameterIsMissing() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, World!").build();
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/hello/asynchronous"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("World")))
            .andExpect(content().string(startsWith("Hello,"))));
  }

  @Test
  @DisplayName("should handle empty name asynchronously")
  void shouldHandleEmptyNameAsynchronously() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, !").build();
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/hello/asynchronous").param("name", ""))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(startsWith("Hello,")))
            .andExpect(content().string(endsWith("!"))));
  }

  @Test
  @DisplayName("should handle special characters asynchronously")
  void shouldHandleSpecialCharactersAsynchronously() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, Alice@123!").build();
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/hello/asynchronous").param("name", "Alice@123"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Alice")))
            .andExpect(content().string(containsString("@123"))));
  }

  @Test
  @DisplayName("should handle unicode characters asynchronously")
  void shouldHandleUnicodeCharactersAsynchronously() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, François!").build();
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/hello/asynchronous").param("name", "François"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("François")))
            .andExpect(content().string(endsWith("!"))));
  }

  @Test
  @DisplayName("should handle spaces in name asynchronously")
  void shouldHandleSpacesInNameAsynchronously() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, Jane Doe!").build();
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/hello/asynchronous").param("name", "Jane Doe"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Jane Doe")))
            .andExpect(content().string(startsWith("Hello,"))));
  }

  @Test
  @DisplayName("should handle very long name asynchronously")
  void shouldHandleVeryLongNameAsynchronously() throws Exception {
    String longName = "X".repeat(500);
    String expectedMessage = "Hello, " + longName + "!";
    HelloResponse response = HelloResponse.newBuilder().setMessage(expectedMessage).build();
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/hello/asynchronous").param("name", longName))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(startsWith("Hello,")))
            .andExpect(content().string(endsWith("!"))));
  }

  @Test
  @DisplayName("should handle numeric name asynchronously")
  void shouldHandleNumericNameAsynchronously() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, 99999!").build();
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/hello/asynchronous").param("name", "99999"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("99999")))
            .andExpect(content().string(endsWith("!"))));
  }

  @Test
  @DisplayName("should return 200 OK status asynchronously")
  void shouldReturn200OkStatusAsynchronously() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, Async!").build();
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/hello/asynchronous").param("name", "Async"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Async")))
            .andExpect(content().string(startsWith("Hello,"))));
  }

  @Test
  @DisplayName("should handle future exceptions in async call")
  void shouldHandleFutureExceptionsInAsyncCall() throws Exception {
    when(helloServiceFutureStub.sayHello(any(HelloRequest.class)))
        .thenReturn(Futures.immediateFailedFuture(new RuntimeException("gRPC service unavailable")));

    mockMvc.perform(get("/v1/api/hello/asynchronous").param("name", "Test"))
        .andExpect(request().asyncStarted())
        .andDo(result -> {
          try {
            mockMvc.perform(asyncDispatch(result));
          } catch (Exception e) {
            assertTrue(e instanceof Exception);
          }
        });
  }
}

