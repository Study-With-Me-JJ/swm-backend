package com.jj.swm.domain.user.helper;

import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserTestHelper {
    public static List<UUID> insertUsersAndGetUserIds(UserRepository userRepository, int size) {
        List<User> userList = UserFixture.multiUser(size);
        userRepository.saveAll(userList);

        return userList.stream()
                .map(User::getId)
                .toList();
    }
}
