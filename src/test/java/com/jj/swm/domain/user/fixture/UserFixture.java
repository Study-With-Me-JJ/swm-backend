package com.jj.swm.domain.user.fixture;

import com.jj.swm.domain.user.entity.RoleType;
import com.jj.swm.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserFixture {

    public static final UUID uuid = UUID.randomUUID();

    public static User createRoomAdmin() {
        return User.builder()
                .id(uuid)
                .nickname("test")
                .name("test")
                .profileImageUrl("http://test.png")
                .userRole(RoleType.USER)
                .studyRooms(new ArrayList<>())
                .build();
    }

    public static User createUserWithUUID() {
        return User.builder()
                .id(UUID.randomUUID())
                .nickname(UUID.randomUUID().toString())
                .name("test")
                .profileImageUrl("http://test.png")
                .userRole(RoleType.USER)
                .studyRooms(new ArrayList<>())
                .build();
    }

    public static List<User> multiUser(int size) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            User user = UserFixture.createUserWithUUID();
            users.add(user);
        }
        return users;
    }

}
