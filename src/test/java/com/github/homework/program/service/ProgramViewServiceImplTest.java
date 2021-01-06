package com.github.homework.program.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import com.github.homework.program.domain.Program;
import com.github.homework.program.model.ProgramViewDto;
import com.github.homework.program.repository.ProgramRepository;
import com.github.homework.theme.domain.Theme;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ProgramViewServiceImplTest {

    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private ProgramViewServiceImpl programViewService;

    @Test
    @DisplayName("프로그램이 한개 일때")
    void getByTest() {
        //given
        Program program = Program.builder()
                .name("name")
                .introduction("introduction")
                .introductionDetail("introductionDetail")
                .region("region")
                .themes(Set.of(new Theme("theme1"), new Theme("theme2")))
                .views(0L)
                .build();

        /* Mock을 사용하고 programRepository에 대한 의존성을 없앰 */
        given(this.programRepository.findById(1L)).willReturn(Optional.of(program));
        //when
        Optional<ProgramViewDto> optionalProgramViewDto = this.programViewService.getBy(1L);
        //then
        then(optionalProgramViewDto).hasValueSatisfying(programViewDto -> {
                    then(programViewDto.getName()).isEqualTo("name");
                    then(programViewDto.getIntroduction()).isEqualTo("introduction");
                    then(programViewDto.getIntroductionDetail()).isEqualTo("introductionDetail");
                    then(programViewDto.getRegion()).isEqualTo("region");
                    then(programViewDto.getThemeName()).isEqualTo("theme2,theme1");
                    then(programViewDto.getViews()).isEqualTo(1L);
                }
        );

    }


    @Test
    @DisplayName("프로그램 한개 이름으로 조회시")
    void getByNameTest() {
        //given
        Program program = Program.builder()
                .name("name")
                .introduction("introduction")
                .introductionDetail("introductionDetail")
                .region("region")
                .themes(Set.of(new Theme("theme1"), new Theme("theme2")))
                .views(0L)
                .build();

        given(this.programRepository.findByName("name")).willReturn(Optional.of(program));
        //when
        Optional<ProgramViewDto> optionalProgramViewDto = this.programViewService.getByName("name");
        //then
        then(optionalProgramViewDto).hasValueSatisfying(programViewDto -> {
                    then(programViewDto.getName()).isEqualTo("name");
                    then(programViewDto.getIntroduction()).isEqualTo("introduction");
                    then(programViewDto.getIntroductionDetail()).isEqualTo("introductionDetail");
                    then(programViewDto.getRegion()).isEqualTo("region");
                    then(programViewDto.getThemeName()).isEqualTo("theme2,theme1");
                    then(programViewDto.getViews()).isEqualTo(1L);
                }
        );
    }

    @Test
    @DisplayName("프로그램 한개 이름으로 검색 결과 없음")
    void getByNameEmptyTest() {
        //given
        //when
        Optional<ProgramViewDto> optionalProgramViewDto = this.programViewService.getByName("name");
        //then
        then(optionalProgramViewDto).isEmpty();
    }

    @Test
    @DisplayName("프로그램이 여러개 일때")
    void pageByTest() {
        //given
        ProgramViewDto programViewDto = new ProgramViewDto(1L, "name", "introduction", "introductionDetail",
                "region", "theme1", 0L);
        given(this.programRepository.findBy(PageRequest.of(0, 100)))
                .willReturn(
                        new PageImpl<>(List.of(programViewDto, programViewDto))
                );

        //when
        Page<ProgramViewDto> programViewDtos = this.programViewService.pageBy(PageRequest.of(0, 100));
        //then
        then(programViewDtos.getContent()).hasSize(2);
        then(programViewDtos.getContent()).allSatisfy(p -> {
                    then(p.getId()).isGreaterThan(0L);
                    then(p.getName()).isEqualTo("name");
                    then(p.getIntroduction()).isEqualTo("introduction");
                    then(p.getIntroductionDetail()).isEqualTo("introductionDetail");
                    then(p.getRegion()).isEqualTo("region");
                    then(p.getThemeName()).isEqualTo("theme1");
                    then(p.getViews()).isEqualTo(0L);
                }
        );
    }

    @Test
    @DisplayName("인기 프로그램 10개 조회")
    void getTopTenTest() {
        //given
        List<Program> programs = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            Program program = Program.builder()
                    .name("name" + i)
                    .introduction("introduction")
                    .introductionDetail("introductionDetail")
                    .region("region")
                    .themes(Set.of(new Theme("theme")))
                    .views(10L - i)
                    .build();
            programs.add(program);
        });
        given(this.programRepository.findTop10ByOrderByViewsDesc()).willReturn(programs);
        //when
        List<ProgramViewDto> programViewDtos = this.programViewService.getTopTen();
        //then
        then(programViewDtos.size()).isEqualTo(10);
        int i = 0;
        for(ProgramViewDto p : programViewDtos) {
            then(p.getName()).isEqualTo("name" + i);
            then(p.getIntroduction()).isEqualTo("introduction");
            then(p.getIntroductionDetail()).isEqualTo("introductionDetail");
            then(p.getRegion()).isEqualTo("region");
            then(p.getThemeName()).isEqualTo("theme");
            then(p.getViews()).isEqualTo(10L - i);
            i++;
        }
    }

}