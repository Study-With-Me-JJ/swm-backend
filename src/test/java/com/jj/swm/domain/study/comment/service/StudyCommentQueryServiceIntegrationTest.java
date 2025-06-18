package com.jj.swm.domain.study.comment.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.response.GetStudyChildCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.GetStudyParentCommentResponse;
import com.jj.swm.domain.study.comment.fixture.dto.request.UpsertStudyCommentRequestFixture;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.constants.PageSize;
import com.jj.swm.global.common.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class StudyCommentQueryServiceIntegrationTest extends IntegrationContainerSupporter {

    // target service
    @Autowired
    private StudyCommentQueryService commentQueryService;

    // service
    @Autowired
    private StudyCommandService studyCommandService;

    @Autowired
    private StudyCommentCommandService commentCommandService;

    // repository
    @Autowired
    private UserRepository userRepository;

    // entity
    private User user;
    private Long parentId;
    private final Long studyId = 1L; // setUp에서 생성한 스터디의 ID 값

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        parentId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                studyId,
                null,
                user.getId()
        ).getCommentId();
    }

    @Test
    @DisplayName("스터디 모집 댓글 목록 조회에 성공한다.")
    void getParents_Success() {
        //given
        for (int i = 0; i < PageSize.StudyParentComment - 1; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    studyId,
                    null,
                    user.getId()
            );
        }

        Long lastParentId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                studyId,
                null,
                user.getId()
        ).getCommentId();

        //when
        PageResponse<GetStudyParentCommentResponse> pageResponse = commentQueryService.getParents(studyId, 0);

        //then
        assertEquals(PageSize.StudyParentComment, pageResponse.getNumberOfElements());
        assertEquals((lastParentId - 1) / PageSize.StudyParentComment + 1, pageResponse.getTotalPages());
        assertEquals(lastParentId, pageResponse.getTotalElements());
        assertEquals(lastParentId - PageSize.StudyParentComment + 1, pageResponse.getData().getLast().getCommentId());
        assertTrue(pageResponse.isHasNext()); // StudyParentComment 페이지 사이즈 보다 1 크므로
        for (GetStudyParentCommentResponse response : pageResponse.getData()) {
            assertEquals(lastParentId--, response.getCommentId());
        }
    }

    @Test
    @DisplayName("스터디 모집 대댓글 목록 조회에 성공한다.")
    void getChildren_Success() {
        //given
        for (int i = 0; i < PageSize.StudyChildComment - 1; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    studyId,
                    parentId,
                    user.getId()
            );
        }
        Long lastChildId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                studyId,
                parentId,
                user.getId()
        ).getCommentId();

        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(parentId, null);

        //then
        assertEquals(PageSize.StudyChildComment, pageResponse.getNumberOfElements());
        assertEquals(-1, pageResponse.getTotalPages()); // 무한 스크롤 방식은 -1로 고정
        assertEquals(-1, pageResponse.getTotalElements()); // 무한 스크롤 방식은 -1로 고정
        assertFalse(pageResponse.isHasNext()); // StudyChildComment 페이지 사이즈와 같으므로
        for (GetStudyChildCommentResponse response : pageResponse.getData()) {
            assertEquals(lastChildId--, response.getReplyId());
        }
    }

    @Test
    @DisplayName("스터디 모집 대댓글이 페이지 사이즈보다 커도 대댓글 목록 조회에 성공한다.")
    void getChildren__Success() {
        //given
        for (int i = 0; i < PageSize.StudyChildComment * 2 - 1; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    studyId,
                    parentId,
                    user.getId()
            );
        }
        Long lastChildId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                studyId,
                parentId,
                user.getId()
        ).getCommentId();


        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(parentId, lastChildId);

        //then
        assertEquals(PageSize.StudyChildComment, pageResponse.getNumberOfElements());
        assertTrue(pageResponse.isHasNext()); // StudyChildComment 페이지 사이즈 보다 크므로
        assertEquals(lastChildId - 1, pageResponse.getData().getFirst().getReplyId()); // lastChildId의  다음 ID 값
        assertEquals(lastChildId - PageSize.StudyChildComment, pageResponse.getData().getLast().getReplyId());
    }

    @Test
    @DisplayName("스터디 모집 대댓글이 없어도 대댓글 목록 조회에 성공한다.")
    void getChildren_NoReply_Success() {
        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(parentId, null);

        //then
        assertEquals(0, pageResponse.getNumberOfElements());
        assertTrue(pageResponse.getData().isEmpty());
    }
}
