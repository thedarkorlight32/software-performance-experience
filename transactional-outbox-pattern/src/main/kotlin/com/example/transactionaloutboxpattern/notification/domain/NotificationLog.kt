package com.example.transactionaloutboxpattern.notification.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "notification_logs")
open class NotificationLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false)
    open var userId: Long = 0L,

    @Column(nullable = false)
    open var username: String = "",

    @Column(nullable = false)
    open var message: String = "",

    @Column(nullable = false)
    open var sentAt: Instant = Instant.now()
)
