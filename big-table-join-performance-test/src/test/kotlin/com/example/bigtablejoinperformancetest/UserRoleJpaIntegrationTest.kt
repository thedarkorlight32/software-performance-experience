package com.example.bigtablejoinperformancetest

import com.example.bigtablejoinperformancetest.persistence.Role
import com.example.bigtablejoinperformancetest.persistence.RoleDao
import com.example.bigtablejoinperformancetest.persistence.RoleStatus
import com.example.bigtablejoinperformancetest.persistence.User
import com.example.bigtablejoinperformancetest.persistence.UserDao
import com.example.bigtablejoinperformancetest.persistence.UserStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class UserRoleJpaIntegrationTest @Autowired constructor(
    private val testEntityManager: TestEntityManager,
    private val userDao: UserDao,
    private val roleDao: RoleDao
) {

    private companion object {
       const val TOTAL_USERS = 100_000
       const val ACTIVE_USERS = 2_000
    }

    private fun prepareBenchmarkData(): Pair<List<Role>, List<String>> {
       val roles = (1..500).map { index ->
           val role = Role(name = "ROLE_$index", status = RoleStatus.ACTIVE)
           testEntityManager.persist(role)
           role
       }

       val targetRoles = roles.take(5)
       val targetRoleNames = targetRoles.map { it.name }

       for (index in 1..TOTAL_USERS) {
           val status = if (index <= ACTIVE_USERS) UserStatus.ACTIVE else UserStatus.INACTIVE
           val chosenRole = if (status == UserStatus.ACTIVE) {
               targetRoles[index % targetRoles.size]
           } else {
               roles[(index * 17) % roles.size]
           }

           val user = User(name = "USER_$index", status = status, role = chosenRole)
           testEntityManager.persist(user)

           if (index % 5_000 == 0) {
               testEntityManager.flush()
               testEntityManager.clear()
           }
       }

       testEntityManager.flush()
       testEntityManager.clear()
       return Pair(roles, targetRoleNames)
    }

    @Test
    fun `filter active users before joining roles runs faster than join then filter`() {
       val (_, targetRoleNames) = prepareBenchmarkData()

       val joinThenFilterQuery = """
           SELECT COUNT(u)
           FROM User u
           JOIN u.role r
           WHERE r.name IN :roleNames AND u.status = :userStatus
       """.trimIndent()

       val filterThenJoinQuery = """
           SELECT COUNT(u)
           FROM User u
           WHERE u.status = :userStatus
             AND u.role IN (
                 SELECT r
                 FROM Role r
                 WHERE r.name IN :roleNames
             )
       """.trimIndent()

       val joinThenFilterStart = System.nanoTime()
       var joinThenFilterCount: Long = 0
       repeat(20) {
           joinThenFilterCount = testEntityManager.entityManager
               .createQuery(joinThenFilterQuery, Long::class.java)
               .setParameter("roleNames", targetRoleNames)
               .setParameter("userStatus", UserStatus.ACTIVE)
               .singleResult
       }
       val joinThenFilterElapsedMs = (System.nanoTime() - joinThenFilterStart) / 1_000_000.0

       val filterThenJoinStart = System.nanoTime()
       var filterThenJoinCount: Long = 0
       repeat(20) {
           filterThenJoinCount = testEntityManager.entityManager
               .createQuery(filterThenJoinQuery, Long::class.java)
               .setParameter("roleNames", targetRoleNames)
               .setParameter("userStatus", UserStatus.ACTIVE)
               .singleResult
       }
       val filterThenJoinElapsedMs = (System.nanoTime() - filterThenJoinStart) / 1_000_000.0
       val timeDifferenceMs = joinThenFilterElapsedMs - filterThenJoinElapsedMs

       println("joinThenFilterElapsedMs=${joinThenFilterElapsedMs}ms")
       println("filterThenJoinElapsedMs=${filterThenJoinElapsedMs}ms")
       println("timeDifferenceMs=${timeDifferenceMs}ms")

       assertThat(joinThenFilterCount).isEqualTo(ACTIVE_USERS.toLong())
       assertThat(filterThenJoinCount).isEqualTo(ACTIVE_USERS.toLong())
       assertThat(filterThenJoinElapsedMs).isLessThan(joinThenFilterElapsedMs)
       assertThat(userDao.findByStatus(UserStatus.INACTIVE)).hasSizeGreaterThan(90_000)
    }
}
