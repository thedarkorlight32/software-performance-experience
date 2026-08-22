package com.example.transactionaloutboxpattern.outbox

import com.example.transactionaloutboxpattern.notificationdomain.NotificationService
import com.example.transactionaloutboxpattern.userdomain.UserStatus
import com.example.transactionaloutboxpattern.userdomain.UserStatusChangedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
open class UserOutboxEventPublisher {
    @Autowired
    lateinit var outboxEventRepository: OutboxEventRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var notificationService: NotificationService

    private val logger = LoggerFactory.getLogger(UserOutboxEventPublisher::class.java)

    @Scheduled(fixedDelay = 3000)
    fun publishPendingEvents() {
        val events = outboxEventRepository.findByProcessedAtIsNullOrderByCreatedAtAsc()
        events.forEach { event ->
            try {
                when (event.eventType) {
                    "UserStatusChangedEvent" -> handleStatusChangedEvent(event)
                    "UserCreatedEvent" -> logger.info("User created event processed for user {}", event.aggregateId)
                    else -> logger.info("Unhandled outbox event type {}", event.eventType)
                }
                event.processedAt = Instant.now()
                outboxEventRepository.save(event)
            } catch (ex: Exception) {
                logger.error("Failed to process outbox event {}", event.id, ex)
            }
        }
    }

    private fun handleStatusChangedEvent(event: OutboxEvent) {
        val userChangeEvent = objectMapper.readValue(event.payload, UserStatusChangedEvent::class.java)

        if (userChangeEvent.previousStatus == UserStatus.INACTIVE && userChangeEvent.newStatus == UserStatus.ACTIVE) {
            notificationService.sendActivationEmail(userChangeEvent.userId!!, userChangeEvent.username)
        }
    }
}
