package com.example.transactionaloutboxpattern.userdomain

import java.time.Instant

sealed interface UserDomainEvent {
    val occurredAt: Instant
    val userId: Long?
}

data class UserCreatedEvent(
    override val userId: Long?,
    val username: String,
    val firstName: String,
    val lastName: String,
    val status: UserStatus,
    override val occurredAt: Instant = Instant.now()
) : UserDomainEvent

data class UserUpdatedEvent(
    override val userId: Long?,
    val changedField: String,
    val previousValue: String,
    val newValue: String,
    override val occurredAt: Instant = Instant.now()
) : UserDomainEvent

data class UserStatusChangedEvent(
    override val userId: Long?,
    val username: String,
    val previousStatus: UserStatus,
    val newStatus: UserStatus,
    override val occurredAt: Instant = Instant.now()
) : UserDomainEvent
