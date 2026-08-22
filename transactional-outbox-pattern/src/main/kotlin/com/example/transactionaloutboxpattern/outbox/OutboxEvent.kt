package com.example.transactionaloutboxpattern.outbox

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "outbox_events")
open class OutboxEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false)
    open var aggregateType: String = "",

    @Column(nullable = false)
    open var aggregateId: Long = 0L,

    @Column(nullable = false)
    open var eventType: String = "",

    @Lob
    @Column(nullable = false)
    open var payload: String = "",

    @Column(nullable = false)
    open var createdAt: Instant = Instant.now(),

    @Column(nullable = true)
    open var processedAt: Instant? = null
)
