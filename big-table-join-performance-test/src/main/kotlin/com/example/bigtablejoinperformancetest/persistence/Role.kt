package com.example.bigtablejoinperformancetest.persistence

import jakarta.persistence.*

@Entity
@Table(name = "roles")
open class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false, unique = true)
    open var name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var status: RoleStatus = RoleStatus.ACTIVE,

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    open var users: MutableList<User> = mutableListOf()
)

enum class RoleStatus {
    ACTIVE,
    INACTIVE
}
