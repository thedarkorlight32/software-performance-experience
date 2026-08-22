package com.example.transactionaloutboxpattern.outbox

import org.springframework.data.jpa.repository.JpaRepository

interface OutboxEventRepository : JpaRepository<OutboxEvent, Long> {
    fun findByProcessedAtIsNullOrderByCreatedAtAsc(): List<OutboxEvent>
}
