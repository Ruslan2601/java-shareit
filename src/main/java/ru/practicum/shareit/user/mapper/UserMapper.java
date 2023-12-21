package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreate userCreate);

    UserCreate toUserCreate(User user);

    User toUser(UserUpdate userCreate);

    UserCreate toUserUpdate(User user);
}
