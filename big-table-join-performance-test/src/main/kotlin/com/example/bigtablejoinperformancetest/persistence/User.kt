package com.example.bigtablejoinperformancetest.persistence

import jakarta.persistence.*

@Entity
@Table(name = "users")
open class User @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false)
    open var name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var status: UserStatus = UserStatus.ACTIVE,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    open var role: Role = Role()
)

enum class UserStatus {
    ACTIVE,
    INACTIVE
}
