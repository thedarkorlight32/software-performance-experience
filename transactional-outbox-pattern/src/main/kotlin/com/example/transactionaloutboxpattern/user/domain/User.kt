package com.example.transactionaloutboxpattern.user.domain


class User(
    var username: String,
    var firstName: String,
    var lastName: String,
    var status: UserStatus,
    var userId: Long? = null
) {
    private val domainEvents = mutableListOf<UserDomainEvent>()

    companion object {
        fun create(username: String, firstName: String, lastName: String, status: UserStatus = UserStatus.ACTIVE): User {
            val user = User(username, firstName, lastName, status)
            user.recordEvent(
                UserCreatedEvent(
                    userId = null,
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    status = status
                )
            )
            return user
        }
    }

    fun updateUsername(newUsername: String) {
        if (newUsername != username) {
            val previous = username
            username = newUsername
            recordEvent(UserUpdatedEvent(userId, "username", previous, newUsername))
        }
    }

    fun updateFirstName(newFirstName: String) {
        if (newFirstName != firstName) {
            val previous = firstName
            firstName = newFirstName
            recordEvent(UserUpdatedEvent(userId, "firstName", previous, newFirstName))
        }
    }

    fun updateLastName(newLastName: String) {
        if (newLastName != lastName) {
            val previous = lastName
            lastName = newLastName
            recordEvent(UserUpdatedEvent(userId, "lastName", previous, newLastName))
        }
    }

    fun updateStatus(newStatus: UserStatus) {
        if (newStatus != status) {
            val previous = status
            status = newStatus
            recordEvent(UserStatusChangedEvent(userId, username, previous, newStatus))
        }
    }

    fun withGeneratedId(id: Long): User {
        this.userId = id
        domainEvents.replaceAll { event ->
            when (event) {
                is UserCreatedEvent -> event.copy(userId = id)
                is UserUpdatedEvent -> event.copy(userId = id)
                is UserStatusChangedEvent -> event.copy(userId = id, username = username)
            }
        }
        return this
    }

    fun consumeDomainEvents(): List<UserDomainEvent> {
        val events = domainEvents.toList()
        domainEvents.clear()
        return events
    }

    private fun recordEvent(event: UserDomainEvent) {
        domainEvents.add(event)
    }
}
