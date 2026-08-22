package com.example.transactionaloutboxpattern.notification.db

import com.example.transactionaloutboxpattern.notification.domain.NotificationLog
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationLogRepository : JpaRepository<NotificationLog, Long>
