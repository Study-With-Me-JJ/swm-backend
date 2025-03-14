package com.jj.swm.domain.study.comment.service;

import com.jj.swm.domain.study.comment.dto.ReplyCountInfo;
import com.jj.swm.domain.study.comment.dto.response.GetCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.GetParentCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.CommentRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.constants.PageSize;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public PageResponse<GetParentCommentResponse> getComments(Long studyId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyComment,
                Sort.by("id").descending()
        );

        return getPageParentAndReplyCountResponse(studyId, pageable);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetCommentResponse> getReplies(Long parentId, Long lastReplyId) {
        List<StudyComment> replies = commentRepository.findPagedReplyByParentIdWithUser(
                PageSize.StudyReply + 1,
                parentId,
                lastReplyId
        );

        if (replies.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = replies.size() > PageSize.StudyReply;

        List<StudyComment> pagedReply = hasNext ? replies.subList(0, PageSize.StudyReply) : replies;

        List<GetCommentResponse> responses = pagedReply.stream()
                .map(GetCommentResponse::from)
                .toList();

        return PageResponse.of(responses, hasNext);
    }

    public PageResponse<GetParentCommentResponse> getPageParentAndReplyCountResponse(Long studyId, Pageable pageable) {
        Page<StudyComment> pagedComment = commentRepository.findPagedParentByStudyIdWithUser(studyId, pageable);

        List<Long> parentIds = pagedComment.get()
                .map(StudyComment::getId)
                .toList();

        Map<Long, Integer> replyCountByParentId = commentRepository.countByParentIdsGroupByParentId(parentIds)
                .stream()
                .collect(Collectors.toMap(ReplyCountInfo::getParentId, ReplyCountInfo::getReplyCount));

        return PageResponse.of(
                pagedComment,
                (comment) -> GetParentCommentResponse.of(comment, replyCountByParentId.getOrDefault(comment.getId(), 0))
        );
    }
}
