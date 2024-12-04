package com.jj.swm.domain.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jj.swm.domain.study.dto.request.StudyCreateRequest;
import com.jj.swm.domain.study.dto.request.StudyRecruitPositionsCreateRequest;
import com.jj.swm.domain.study.entity.StudyCategory;
import com.jj.swm.domain.study.service.StudyCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(StudyCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudyCommandControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private StudyCommandService studyCommandService;

    @Test
    @DisplayName("스터디 생성 컨트롤러 성공 테스트")
    void createStudy_Success() throws Exception {
        //given
        Mockito.doNothing().when(studyCommandService).create(any(UUID.class), any(StudyCreateRequest.class));

        StudyCreateRequest studyCreateRequest = StudyCreateRequest.builder()
                .title("title")
                .content("content")
                .category(StudyCategory.ALGORITHM)
                .thumbnail("thumbnail")
                .tags(List.of("tag1", "tag2"))
                .imageUrls(List.of("imageUrl1", "imageUrl2"))
                .recruitPositionsCreateRequests(List.of(
                        StudyRecruitPositionsCreateRequest.builder()
                                .title("title1")
                                .headcount(3)
                                .build(),
                        StudyRecruitPositionsCreateRequest.builder()
                                .title("title2")
                                .headcount(2)
                                .build()))
                .build();

        //when & then
        mockMvc.perform(post("/api/v1/study")
                        .principal(() -> UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("정상적으로 생성되었습니다.")));
    }
}
