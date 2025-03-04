package com.jj.swm.domain.study.util;

import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class ConcurrencyTestUtils {

    public static final int THREAD_COUNT = 100;

    public static List<UUID> storeUserListAndLoadUserIdList(UserRepository userRepository) {
        List<User> userList = UserFixture.multiUser(THREAD_COUNT);
        userRepository.saveAll(userList);

        return userList.stream()
                .map(User::getId)
                .toList();
    }
}
