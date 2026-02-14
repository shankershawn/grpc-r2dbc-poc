package com.shankarsan.grpc_r2dbc_poc.dao.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERS_T")
@Builder
@Data
public class User {
  @Id
  @Column("USER_ID")
  private Long userId;

  @Column("USER_NAME")
  private String userName;

  @Column("USER_EMAIL")
  private String userEmail;

}
