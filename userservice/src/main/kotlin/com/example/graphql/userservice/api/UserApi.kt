package com.example.graphql.userservice.api

import com.example.graphql.generated.CreateUserInputDTO
import com.example.graphql.generated.UserDTO
import com.example.graphql.generated.UsersMutationApi
import com.example.graphql.generated.UsersQueryApi
import com.example.graphql.userservice.model.mapper.UserMapper
import com.example.graphql.userservice.service.UserService
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserApi(
    private val userMapper: UserMapper,
    private val userService: UserService
) : UsersQueryApi, UsersMutationApi {

    @MutationMapping
    override fun createUser(@Argument user: CreateUserInputDTO, env: DataFetchingEnvironment): UserDTO {
        val userEntity = userService.create(userMapper.toEntity(user));
        return userMapper.toDTO(userEntity)
    }

    @QueryMapping
    override fun user(@Argument id: Long, env: DataFetchingEnvironment): UserDTO? {
        return userService.byId(id)?.let {
            userMapper.toDTO(it)
        };
    }

    @QueryMapping
    override fun users(env: DataFetchingEnvironment): List<UserDTO> {
        return userService.all().map { userMapper.toDTO(it) }
    }

    @QueryMapping
    override fun _usersByIds(@Argument ids: List<Long>, env: DataFetchingEnvironment): List<UserDTO?> {
        val users = userService.byIds(ids).map { userMapper.toDTO(it) }.associateBy { it.id };
        return ids.map { users[it] }
    }
}
