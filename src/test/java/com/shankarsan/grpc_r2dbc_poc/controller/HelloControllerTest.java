package com.shankarsan.grpc_r2dbc_poc.controller;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("HelloController Tests")
class HelloControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub;

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
        .andExpect(content().string("Hello, Alice!"));
  }

  @Test
  @DisplayName("should return default greeting when name parameter is missing")
  void shouldReturnDefaultGreetingWhenNameParameterIsMissing() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, World!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello, World!"));
  }

  @Test
  @DisplayName("should return greeting with empty string when name is empty")
  void shouldReturnGreetingWithEmptyStringWhenNameIsEmpty() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, !").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", ""))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello, !"));
  }

  @Test
  @DisplayName("should handle special characters in name parameter")
  void shouldHandleSpecialCharactersInNameParameter() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, John@#$!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "John@#$"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Hello,")));
  }

  @Test
  @DisplayName("should handle unicode characters in name parameter")
  void shouldHandleUnicodeCharactersInNameParameter() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, José!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "José"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello, José!"));
  }

  @Test
  @DisplayName("should handle spaces in name parameter")
  void shouldHandleSpacesInNameParameter() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, John Doe!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "John Doe"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello, John Doe!"));
  }

  @Test
  @DisplayName("should return 200 OK status")
  void shouldReturn200OkStatus() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, Test!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "Test"))
        .andExpect(status().isOk());
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
        .andExpect(content().string(expectedMessage));
  }

  @Test
  @DisplayName("should handle numeric characters in name parameter")
  void shouldHandleNumericCharactersInNameParameter() throws Exception {
    HelloResponse response = HelloResponse.newBuilder().setMessage("Hello, 12345!").build();
    when(helloServiceBlockingStub.sayHello(any(HelloRequest.class))).thenReturn(response);

    mockMvc.perform(get("/v1/api/hello/synchronous").param("name", "12345"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello, 12345!"));
  }
}

