package com.shankarsan.grpc_r2dbc_poc.dao.repository;

import com.shankarsan.grpc_r2dbc_poc.dao.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface UserRepository extends R2dbcRepository<User, Long> {
}
