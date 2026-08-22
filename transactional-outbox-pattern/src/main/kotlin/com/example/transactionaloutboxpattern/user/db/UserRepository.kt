package com.example.transactionaloutboxpattern.user.db

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long>
