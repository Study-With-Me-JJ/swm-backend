package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomImage;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomImageRepository extends JpaRepository<StudyRoomImage, Long>, JdbcImageRepository {

    int countStudyRoomImageByIdInAndStudyRoom(List<Long> imageIds, StudyRoom studyRoom);

    List<StudyRoomImage> findAllByStudyRoomId(Long studyRoomId);

    @Modifying
    @Query("delete from StudyRoomImage s where s.studyRoom.id = ?1")
    void deleteAllByStudyRoomId(Long studyRoomId);

    long countByStudyRoomId(Long studyRoomId);
}
