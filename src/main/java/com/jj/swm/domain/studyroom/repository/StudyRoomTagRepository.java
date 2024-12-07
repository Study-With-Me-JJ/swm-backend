package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomTag;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcTagRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomTagRepository extends JpaRepository<StudyRoomTag, Long>, JdbcTagRepository {

    List<StudyRoomTag> findAllByIdInAndStudyRoom(List<Long> tagIds, StudyRoom studyRoom);

    int countStudyRoomTagByIdInAndStudyRoom(List<Long> tagIds, StudyRoom studyRoom);

    @Modifying
    @Query("update StudyRoomTag s set s.deletedAt = CURRENT_TIMESTAMP where s.id in :tagIds")
    void deleteAllByIdInBatch(@Param("tagIds") List<Long> tagIds);

}
