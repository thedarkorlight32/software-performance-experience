package com.example.transactionaloutboxpattern.user.db

import com.example.transactionaloutboxpattern.user.domain.User
import com.example.transactionaloutboxpattern.user.domain.UserStatus
import jakarta.persistence.*

@Entity
@Table(name = "users")
open class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false, unique = true)
    open var username: String = "",

    @Column(nullable = false)
    open var firstName: String = "",

    @Column(nullable = false)
    open var lastName: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var status: UserStatus = UserStatus.ACTIVE
) {
    constructor(user: User) : this(
        id = user.userId,
        username = user.username,
        firstName = user.firstName,
        lastName = user.lastName,
        status = user.status
    )

    fun toDomain(): User {
        return User(username = username, firstName = firstName, lastName = lastName, status = status, userId = id)
            .withGeneratedId(id ?: throw IllegalStateException("User id is required"))
    }
}
