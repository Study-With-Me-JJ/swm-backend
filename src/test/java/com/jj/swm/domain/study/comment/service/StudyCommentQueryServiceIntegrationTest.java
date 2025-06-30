package com.jj.swm.domain.study.comment.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.response.GetStudyChildCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.GetStudyParentCommentResponse;
import com.jj.swm.domain.study.comment.fixture.dto.request.UpsertStudyCommentRequestFixture;
import com.jj.swm.domain.study.comment.repository.StudyCommentRepository;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.core.support.StudyTestRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.constants.PageSize;
import com.jj.swm.global.common.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadLocalRandom;

import static com.jj.swm.domain.study.support.TestConstants.FIRST_PAGE;
import static com.jj.swm.domain.study.support.TestConstants.NON_EXISTING_ID;
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

    @Autowired
    private StudyTestRepository studyRepository;

    @Autowired
    private StudyCommentRepository commentRepository;

    // entity
    private User user;
    private Long parentId;
    private Study study;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());

        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        Study oldStudy = studyRepository.findFirstByOrderByCreatedAt().orElseThrow();

        parentId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                oldStudy.getId(),
                null,
                user.getId()
        ).getCommentId();

        study = studyRepository.findById(oldStudy.getId()).orElseThrow();
    }

    @Test
    @DisplayName("스터디 모집 댓글 목록 조회에 성공한다.")
    void getParents_Success() {
        //given
        for (int i = 0; i < PageSize.StudyParentComment; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    null,
                    user.getId()
            );
        }

        long commentCount = commentRepository.count();

        //when
        PageResponse<GetStudyParentCommentResponse> pageResponse = commentQueryService.getParents(
                study.getId(), FIRST_PAGE
        );

        //then
        assertEquals(PageSize.StudyParentComment, pageResponse.getNumberOfElements());
        assertEquals((commentCount - 1) / PageSize.StudyParentComment + 1, pageResponse.getTotalPages());
        assertEquals(commentCount, pageResponse.getTotalElements());
        assertEquals(commentCount > PageSize.StudyParentComment, pageResponse.isHasNext());
        for (GetStudyParentCommentResponse response : pageResponse.getData()) {
            assertEquals(commentCount--, response.getCommentId());
            assertEquals(UpsertStudyCommentRequestFixture.create().getContent(), response.getContent());
            assertEquals(0, response.getReplyCount()); // 대댓글은 생성 안 했으므로
        }
    }

    @Test
    @DisplayName("마지막 페이지 스터디 모집 댓글 목록 조회에 성공한다.")
    void getParents_LastPage_Success() {
        //given
        for (int i = 0; i < PageSize.StudyParentComment; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    null,
                    user.getId()
            );
        }

        long commentCount = commentRepository.count();

        int lastPageNo = (int) ((commentCount - 1) / PageSize.StudyParentComment);

        //when
        PageResponse<GetStudyParentCommentResponse> pageResponse = commentQueryService.getParents(
                study.getId(), lastPageNo
        );

        //then
        assertEquals(commentCount - (long) lastPageNo * PageSize.StudyParentComment, pageResponse.getNumberOfElements());
        assertEquals(1L, pageResponse.getData().getLast().getCommentId()); // ID 순이므로 마지막 페이지의 마지막 데이터는 1L
        assertFalse(pageResponse.isHasNext());
    }

    @Test
    @DisplayName("대댓글이 존재하는 스터디 모집 댓글 목록 조회에 성공한다.")
    void getParents_HavingReply_Success() {
        //given
        Long firstParentId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
                null,
                user.getId()
        ).getCommentId();

        int expectedReplyCountOfFirstParent = 0;
        int expectedReplyCountOfLastParent = 0;

        for (int i = 0; i < PageSize.StudyChildComment; i++) {
            Long targetParentId = ThreadLocalRandom.current().nextBoolean() ? parentId : firstParentId;

            if (targetParentId.equals(parentId)) expectedReplyCountOfLastParent++;
            else expectedReplyCountOfFirstParent++;

            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    targetParentId,
                    user.getId()
            );
        }

        //when
        PageResponse<GetStudyParentCommentResponse> pageResponse = commentQueryService.getParents(
                study.getId(), FIRST_PAGE
        );

        //then
        assertEquals(firstParentId, pageResponse.getData().getFirst().getCommentId());
        assertEquals(expectedReplyCountOfFirstParent, pageResponse.getData().getFirst().getReplyCount());
        assertEquals(parentId, pageResponse.getData().getLast().getCommentId());
        assertEquals(expectedReplyCountOfLastParent, pageResponse.getData().getLast().getReplyCount());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 목록 조회에 성공한다.")
    void getChildren_Success() {
        //given
        for (int i = 0; i < PageSize.StudyChildComment - 1; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    parentId,
                    user.getId()
            );
        }
        Long firstChildId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
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
            assertEquals(firstChildId--, response.getReplyId());
            assertEquals(UpsertStudyCommentRequestFixture.create().getContent(), response.getContent());
        }
    }

    @Test
    @DisplayName("커서 값이 지정되어도 대댓글 목록 조회에 성공한다.")
    void getChildren_WithCursorValue_Success() {
        //given
        for (int i = 0; i < PageSize.StudyChildComment - 1; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    parentId,
                    user.getId()
            );
        }
        Long firstChildId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
                parentId,
                user.getId()
        ).getCommentId();

        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(parentId, firstChildId);

        //then
        assertEquals(PageSize.StudyChildComment - 1, pageResponse.getNumberOfElements());
        assertEquals(firstChildId - 1, pageResponse.getData().getFirst().getReplyId()); // lastChildId의  다음 ID 값
    }

    @Test
    @DisplayName("스터디 모집 대댓글이 페이지 사이즈보다 커도 대댓글 목록 조회에 성공한다.")
    void getChildren_MoreThanPageSize_Success() {
        //given
        for (int i = 0; i < PageSize.StudyChildComment * 2 - 1; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    parentId,
                    user.getId()
            );
        }
        Long firstChildId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
                parentId,
                user.getId()
        ).getCommentId();

        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(parentId, null);

        //then
        assertEquals(PageSize.StudyChildComment, pageResponse.getNumberOfElements());
        assertTrue(pageResponse.isHasNext()); // StudyChildComment 페이지 사이즈 보다 크므로
        assertEquals(firstChildId - PageSize.StudyChildComment + 1, pageResponse.getData().getLast().getReplyId());
    }

    @Test
    @DisplayName("스터디 모집 대댓글이 없어도 대댓글 목록 조회에 성공한다.")
    void getChildren_NoReply_Success() {
        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(NON_EXISTING_ID, null);

        //then
        assertEquals(0, pageResponse.getNumberOfElements());
        assertTrue(pageResponse.getData().isEmpty());
    }
}
