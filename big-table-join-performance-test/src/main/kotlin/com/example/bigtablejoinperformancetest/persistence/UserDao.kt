package com.example.bigtablejoinperformancetest.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserDao : JpaRepository<User, Long> {
    fun findByStatus(status: UserStatus): List<User>
}
