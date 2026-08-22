package com.example.transactionaloutboxpattern.userapplication

import com.example.transactionaloutboxpattern.userdomain.UserStatus
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
open class UserController(
    private val userCommandService: UserCommandService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateUserRequest): UserResponse {
        val user = userCommandService.createUser(request.username, request.firstName, request.lastName, request.status)
        return UserResponse(
            id = user.userId ?: throw IllegalStateException("User id missing"),
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            status = user.status
        )
    }

    @PutMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestBody request: UpdateUserStatusRequest): UserResponse {
        val user = userCommandService.updateUser(id, status = request.status)
        return UserResponse(
            id = user.userId ?: throw IllegalStateException("User id missing"),
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            status = user.status
        )
    }
}

data class CreateUserRequest(
    val username: String,
    val firstName: String,
    val lastName: String,
    val status: UserStatus = UserStatus.ACTIVE
)

data class UpdateUserStatusRequest(
    val status: UserStatus
)

data class UserResponse(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val status: UserStatus
)
