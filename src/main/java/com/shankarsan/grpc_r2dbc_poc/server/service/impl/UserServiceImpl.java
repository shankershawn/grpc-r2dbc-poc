package com.shankarsan.grpc_r2dbc_poc.server.service.impl;

import com.shankarsan.grpc_r2dbc_poc.dao.entity.User;
import com.shankarsan.grpc_r2dbc_poc.dao.repository.UserRepository;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse;
import com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

  private final UserRepository userRepository;

  @Override
  public void viewUser(com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest request,
                       io.grpc.stub.StreamObserver<com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse> responseObserver) {
    if (-1 == request.getUserId()) {
      extractFluxToObserver(userRepository.findAll(), responseObserver);
    } else {
      extractMonoToObserver(userRepository.findById(request.getUserId()), responseObserver);
    }
  }

  @Override
  public void createUser(com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest request,
                         io.grpc.stub.StreamObserver<com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse> responseObserver) {
    User user = User.builder()
        .userName(request.getUserName())
        .userEmail(request.getUserEmail())
        .build();

    extractMonoToObserver(userRepository.save(user), responseObserver);
  }

  @Override
  public void updateUser(com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest request,
                         io.grpc.stub.StreamObserver<com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse> responseObserver) {
    Long userId = Optional.of(request).map(UserRequest::getUserId).orElse(null);
    User user = User.builder()
        .userId(userId)
        .userName(request.getUserName())
        .userEmail(request.getUserEmail())
        .build();

    extractMonoToObserver(userRepository.save(user), responseObserver);
  }

  private void extractMonoToObserver(Mono<User> userMono, StreamObserver<UserResponse> responseObserver) {
    userMono
        .map(user -> UserResponse.newBuilder()
            .setUserId(user.getUserId())
            .setUserName(user.getUserName())
            .setUserEmail(user.getUserEmail())
            .build())
        .doFinally(signalType -> responseObserver.onCompleted())
        .subscribe(responseObserver::onNext);
  }

  private void extractFluxToObserver(Flux<User> userFlux, StreamObserver<UserResponse> responseObserver) {
    userFlux
        .map(savedUser -> UserResponse.newBuilder()
            .setUserId(savedUser.getUserId())
            .setUserName(savedUser.getUserName())
            .setUserEmail(savedUser.getUserEmail())
            .build())
        .doOnNext(responseObserver::onNext)
        .doOnComplete(responseObserver::onCompleted)
        .subscribe();
  }

  @Override
  public void deleteUser(com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserRequest request,
                         io.grpc.stub.StreamObserver<com.shankarsan.grpc_r2dbc_poc.proto_stubs.UserResponse> responseObserver) {
    Mono<Void> savedUserMono = userRepository.deleteById(request.getUserId());

    savedUserMono
        .doFinally(signalType -> {
          responseObserver.onNext(UserResponse.newBuilder()
              .setUserId(request.getUserId())
              .setUserName(request.getUserName())
              .setUserEmail(request.getUserEmail())
              .build());
          responseObserver.onCompleted();
        })
        .subscribe();
  }
}