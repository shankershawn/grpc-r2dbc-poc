package com.shankarsan.grpc_r2dbc_poc.controller;

import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserServiceGrpc.UserServiceStub userServiceStub;

  @Test
  @DisplayName("should create user and return response")
  void shouldCreateUserAndReturnResponse() throws Exception {
    UserResponse response = UserResponse.newBuilder()
        .setMessage("created")
        .setUserId(10L)
        .setUserName("Alice")
        .setUserEmail("alice@example.com")
        .build();

    doAnswer(invocation -> {
      StreamObserver<UserResponse> observer = invocation.getArgument(1);
      observer.onNext(response);
      observer.onCompleted();
      return null;
    }).when(userServiceStub).createUser(any(UserRequest.class), any(StreamObserver.class));

    String body = "{\"userName\":\"Alice\",\"userEmail\":\"alice@example.com\"}";

    mockMvc.perform(post("/v1/api/users")
            .contentType("application/json")
            .content(body))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(10))
            .andExpect(jsonPath("$.userName").value("Alice"))
            .andExpect(jsonPath("$.userEmail").value("alice@example.com"))
            .andExpect(content().string(containsString("Alice"))));

    ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
    verify(userServiceStub).createUser(requestCaptor.capture(), any(StreamObserver.class));
    assertEquals("Alice", requestCaptor.getValue().getUserName());
    assertEquals("alice@example.com", requestCaptor.getValue().getUserEmail());
  }

  @Test
  @DisplayName("should update user and return response")
  void shouldUpdateUserAndReturnResponse() throws Exception {
    UserResponse response = UserResponse.newBuilder()
        .setMessage("updated")
        .setUserId(12L)
        .setUserName("Bob")
        .setUserEmail("bob@example.com")
        .build();

    doAnswer(invocation -> {
      StreamObserver<UserResponse> observer = invocation.getArgument(1);
      observer.onNext(response);
      observer.onCompleted();
      return null;
    }).when(userServiceStub).updateUser(any(UserRequest.class), any(StreamObserver.class));

    String body = "{\"userName\":\"Bob\",\"userEmail\":\"bob@example.com\"}";

    mockMvc.perform(put("/v1/api/users")
            .param("userId", "12")
            .contentType("application/json")
            .content(body))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(12))
            .andExpect(jsonPath("$.userName").value("Bob"))
            .andExpect(jsonPath("$.userEmail").value("bob@example.com")));

    ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
    verify(userServiceStub).updateUser(requestCaptor.capture(), any(StreamObserver.class));
    assertEquals(12L, requestCaptor.getValue().getUserId());
    assertEquals("Bob", requestCaptor.getValue().getUserName());
    assertEquals("bob@example.com", requestCaptor.getValue().getUserEmail());
  }

  @Test
  @DisplayName("should get user stream by id")
  void shouldGetUserById() throws Exception {
    UserResponse response = UserResponse.newBuilder()
        .setMessage("found")
        .setUserId(5L)
        .setUserName("Carol")
        .setUserEmail("carol@example.com")
        .build();

    doAnswer(invocation -> {
      StreamObserver<UserResponse> observer = invocation.getArgument(1);
      observer.onNext(response);
      observer.onCompleted();
      return null;
    }).when(userServiceStub).viewUser(any(UserRequest.class), any(StreamObserver.class));

    mockMvc.perform(get("/v1/api/users").param("userId", "5"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].userId").value(5))
            .andExpect(jsonPath("$[0].userName").value("Carol"))
            .andExpect(jsonPath("$[0].userEmail").value("carol@example.com")));

    ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
    verify(userServiceStub).viewUser(requestCaptor.capture(), any(StreamObserver.class));
    assertEquals(5L, requestCaptor.getValue().getUserId());
  }

  @Test
  @DisplayName("should delete user by id")
  void shouldDeleteUserById() throws Exception {
    UserResponse response = UserResponse.newBuilder()
        .setMessage("deleted")
        .setUserId(7L)
        .build();

    doAnswer(invocation -> {
      StreamObserver<UserResponse> observer = invocation.getArgument(1);
      observer.onNext(response);
      observer.onCompleted();
      return null;
    }).when(userServiceStub).deleteUser(any(UserRequest.class), any(StreamObserver.class));

    mockMvc.perform(delete("/v1/api/users").param("userId", "7"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(7)));

    ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
    verify(userServiceStub).deleteUser(requestCaptor.capture(), any(StreamObserver.class));
    assertEquals(7L, requestCaptor.getValue().getUserId());
  }
}
