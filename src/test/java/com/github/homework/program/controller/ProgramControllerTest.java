package com.github.homework.program.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.homework.common.BaseControllerTest;
import com.github.homework.program.domain.Program;
import com.github.homework.program.model.ProgramSaveDto;
import com.github.homework.program.repository.ProgramRepository;
import com.github.homework.theme.domain.Theme;
import com.github.homework.theme.repository.ThemeRepository;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class ProgramControllerTest extends BaseControllerTest {
    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    @DisplayName("프로그램 단건 조회")
    public void getProgramTest() throws Exception {
        Program program = givenProgram(Set.of(givenTheme("식도락여행")));
        this.mockMvc.perform(get("/api/programs/{id}", program.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").isNumber())
            .andExpect(jsonPath("name").value("여수 10미 먹거리"))
            .andExpect(jsonPath("introduction").value("여수시 일대 게장백반, 돌산갓김치등"))
            .andExpect(jsonPath("introductionDetail").value("여행자와 현지인이 꼽은 최고의 먹거리 여행지' 에서 대한민국 229개 지방자치단체 중 여수시가 1위에 선정되어 식도락 여행에 최적화된 프로그램"))
            .andExpect(jsonPath("region").value("전라남도 여수시"))
            .andExpect(jsonPath("themeName").value("식도락여행"))
            .andExpect(jsonPath("views").value(1L))
            .andDo(write.document(
                pathParameters(
                    parameterWithName("id").description("id")
                )
            ));
    }

    @Test
    @DisplayName("프로그램 단건 조회 결과 없음")
    public void getProgramFailTest() throws Exception {
        this.mockMvc.perform(get("/api/programs/{id}", 30L))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("프로그램 단건 이름으로 조회")
    public void getByNameTest() throws Exception {
        Program program = givenProgram(Set.of(givenTheme("식도락여행")));
        this.mockMvc.perform(get("/api/programs/byName").param("name", program.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNumber())
                .andExpect(jsonPath("name").value("여수 10미 먹거리"))
                .andExpect(jsonPath("introduction").value("여수시 일대 게장백반, 돌산갓김치등"))
                .andExpect(jsonPath("introductionDetail").value("여행자와 현지인이 꼽은 최고의 " +
                        "먹거리 여행지' 에서 대한민국 229개 지방자치단체 중 여수시가 1위에 선정되어 식도락 여행에 최적화된 프로그램"))
                .andExpect(jsonPath("region").value("전라남도 여수시"))
                .andExpect(jsonPath("themeName").value("식도락여행"))
                .andExpect(jsonPath("views").value(1L))
                .andDo(write.document(
                        requestParameters(
                                parameterWithName("name").description("이름")
                        )
                ));
    }

    @Test
    @DisplayName("프로그램 단건 이름으로 조회 결과 없음")
    public void getByNameFailTest() throws Exception {
        this.mockMvc.perform(get("/api/programs/byName").param("name", "name"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("프로그램 page 조회")
    public void getPageProgramTest() throws Exception {
        givenProgram(Set.of(givenTheme("식도락여행"),givenTheme("즐거운여행")));
        this.mockMvc.perform(get("/api/programs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..id").isNotEmpty())
            .andExpect(jsonPath("$..name").value("여수 10미 먹거리"))
            .andExpect(jsonPath("$..introduction").value("여수시 일대 게장백반, 돌산갓김치등"))
            .andExpect(jsonPath("$..introductionDetail").value("여행자와 현지인이 꼽은 최고의 먹거리 여행지' 에서 대한민국 229개 지방자치단체 중 여수시가 1위에 선정되어 식도락 여행에 최적화된 프로그램"))
            .andExpect(jsonPath("$..region").value("전라남도 여수시"))
            .andExpect(jsonPath("$..themeName").value("식도락여행,즐거운여행"))
            .andExpect(jsonPath("totalPages").value("1"))
            .andExpect(jsonPath("totalElements").value("1"));
    }

    @Test
    @DisplayName(("인기 프로그램 상위 10개 조회"))
    public void getTopTenTest() throws Exception{
        IntStream.range(0, 20).forEach(i -> {
                    Program program = givenProgram(Set.of(givenTheme("즐거운여행" + i)));
                    IntStream.range(0, i).forEach(j -> {
                        program.updateViews();
                    });
                    programRepository.save(program);
                }
        );
        this.mockMvc.perform(get("/api/programs/topTen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].name").value("여수 10미 먹거리"))
                .andExpect(jsonPath("$[0].introduction").value("여수시 일대 게장백반, 돌산갓김치등"))
                .andExpect(jsonPath("$[0].introductionDetail").value("여행자와 현지인이 꼽은 최고의 먹거리 여행지' 에서 대한민국 229개 지방자치단체 중 여수시가 1위에 선정되어 식도락 여행에 최적화된 프로그램"))
                .andExpect(jsonPath("$[0].region").value("전라남도 여수시"))
                .andExpect(jsonPath("$[0].themeName").value("즐거운여행19"))
                .andExpect(jsonPath("$[0].views").value(19))
                .andExpect(jsonPath("$[1].themeName").value("즐거운여행18"))
                .andExpect(jsonPath("$[1].views").value(18));
    }

    @Test
    @DisplayName("프로그램 저장 정상 케이스")
    public void saveProgramTest() throws Exception {
        ProgramSaveDto programSaveDto = ProgramSaveDto.builder().name("여수 10미 먹거리")
            .region("전라남도 여수시").themeName("식도락여행")
            .introduction("여수시 일대 게장백반, 돌산갓김치등")
            .introductionDetail(
                "여행자와 현지인이 꼽은 최고의 먹거리 여행지' 에서 대한민국 229개 지방자치단체 중 여수시가 1위에 선정되어 식도락 여행에 최적화된 프로그램")
            .build();
        this.mockMvc.perform(post("/api/programs/").contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(programSaveDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("success").value("true"))
            .andExpect(jsonPath("message").value("저장 성공"))
            .andDo(write.document(
                requestFields(
                    fieldWithPath("name").description("프로그램 이름"),
                    fieldWithPath("introduction").description("소개"),
                    fieldWithPath("introductionDetail").description("상세 소개"),
                    fieldWithPath("region").description("지역 이름"),
                    fieldWithPath("themeName").description("테마 이름")
            )
        ));
    }

    @Test
    @DisplayName("프로그램 저장 name이 없는 케이스")
    public void saveProgramFailTest() throws Exception {
        ProgramSaveDto programSaveDto = ProgramSaveDto.builder()
            .region("전라남도 여수시").themeName("식도락여행")
            .introduction("여수시 일대 게장백반, 돌산갓김치등")
            .introductionDetail(
                "여행자와 현지인이 꼽은 최고의 먹거리 여행지' 에서 대한민국 229개 지방자치단체 중 여수시가 1위에 선정되어 식도락 여행에 최적화된 프로그램")
            .build();
        this.mockMvc.perform(post("/api/programs/").contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(programSaveDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("[0].field").value("name"))
            .andExpect(jsonPath("[0].objectName").value("programSaveDto"))
            .andExpect(jsonPath("[0].code").value("NotBlank"))
            .andExpect(jsonPath("[0].defaultMessage").value("must not be blank"));

    }

    @Test
    @DisplayName("프로그램 수정 정상 케이스")
    public void updateProgramTest() throws Exception {
        Program program = givenProgram(Set.of(givenTheme("식도락여행"),givenTheme("즐거운여행")));
        ProgramSaveDto programSaveDto = ProgramSaveDto.builder().id(program.getId()).name("여수 10미 먹거리2")
            .region("전라남도 여수시").themeName("식도락여행2")
            .introduction(program.getIntroduction())
            .introductionDetail(program.getIntroductionDetail())
            .build();

        this.mockMvc.perform(put("/api/programs/").contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(programSaveDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(write.document(
            requestFields(
                fieldWithPath("id").description("id"),
                fieldWithPath("name").description("프로그램 이름"),
                fieldWithPath("introduction").description("소개"),
                fieldWithPath("introductionDetail").description("상세 소개"),
                fieldWithPath("region").description("지역 이름"),
                fieldWithPath("themeName").description("테마 이름")
            )));
    }

    @Test
    @DisplayName("프로그램 수정 없는 id로 요청 케이스")
    public void updateProgramFailTest() throws Exception {
        ProgramSaveDto programSaveDto = ProgramSaveDto.builder().id(10L)
            .name("여수 10미 먹거리2")
            .region("전라남도 여수시").themeName("식도락여행2")
            .introduction("여수시 일대 게장백반, 돌산갓김치등")
            .introductionDetail("여행자와 현지인이 꼽은 최고의 먹거리 여행지' 에서 대한민국 229개 지방자치단체 중 여수시가 1위에 선정되어 식도락 여행에 최적화된 프로그램")
            .build();
        this.mockMvc.perform(put("/api/programs/").contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(programSaveDto)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    private Program givenProgram(Set<Theme> themes) {
        Program program = Program.builder().name("여수 10미 먹거리")
            .region("전라남도 여수시")
            .introduction("여수시 일대 게장백반, 돌산갓김치등")
            .introductionDetail(
                "여행자와 현지인이 꼽은 최고의 먹거리 여행지' 에서 대한민국 229개 지방자치단체 중 여수시가 1위에 선정되어 식도락 여행에 최적화된 프로그램")
            .themes(themes)
            .views(0L)
            .build();
        return this.programRepository.save(program);
    }
    private Theme givenTheme(String name) {
        return this.themeRepository.save(new Theme(name));
    }
}