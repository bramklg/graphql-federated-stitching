package com.example.graphql.userservice.service

import com.example.graphql.userservice.model.User
import com.example.graphql.userservice.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun byId(id: Long): User? {
        return userRepository.findByIdOrNull(id)
    }

    fun all(): List<User> {
        return userRepository.findAll()
    }

    fun byIds(ids: Iterable<Long>): List<User> {
        return userRepository.findAllById(ids)
    }

    fun create(user: User) : User {
        return userRepository.save(user);
    }
}
