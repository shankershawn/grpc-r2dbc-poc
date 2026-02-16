package com.shankarsan.grpc_r2dbc_poc.controller;

import com.shankarsan.grpc_r2dbc_poc.controller.model.User;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

@RestController
@RequestMapping("/v1/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserServiceGrpc.UserServiceStub userServiceStub;

  @PostMapping
  public Mono<User> createUser(@RequestBody User user) {
    Mono<UserResponse> userResponseMono = Mono.create(userResponseMonoSink ->
        userServiceStub.createUser(UserRequest.newBuilder()
                .setUserName(user.getUserName())
                .setUserEmail(user.getUserEmail())
                .build(),
            getResponseObserver(userResponseMonoSink)));
    return convertToUserMono(userResponseMono);
  }

  @PutMapping
  public Mono<User> updateUser(@RequestParam(defaultValue = "-1") Long userId, @RequestBody User user) {
    Mono<UserResponse> userResponseMono = Mono.create(userResponseMonoSink ->
        userServiceStub.updateUser(UserRequest.newBuilder()
                .setUserId(userId)
                .setUserName(user.getUserName())
                .setUserEmail(user.getUserEmail()).build(),
            getResponseObserver(userResponseMonoSink)));
    return convertToUserMono(userResponseMono);
  }

  @GetMapping
  public Flux<User> getUser(@RequestParam(defaultValue = "-1") Long userId) {
    Flux<UserResponse> userResponseFlux = Flux.create(userResponseFluxSink ->
        userServiceStub.viewUser(UserRequest.newBuilder().setUserId(userId).build(),
            getResponseObserver(userResponseFluxSink)));
    return userResponseFlux.map(userResponse -> User.builder()
        .userId(userResponse.getUserId())
        .userName(userResponse.getUserName())
        .userEmail(userResponse.getUserEmail())
        .build());
  }

  @DeleteMapping
  public Mono<User> deleteUser(@RequestParam(defaultValue = "-1") Long userId) {
    Mono<UserResponse> userResponseMono = Mono.create(userResponseMonoSink ->
        userServiceStub.deleteUser(UserRequest.newBuilder().setUserId(userId).build(),
            getResponseObserver(userResponseMonoSink)));
    return convertToUserMono(userResponseMono);
  }

  private static Mono<User> convertToUserMono(Mono<UserResponse> userResponseMono) {
    return userResponseMono.map(userResponse -> User.builder()
        .userId(userResponse.getUserId())
        .userName(userResponse.getUserName())
        .userEmail(userResponse.getUserEmail())
        .build());
  }

  private static StreamObserver<UserResponse> getResponseObserver(FluxSink<UserResponse> userResponseFluxSink) {
    return new StreamObserver<>() {
      @Override
      public void onNext(UserResponse userResponse) {
        userResponseFluxSink.next(userResponse);
      }

      @Override
      public void onError(Throwable throwable) {
        userResponseFluxSink.error(throwable);
      }

      @Override
      public void onCompleted() {
        userResponseFluxSink.complete();
      }
    };
  }

  private static StreamObserver<UserResponse> getResponseObserver(MonoSink<UserResponse> userResponseMonoSink) {
    return new StreamObserver<>() {
      @Override
      public void onNext(UserResponse userResponse) {
        userResponseMonoSink.success(userResponse);
      }

      @Override
      public void onError(Throwable throwable) {
        userResponseMonoSink.error(throwable);
      }

      @Override
      public void onCompleted() {
        // onCompleted is not needed for MonoSink as it will be completed when success or error is called
      }
    };
  }
}
