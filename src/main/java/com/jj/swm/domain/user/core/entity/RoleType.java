package com.jj.swm.domain.user.core.entity;

public enum RoleType {
    USER, ROOM_ADMIN, ADMIN;

    public String toSpringRole(){
        return "ROLE_" + this.name();
    }
}
