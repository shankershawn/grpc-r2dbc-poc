package com.shankarsan.grpc_r2dbc_poc.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class User {

  private Long userId;
  private String userName;
  private String userEmail;
}
