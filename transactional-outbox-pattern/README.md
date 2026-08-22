# Transactional Outbox Pattern

The transactional outbox pattern is a way to reliably publish domain events without losing them when a database transaction is committed. In a normal workflow, business data and side effects can get out of sync if a database write succeeds but an external notification or event publication fails. The outbox pattern solves this by storing the event in the same database transaction as the business change. Later, a background process reads those persisted events and delivers them asynchronously.

This sub-project follows the onion architecture: the domain model sits at the center, application services orchestrate use cases, and infrastructure concerns such as persistence, HTTP, and scheduled jobs live on the outside. Dependencies always point inward, so the core domain stays independent from frameworks and technical details.

## User domain

The user domain is the main business area of the application. It contains the `User` aggregate and related domain events such as:

- `UserCreatedEvent`
- `UserUpdatedEvent`
- `UserStatusChangedEvent`

A user has fields such as username, first name, last name, and status (`ACTIVE` / `INACTIVE`). When a user is created or updated, the aggregate records domain events instead of directly triggering side effects.

## Notification domain

The notification domain represents the downstream concern that reacts to important business changes. In this example, it is responsible for recording activation notifications when a user transitions from `INACTIVE` to `ACTIVE`.

This domain stays separate from the user domain. It is not directly called during the user update transaction; instead, it is invoked later by the outbox processor.

## How the outbox message reaches the notification flow

The flow is:

1. A user update or creation is executed inside a transactional service.
2. The domain emits events and the application service persists them as outbox records in the same transaction.
3. The user data and the outbox event are committed together.
4. A scheduled publisher (`UserOutboxEventPublisher`) scans for unprocessed outbox events.
5. When it sees a `UserStatusChangedEvent`, it checks whether the status changed from `INACTIVE` to `ACTIVE`.
6. If so, it calls the `NotificationService`.
7. The notification service records the notification in the notification log repository.

This keeps the main business transaction atomic while moving the side effect to a reliable asynchronous step.

## Project structure

- `user/domain` contains the business model and domain logic
- `user/usecases` contains application services and controller endpoints
- `user/db` contains JPA persistence mapping
- `notification/domain` contains the notification logic
- `notification/db` contains the notification log persistence
- `outbox` contains the event store and scheduled processor

## How to run

From the parent project directory, run only this artifact's tests with:

```bash
./mvnw -pl transactional-outbox-pattern test
```
The app exposes the user API and demonstrates the transactional outbox flow end-to-end.
