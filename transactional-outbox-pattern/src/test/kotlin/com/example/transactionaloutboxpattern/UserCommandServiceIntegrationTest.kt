package com.example.transactionaloutboxpattern

import com.example.transactionaloutboxpattern.notification.db.NotificationLogRepository
import com.example.transactionaloutboxpattern.outbox.OutboxEventRepository
import com.example.transactionaloutboxpattern.outbox.UserOutboxEventPublisher
import com.example.transactionaloutboxpattern.user.usecases.UserCommandService
import com.example.transactionaloutboxpattern.user.domain.UserStatus
import com.example.transactionaloutboxpattern.user.db.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserCommandServiceIntegrationTest {

    @BeforeEach
    fun resetState() {
        notificationLogRepository.deleteAll()
        outboxEventRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Autowired
    private lateinit var userCommandService: UserCommandService

    @Autowired
    private lateinit var userOutboxEventPublisher: UserOutboxEventPublisher

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var outboxEventRepository: OutboxEventRepository

    @Autowired
    private lateinit var notificationLogRepository: NotificationLogRepository

    @Test
    fun `creating a user stores user and outbox record in the same transaction`() {
        val user = userCommandService.createUser("jsmith", "John", "Smith", UserStatus.ACTIVE)

        assertThat(user.userId).isNotNull()
        assertThat(userRepository.count()).isEqualTo(1)
        assertThat(outboxEventRepository.findByProcessedAtIsNullOrderByCreatedAtAsc()).hasSize(1)
    }

    @Test
    fun `activating an inactive user logs an email notification`() {
        val created = userCommandService.createUser("inactive_user", "Jane", "Doe", UserStatus.INACTIVE)
        userCommandService.updateUser(created.userId!!, status = UserStatus.ACTIVE)

        val statusChangeEvent = outboxEventRepository.findByProcessedAtIsNullOrderByCreatedAtAsc()
            .first { it.eventType == "UserStatusChangedEvent" }
        assertThat(statusChangeEvent.payload).contains("INACTIVE").contains("ACTIVE")

        userOutboxEventPublisher.publishPendingEvents()

        assertThat(notificationLogRepository.count()).isEqualTo(1)
        assertThat(outboxEventRepository.findByProcessedAtIsNullOrderByCreatedAtAsc()).isEmpty()
    }
}
