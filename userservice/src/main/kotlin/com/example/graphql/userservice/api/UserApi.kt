package com.example.graphql.userservice.api

import com.example.graphql.generated.CreateUserInputDTO
import com.example.graphql.generated.UserDTO
import com.example.graphql.generated.UsersMutationApi
import com.example.graphql.generated.UsersQueryApi
import com.example.graphql.userservice.model.mapper.UserMapper
import com.example.graphql.userservice.service.UserService
import graphql.schema.DataFetchingEnvironment
import jakarta.servlet.http.Part
import org.springframework.stereotype.Controller

@Controller
class UserApi(
    private val userMapper: UserMapper,
    private val userService: UserService
) : UsersQueryApi, UsersMutationApi {

    override fun createUser(user: CreateUserInputDTO, env: DataFetchingEnvironment): UserDTO {
        val userEntity = userService.create(userMapper.toEntity(user));
        return userMapper.toDTO(userEntity)
    }

    override fun addAvatar(user: Long, file: Part, env: DataFetchingEnvironment): String {
        return "${file.submittedFileName} uploaded with a file size of ${file.size}bytes";
    }

    override fun user(id: Long, env: DataFetchingEnvironment): UserDTO? {
        return userService.byId(id)?.let {
            userMapper.toDTO(it)
        };
    }

    override fun users(env: DataFetchingEnvironment): List<UserDTO> {
        return userService.all().map { userMapper.toDTO(it) }
    }

    override fun _usersByIds(ids: List<Long>, env: DataFetchingEnvironment): List<UserDTO?> {
        val users = userService.byIds(ids).map { userMapper.toDTO(it) }.associateBy { it.id };
        return ids.map { users[it] }
    }
}
