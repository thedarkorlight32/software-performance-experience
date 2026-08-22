package com.example.transactionaloutboxpattern.user.usecases

import com.example.transactionaloutboxpattern.outbox.OutboxEvent
import com.example.transactionaloutboxpattern.outbox.OutboxEventRepository
import com.example.transactionaloutboxpattern.user.domain.User
import com.example.transactionaloutboxpattern.user.domain.UserStatus
import com.example.transactionaloutboxpattern.user.db.UserEntity
import com.example.transactionaloutboxpattern.user.db.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserCommandService {
    fun createUser(username: String, firstName: String, lastName: String, status: UserStatus = UserStatus.ACTIVE): User
    fun updateUser(id: Long, username: String? = null, firstName: String? = null, lastName: String? = null, status: UserStatus? = null): User
}

@Service
open class DefaultUserCommandService : UserCommandService {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var outboxEventRepository: OutboxEventRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Transactional
    override fun createUser(username: String, firstName: String, lastName: String, status: UserStatus): User {
        val user = User.create(username, firstName, lastName, status)
        val saved = userRepository.save(UserEntity(user))
        user.withGeneratedId(saved.id ?: throw IllegalStateException("User id missing"))
        persistOutboxEvents(user)
        return user
    }

    @Transactional
    override fun updateUser(id: Long, username: String?, firstName: String?, lastName: String?, status: UserStatus?): User {
        val entity = userRepository.findById(id).orElseThrow { IllegalArgumentException("User not found: $id") }
        val user = entity.toDomain()

        username?.let { user.updateUsername(it) }
        firstName?.let { user.updateFirstName(it) }
        lastName?.let { user.updateLastName(it) }
        status?.let { user.updateStatus(it) }

        val updated = userRepository.save(UserEntity(user))
        user.withGeneratedId(updated.id ?: id)
        persistOutboxEvents(user)
        return user
    }

    private fun persistOutboxEvents(user: User) {
        user.consumeDomainEvents().forEach { event ->
            val payload = objectMapper.writeValueAsString(event)
            outboxEventRepository.save(
                OutboxEvent(
                    aggregateType = "User",
                    aggregateId = user.userId ?: throw IllegalStateException("User id required for outbox event"),
                    eventType = event.javaClass.simpleName,
                    payload = payload
                )
            )
        }
    }
}