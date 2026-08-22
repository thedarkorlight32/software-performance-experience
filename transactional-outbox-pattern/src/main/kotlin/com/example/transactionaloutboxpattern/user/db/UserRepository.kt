package com.example.transactionaloutboxpattern.userinfrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long>
