package com.shankarsan.grpc_r2dbc_poc.server.service.impl;

import com.shankarsan.grpc_r2dbc_poc.dao.entity.User;
import com.shankarsan.grpc_r2dbc_poc.dao.repository.UserRepository;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

  private UserServiceImpl service;

  @Mock
  private UserRepository userRepository;

  @Mock
  private StreamObserver<UserResponse> responseObserver;

  @BeforeEach
  void setUp() {
    service = new UserServiceImpl(userRepository);
  }

  @Test
  @DisplayName("viewUser should emit response and complete when user exists")
  void viewUserShouldEmitResponseAndCompleteWhenUserExists() {
    User user = User.builder()
        .userId(1L)
        .userName("Alice")
        .userEmail("alice@example.com")
        .build();
    when(userRepository.findById(1L)).thenReturn(Mono.just(user));

    service.viewUser(UserRequest.newBuilder().setUserId(1L).build(), responseObserver);

    ArgumentCaptor<UserResponse> responseCaptor = ArgumentCaptor.forClass(UserResponse.class);
    InOrder inOrder = inOrder(responseObserver);
    inOrder.verify(responseObserver).onNext(responseCaptor.capture());
    inOrder.verify(responseObserver).onCompleted();
    inOrder.verifyNoMoreInteractions();

    UserResponse response = responseCaptor.getValue();
    assertEquals(1L, response.getUserId());
    assertEquals("Alice", response.getUserName());
    assertEquals("alice@example.com", response.getUserEmail());
  }

  @Test
  @DisplayName("viewUser should complete without response when not found")
  void viewUserShouldCompleteWithoutResponseWhenNotFound() {
    when(userRepository.findById(99L)).thenReturn(Mono.empty());

    service.viewUser(UserRequest.newBuilder().setUserId(99L).build(), responseObserver);

    verify(responseObserver, never()).onNext(any());
    verify(responseObserver).onCompleted();
  }

  @Test
  @DisplayName("createUser should persist and emit response")
  void createUserShouldPersistAndEmitResponse() {
    User saved = User.builder()
        .userId(5L)
        .userName("Bob")
        .userEmail("bob@example.com")
        .build();
    when(userRepository.save(any(User.class))).thenReturn(Mono.just(saved));

    UserRequest request = UserRequest.newBuilder()
        .setUserName("Bob")
        .setUserEmail("bob@example.com")
        .build();
    service.createUser(request, responseObserver);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    ArgumentCaptor<UserResponse> responseCaptor = ArgumentCaptor.forClass(UserResponse.class);
    verify(userRepository).save(userCaptor.capture());
    verify(responseObserver).onNext(responseCaptor.capture());
    verify(responseObserver).onCompleted();

    User toSave = userCaptor.getValue();
    assertNull(toSave.getUserId());
    assertEquals("Bob", toSave.getUserName());
    assertEquals("bob@example.com", toSave.getUserEmail());

    UserResponse response = responseCaptor.getValue();
    assertEquals(5L, response.getUserId());
    assertEquals("Bob", response.getUserName());
    assertEquals("bob@example.com", response.getUserEmail());
  }

  @Test
  @DisplayName("updateUser should persist and emit response")
  void updateUserShouldPersistAndEmitResponse() {
    User saved = User.builder()
        .userId(8L)
        .userName("Carol")
        .userEmail("carol@example.com")
        .build();
    when(userRepository.save(any(User.class))).thenReturn(Mono.just(saved));

    UserRequest request = UserRequest.newBuilder()
        .setUserId(8L)
        .setUserName("Carol")
        .setUserEmail("carol@example.com")
        .build();
    service.updateUser(request, responseObserver);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    ArgumentCaptor<UserResponse> responseCaptor = ArgumentCaptor.forClass(UserResponse.class);
    verify(userRepository).save(userCaptor.capture());
    verify(responseObserver).onNext(responseCaptor.capture());
    verify(responseObserver).onCompleted();

    User toSave = userCaptor.getValue();
    assertEquals(8L, toSave.getUserId());
    assertEquals("Carol", toSave.getUserName());
    assertEquals("carol@example.com", toSave.getUserEmail());

    UserResponse response = responseCaptor.getValue();
    assertEquals(8L, response.getUserId());
    assertEquals("Carol", response.getUserName());
    assertEquals("carol@example.com", response.getUserEmail());
  }

  @Test
  @DisplayName("deleteUser should delete and emit response")
  void deleteUserShouldDeleteAndEmitResponse() {
    when(userRepository.deleteById(7L)).thenReturn(Mono.empty());

    UserRequest request = UserRequest.newBuilder()
        .setUserId(7L)
        .setUserName("Dave")
        .setUserEmail("dave@example.com")
        .build();
    service.deleteUser(request, responseObserver);

    ArgumentCaptor<UserResponse> responseCaptor = ArgumentCaptor.forClass(UserResponse.class);
    InOrder inOrder = inOrder(responseObserver);
    verify(userRepository).deleteById(7L);
    inOrder.verify(responseObserver).onNext(responseCaptor.capture());
    inOrder.verify(responseObserver).onCompleted();
    inOrder.verifyNoMoreInteractions();

    UserResponse response = responseCaptor.getValue();
    assertEquals(7L, response.getUserId());
    assertEquals("Dave", response.getUserName());
    assertEquals("dave@example.com", response.getUserEmail());
  }
}
