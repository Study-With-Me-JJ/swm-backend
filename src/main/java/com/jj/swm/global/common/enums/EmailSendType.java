package com.jj.swm.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailSendType {
    EMAIL("이메일"),
    PASSWORD("패스워드");

    private final String type;
}
