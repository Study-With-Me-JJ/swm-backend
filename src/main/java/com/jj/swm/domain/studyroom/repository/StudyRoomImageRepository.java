package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomImage;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomImageRepository extends JpaRepository<StudyRoomImage, Long>, JdbcImageRepository {

    List<StudyRoomImage> findAllByIdInAndStudyRoom(List<Long> imageIds, StudyRoom studyRoom);

    int countStudyRoomImageByIdInAndStudyRoom(List<Long> imageIds, StudyRoom studyRoom);

    void deleteByStudyRoom(StudyRoom studyRoom);

}
