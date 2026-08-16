# Big Table Join Performance Test

This project is a focused JPA benchmark for comparing two query execution strategies against a large dataset with a user-to-role relationship. The goal is to evaluate how database planning and filtering order affect performance when most rows are inactive and only a small subset of active users should be counted for a specific role set.

The test model uses:
- `User` with fields such as `id`, `name`, `status`, and a single `role`
- `Role` with fields such as `id`, `name`, and `status`
- Spring Data JPA with an in-memory H2 database for repeatable integration testing

The benchmark seeds:
- 100,000 users
- 500 roles
- 2,000 active users
- the remaining users inactive

This setup approximates a realistic “big table” scenario where query optimization matters, especially when a join is performed over a large volume of mostly filtered-out rows.

## Query experience: join first vs filter first

The first execution pattern tested is the more intuitive but often less efficient one:

- join the user table to the role table
- then apply the active-user filter in the `WHERE` clause

This approach can force the database to evaluate many joined rows before it narrows the result set. In a dataset where the majority of users are inactive, that means extra work is performed before the system discovers most rows are irrelevant. In practice, this tends to be more expensive because the join is executed against a large candidate set before the status restriction reduces the row count.

The second execution pattern tested is the opposite strategy:

- filter the active users first
- then restrict the remaining rows to the target role set through the role condition

This approach helps the database reduce the working set earlier. Since the inactive users are the majority, removing them before the role join limits the number of rows participating in the join. In this benchmark, this usually creates a much smaller intermediate result and reduces the time spent computing join matches.

The experience from running these tests mirrors the typical SQL optimization rule: filter early when that predicate is highly selective, and avoid joining large tables before reducing the row set. In a real production system, the difference becomes even more noticeable as data volume grows, skew increases, and the number of inactive rows dominates the table.

This project is intended as a simple, reproducible demonstration of why query ordering matters when working with large joined datasets.
