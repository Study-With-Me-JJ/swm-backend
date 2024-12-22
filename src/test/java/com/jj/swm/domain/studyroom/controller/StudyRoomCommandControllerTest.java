package com.jj.swm.domain.studyroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jj.swm.domain.studyroom.StudyRoomCreateRequestFixture;
import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomRequest;
import com.jj.swm.domain.studyroom.service.StudyRoomCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudyRoomCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudyRoomCommandControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudyRoomCommandService commandService;


    @Test
    @DisplayName("컨트롤러에서 스터디 생성에 성공한다.")
    void createStudy_Success() throws Exception{
        //given
        CreateStudyRoomRequest request = StudyRoomCreateRequestFixture.createStudyRoomCreateRequestFixture();

        doNothing().when(commandService).create(any(CreateStudyRoomRequest.class), any(UUID.class));

        //when & then
        mockMvc.perform(post("/api/v1/studyroom")
                        .principal(() -> UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("정상적으로 생성되었습니다."));

    }

    @Test
    @DisplayName("스터디 생성 시 잘못된 데이터가 들어오면 실패한다.")
    public void createStudy_FailByNotValidData() throws Exception{
        //given
        CreateStudyRoomRequest request = CreateStudyRoomRequest.builder()
                        .title("Not Valid")
                        .build();

        doNothing().when(commandService).create(any(CreateStudyRoomRequest.class), any(UUID.class));

        //when & then
        mockMvc.perform(post("/api/v1/studyroom")
                        .principal(() -> UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

