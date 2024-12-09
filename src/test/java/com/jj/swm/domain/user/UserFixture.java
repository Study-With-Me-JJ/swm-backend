package com.jj.swm.domain.user;

import com.jj.swm.domain.user.entity.RoleType;
import com.jj.swm.domain.user.entity.User;

import java.util.ArrayList;
import java.util.UUID;

public class UserFixture {

    public static final UUID uuid = UUID.randomUUID();

    public static User createUser() {
        return User.builder()
                .id(uuid)
                .nickname("test")
                .profileImageUrl("http://test.png")
                .userRole(RoleType.USER)
                .studyRooms(new ArrayList<>())
                .build();
    }

    public static User createUserWithUUID() {
        return User.builder()
                .id(UUID.randomUUID())
                .nickname("test")
                .profileImageUrl("http://test.png")
                .userRole(RoleType.USER)
                .studyRooms(new ArrayList<>())
                .build();
    }

}
