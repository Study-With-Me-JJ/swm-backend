package com.jj.swm.domain.studyroom.core.repository;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomTag;
import com.jj.swm.domain.studyroom.core.repository.jdbc.JdbcTagRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomTagRepository extends JpaRepository<StudyRoomTag, Long>, JdbcTagRepository {

    int countStudyRoomTagByIdInAndStudyRoom(List<Long> tagIds, StudyRoom studyRoom);

    @Modifying
    @Query("delete from StudyRoomTag s where s.studyRoom.id = ?1")
    void deleteAllByStudyRoomId(Long studyRoomId);

    long countByStudyRoomId(Long studyRoomId);
}
