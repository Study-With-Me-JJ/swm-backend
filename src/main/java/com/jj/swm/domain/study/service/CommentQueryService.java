package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.ReplyCountInfo;
import com.jj.swm.domain.study.dto.response.CommentInquiryResponse;
import com.jj.swm.domain.study.dto.response.ReplyInquiryResponse;
import com.jj.swm.domain.study.entity.StudyComment;
import com.jj.swm.domain.study.repository.CommentRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentQueryService {

    @Value("${study.comment.page.size}")
    private int commentPageSize;

    @Value("${study.reply.page.size}")
    private int replyPageSize;

    private final CommentRepository commentRepository;

    public PageResponse<CommentInquiryResponse> getList(Long studyId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, commentPageSize, Sort.by("id").descending());

        return getCommentPageResponse(studyId, pageable);
    }

    public PageResponse<ReplyInquiryResponse> getReplyList(Long parentId, Long lastReplyId) {
        List<StudyComment> replies = commentRepository.findAllWithUserByParentId(
                replyPageSize + 1,
                parentId,
                lastReplyId
        );

        if (replies.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "replies not found");
        }

        boolean hasNext = replies.size() > replyPageSize;

        List<StudyComment> pagedReplies = hasNext ? replies.subList(0, replyPageSize) : replies;

        List<ReplyInquiryResponse> replyInquiryResponses = pagedReplies.stream()
                .map(ReplyInquiryResponse::from)
                .toList();

        return PageResponse.of(replyInquiryResponses, hasNext);
    }

    protected PageResponse<CommentInquiryResponse> getCommentPageResponse(Long studyId, Pageable pageable) {
        Page<StudyComment> pageComments = commentRepository.findCommentWithUserByStudyId(studyId, pageable);

        List<Long> parentIds = pageComments.get().map(StudyComment::getId).toList();

        Map<Long, Integer> replyCountByParentId = commentRepository.countReplyByParentId(parentIds).stream()
                .collect(Collectors.toMap(ReplyCountInfo::getParentId, ReplyCountInfo::getReplyCount));

        List<CommentInquiryResponse> commentInquiryResponses = pageComments.get()
                .map(comment -> CommentInquiryResponse.of(
                        comment, replyCountByParentId.getOrDefault(comment.getId(), 0))
                ).toList();

        return PageResponse.of(
                pageComments.getNumberOfElements(),
                pageComments.getTotalPages(),
                pageComments.getTotalElements(),
                pageComments.hasNext(),
                commentInquiryResponses
        );
    }
}
