package com.shankarsan.grpc_r2dbc_poc.controller;

import com.google.common.util.concurrent.ListenableFuture;
import com.shankarsan.grpc_r2dbc_poc.controller.model.User;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/v1/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserServiceGrpc.UserServiceFutureStub userServiceFutureStub;

  @PostMapping
  public DeferredResult<UserResponse> createUser(@RequestBody User user) {
    DeferredResult<UserResponse> deferredResult = new DeferredResult<>();

    ListenableFuture<UserResponse> userResponseFuture = userServiceFutureStub.createUser(
        com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest.newBuilder()
            .setUserName(user.getUserName())
            .setUserEmail(user.getUserEmail())
            .build());

    convertToResult(userResponseFuture, deferredResult);
    return deferredResult;
  }

  @PutMapping
  public DeferredResult<UserResponse> updateUser(@RequestParam(defaultValue = "-1") Long userId, @RequestBody User user) {
    DeferredResult<UserResponse> deferredResult = new DeferredResult<>();

    ListenableFuture<UserResponse> userResponseFuture = userServiceFutureStub.updateUser(
        com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest.newBuilder()
            .setUserId(userId)
            .setUserName(user.getUserName())
            .setUserEmail(user.getUserEmail())
            .build());

    convertToResult(userResponseFuture, deferredResult);
    return deferredResult;
  }

  @GetMapping
  public DeferredResult<UserResponse> getUser(@RequestParam(defaultValue = "-1") Long userId) {
    DeferredResult<UserResponse> deferredResult = new DeferredResult<>();

    ListenableFuture<UserResponse> userResponseFuture = userServiceFutureStub.viewUser(
        com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest.newBuilder().setUserId(userId).build());

    convertToResult(userResponseFuture, deferredResult);
    return deferredResult;
  }

  @DeleteMapping
  public DeferredResult<UserResponse> deleteUser(@RequestParam(defaultValue = "-1") Long userId) {
    DeferredResult<UserResponse> deferredResult = new DeferredResult<>();

    ListenableFuture<UserResponse> userResponseFuture = userServiceFutureStub.deleteUser(
        com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest.newBuilder().setUserId(userId).build());

    convertToResult(userResponseFuture, deferredResult);
    return deferredResult;
  }

  private static void convertToResult(ListenableFuture<UserResponse> userResponseFuture, DeferredResult<UserResponse> deferredResult) {
    userResponseFuture.addListener(() -> {
      try {
        UserResponse userResponse = userResponseFuture.get();
        deferredResult.setResult(userResponse);
      } catch (Exception e) {
        deferredResult.setErrorResult(e);
      }
    }, Runnable::run);
  }
}
