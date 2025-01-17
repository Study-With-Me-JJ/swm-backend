package com.jj.swm.domain.external.study.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum Technology {
    TYPESCRIPT,
    JAVASCRIPT,
    REACT,
    VUE,
    SVELTE,
    NEXTJS,
    NODEJS,
    JAVA,
    SPRING,
    GO,
    NESTJS,
    KOTLIN,
    EXPRESS,
    MYSQL,
    MONGODB,
    PYTHON,
    DJANGO,
    PHP,
    GRAPHQL,
    FIREBASE,
    FLUTTER,
    SWIFT,
    REACTNATIVE,
    UNITY,
    AWS,
    KUBERNETES,
    DOCKER,
    GIT,
    FIGMA,
    ZEPLIN,
    JEST,
    C;

    public static Technology of(String name) {
        return valueOf(name.toUpperCase());
    }
}
