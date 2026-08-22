package com.example.transactionaloutboxpattern.notification.domain

import com.example.transactionaloutboxpattern.notification.db.NotificationLogRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class NotificationService {
    @Autowired
    lateinit var notificationLogRepository: NotificationLogRepository

    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    fun sendActivationEmail(userId: Long, username: String) {
        val message = "Email sent to $username because the user became active."
        notificationLogRepository.save(
            NotificationLog(
                userId = userId,
                username = username,
                message = message
            )
        )
        logger.info(message)
    }
}
