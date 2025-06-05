package com.jj.swm.domain.study.common;

import com.jj.swm.domain.study.core.entity.StudyTag;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EntityModificationValidatorUnitTest {

    private final List<StudyTag> tags = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 10; i++) {
            StudyTag tag = Mockito.mock(StudyTag.class);
            Mockito.when(tag.getId()).thenReturn(Long.valueOf(i));
            tags.add(tag);
        }
    }

    @Test
    @DisplayName("존재하는 id만 들어있는 리스트이면 validateAllIdsPresent에 성공한다.")
    void validateAllIdsPresent_Success() {
        //when & then
        assertDoesNotThrow(() -> EntityModificationValidator.validateAllIdsPresent(
                tags.stream().map(StudyTag::getId).toList(),
                tags,
                StudyTag::getId,
                "test"
        ));
    }

    @Test
    @DisplayName("존재하지 않는 id가 들어있는 리스트이면 validateAllIdsPresent에 실패한다.")
    void validateAllIdsPresent_WhenIdsNotPresent_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> EntityModificationValidator.validateAllIdsPresent(
                List.of(987654321L),
                tags,
                StudyTag::getId,
                "test"
        ));
    }

    @Test
    @DisplayName("getSafeList에 null이 아닌 인자를 넘기면 그대로 반환한다.")
    void getSafeList_Success(){
        //when
        List<StudyTag> safeList = EntityModificationValidator.getSafeList(tags);

        //then
        assertIterableEquals(tags, safeList);
    }

    @Test
    @DisplayName("getSafeList에 null인 인자를 넘기면 빈 리스트 반환한다.")
    void getSafeList_NullList_Success(){
        //when
        List<StudyTag> safeList = EntityModificationValidator.getSafeList(null);

        //then
        assertNotNull(safeList);
        assertTrue(safeList.isEmpty());
    }

    @Test
    @DisplayName("최대 사이즈 이하이면 validateSizeLimit에 성공한다.")
    void validateSizeLimit_Success(){
        //when & then
        assertDoesNotThrow(() -> EntityModificationValidator.validateSizeLimit(
                1,
                10,
                "test"
        ));
    }

    @Test
    @DisplayName("최대 사이즈 초과이면 validateSizeLimit에 실패한다.")
    void validateSizeLimit_WhenExceedMaxLimit_Success(){
        //when & then
        assertThrows(GlobalException.class, () -> EntityModificationValidator.validateSizeLimit(
                11,
                10,
                "test"
        ));
        assertThrows(GlobalException.class, () -> EntityModificationValidator.validateSizeLimit(
                11,
                1,
                10,
                "test"
        ));
    }

    @Test
    @DisplayName("최소 사이즈 이상, 최대 사이즈 이하이면 validateSizeLimit에 성공한다.")
    void validateSizeLimit_WithMinAndMaxLimit_Success(){
        //when & then
        assertDoesNotThrow(() -> EntityModificationValidator.validateSizeLimit(
                5,
                1,
                10,
                "test"
        ));
    }

    @Test
    @DisplayName("최소 사이즈 미만이면 validateSizeLimit에 실패한다.")
    void validateSizeLimit_WhenUnderMinLimit_Success(){
        //when & then
        assertThrows(GlobalException.class, () -> EntityModificationValidator.validateSizeLimit(
                1,
                5,
                10,
                "test"
        ));
    }
}
