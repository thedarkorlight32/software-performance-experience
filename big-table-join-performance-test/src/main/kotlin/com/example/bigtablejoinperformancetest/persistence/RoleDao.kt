package com.example.bigtablejoinperformancetest.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface RoleDao : JpaRepository<Role, Long> {
    fun findByStatus(status: RoleStatus): List<Role>
    fun findByName(name: String): Role?
}
