package com.example.graphql.userservice.model.mapper

import com.example.graphql.generated.CreateUserInputDTO
import com.example.graphql.generated.UserDTO
import com.example.graphql.userservice.model.User
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING
)
interface UserMapper {

    fun toDTO(user: User): UserDTO

    fun toEntity(createUserInput: CreateUserInputDTO): User
}
