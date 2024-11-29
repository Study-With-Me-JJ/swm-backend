package com.jj.swm.domain.user.entity;

public enum RoleType {
    USER, ROOM_ADMIN, ADMIN;

    public String toSpringRole(){
        return "ROLE_" + this.name();
    }
}
