package com.jj.swm.domain.study.comment.service;

import com.jj.swm.domain.study.comment.dto.ReplyCountInfo;
import com.jj.swm.domain.study.comment.dto.response.FindCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.FindParentCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.CommentRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.PageSize;
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
    public PageResponse<FindParentCommentResponse> findCommentList(Long studyId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyComment,
                Sort.by("id").descending()
        );

        return loadPageParentAndReplyCountResponse(studyId, pageable);
    }

    @Transactional(readOnly = true)
    public PageResponse<FindCommentResponse> findReplyList(Long parentId, Long lastReplyId) {
        List<StudyComment> replyList = commentRepository.findPagedReplyListByParentIdWithUser(
                PageSize.StudyReply + 1,
                parentId,
                lastReplyId
        );

        if (replyList.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = replyList.size() > PageSize.StudyReply;

        List<StudyComment> pagedReplyList = hasNext ? replyList.subList(0, PageSize.StudyReply) : replyList;

        List<FindCommentResponse> responseList = pagedReplyList.stream()
                .map(FindCommentResponse::from)
                .toList();

        return PageResponse.of(responseList, hasNext);
    }

    public PageResponse<FindParentCommentResponse> loadPageParentAndReplyCountResponse(Long studyId, Pageable pageable) {
        Page<StudyComment> pagedComment = commentRepository.findPagedParentByStudyIdWithUser(studyId, pageable);

        List<Long> parentIdList = pagedComment.get()
                .map(StudyComment::getId)
                .toList();

        Map<Long, Integer> replyCountByParentId = commentRepository.countByParentIdListGroupByParentId(parentIdList)
                .stream()
                .collect(Collectors.toMap(ReplyCountInfo::getParentId, ReplyCountInfo::getReplyCount));

        return PageResponse.of(
                pagedComment,
                (comment) -> FindParentCommentResponse.of(comment, replyCountByParentId.getOrDefault(comment.getId(), 0))
        );
    }
}
