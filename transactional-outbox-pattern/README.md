# Transactional Outbox Pattern

This project demonstrates a transactional outbox pattern in an onion architecture using Spring Boot and Kotlin.

## Why this is onion architecture

Onion architecture is about keeping the business logic at the center and pushing technical concerns outward. In this project the dependency direction is explicit:

- The core of the application is the `userdomain` package.
- That package contains the business rules and the `User` aggregate itself.
- It knows nothing about databases, HTTP controllers, or scheduling.
- The application layer (`userapplication`) orchestrates use cases, but it still depends on the core domain rather than the other way around.
- The infrastructure layer (`userinfrastructure`, `outbox`, `notificationinfrastructure`) is at the outer edge and is responsible for persistence, scheduled processing, and integration concerns.

This matches onion architecture because:

- business rules live at the center
- outer layers can implement technical capabilities
- dependencies point inward, never outward
- the domain can be tested without framework concerns

## Domain layer

The user domain is the center of the onion. It contains a `User` aggregate with:
- username
- firstName
- lastName
- status (`ACTIVE` / `INACTIVE`)

Each field update emits a domain event. A status transition from `INACTIVE` to `ACTIVE` also triggers the outbox flow that notifies the notification domain.

## Application layer

The `userapplication` package coordinates the domain operations. The command service creates and updates users while ensuring the domain events are captured and persisted as an outbox record in the same transaction.

## Infrastructure layer

The infrastructure layer contains the adapters that talk to external systems:

- `userinfrastructure` stores the `UserEntity` in the database
- `outbox` stores event records in a separate table so they are not lost when the business transaction commits
- `notificationinfrastructure` stores notification logs

The scheduled outbox processor runs outside the business transaction, which is the core idea of the transactional outbox pattern.

## Notification domain

The notification flow is intentionally separated from the user domain. When the outbox processor detects an `INACTIVE -> ACTIVE` status change, it calls the notification service. At this stage the notification is logged rather than sent through an email provider.

## Run

```bash
./mvnw test
```
