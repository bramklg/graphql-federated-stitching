package com.example.graphql.userservice.repository

import com.example.graphql.userservice.model.User
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ListCrudRepository<User, Long> {
}
