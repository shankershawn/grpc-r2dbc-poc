package com.shankarsan.grpc_r2dbc_poc.controller;

import com.google.common.util.concurrent.Futures;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserServiceGrpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController Tests")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserServiceGrpc.UserServiceFutureStub userServiceFutureStub;

  @Test
  @DisplayName("should create user and return response")
  void shouldCreateUserAndReturnResponse() throws Exception {
    UserResponse response = UserResponse.newBuilder()
        .setMessage("created")
        .setUserId(10L)
        .setUserName("Alice")
        .setUserEmail("alice@example.com")
        .build();

    when(userServiceFutureStub.createUser(any(UserRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    String body = "{\"userName\":\"Alice\",\"userEmail\":\"alice@example.com\"}";

    mockMvc.perform(post("/v1/api/users")
            .contentType("application/json")
            .content(body))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("created")))
            .andExpect(content().string(containsString("Alice")))
            .andExpect(content().string(containsString("alice@example.com"))));

    verify(userServiceFutureStub).createUser(any(UserRequest.class));
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

    when(userServiceFutureStub.updateUser(any(UserRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    String body = "{\"userName\":\"Bob\",\"userEmail\":\"bob@example.com\"}";

    mockMvc.perform(put("/v1/api/users")
            .param("userId", "12")
            .contentType("application/json")
            .content(body))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("updated")))
            .andExpect(content().string(containsString("Bob")))
            .andExpect(content().string(containsString("bob@example.com"))));

    verify(userServiceFutureStub).updateUser(any(UserRequest.class));
  }

  @Test
  @DisplayName("should get user by id")
  void shouldGetUserById() throws Exception {
    UserResponse response = UserResponse.newBuilder()
        .setMessage("found")
        .setUserId(5L)
        .setUserName("Carol")
        .setUserEmail("carol@example.com")
        .build();

    when(userServiceFutureStub.viewUser(any(UserRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(get("/v1/api/users").param("userId", "5"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("found")))
            .andExpect(content().string(containsString("Carol")))
            .andExpect(content().string(containsString("carol@example.com"))));

    verify(userServiceFutureStub).viewUser(any(UserRequest.class));
  }

  @Test
  @DisplayName("should delete user by id")
  void shouldDeleteUserById() throws Exception {
    UserResponse response = UserResponse.newBuilder()
        .setMessage("deleted")
        .setUserId(7L)
        .build();

    when(userServiceFutureStub.deleteUser(any(UserRequest.class)))
        .thenReturn(Futures.immediateFuture(response));

    mockMvc.perform(delete("/v1/api/users").param("userId", "7"))
        .andExpect(request().asyncStarted())
        .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("deleted"))));

    verify(userServiceFutureStub).deleteUser(any(UserRequest.class));
  }
}
