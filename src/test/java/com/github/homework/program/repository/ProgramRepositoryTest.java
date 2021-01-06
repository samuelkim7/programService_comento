package com.github.homework.program.repository;

import static org.assertj.core.api.BDDAssertions.then;

import com.github.homework.program.domain.Program;
import com.github.homework.program.model.ProgramViewDto;
import com.github.homework.theme.domain.Theme;
import com.github.homework.theme.repository.ThemeRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(showSql = false)
@Transactional
class ProgramRepositoryTest {

    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("프로그램이 여러건일때 테스트")
    public void findByPageTest() {
        //given
        IntStream.range(0, 20).forEach(i -> {
                Program program = Program.builder()
                    .name("name")
                    .introduction("introduction")
                    .introductionDetail("introductionDetail")
                    .region("region")
                    .themes(Set.of(new Theme("theme" + i)))
                    .views(0L)
                    .build();

                testEntityManager.persist(program);
                testEntityManager.flush();
                testEntityManager.clear();
            }
        );

        //when
        Page<ProgramViewDto> programViewDtos = programRepository.findBy(PageRequest.of(0, 2));
        //then
        then(programViewDtos.getContent()).hasSize(2);
        then(programViewDtos.getContent()).allSatisfy(programViewDto -> {
                then(programViewDto.getId()).isGreaterThan(0L);
                then(programViewDto.getName()).isEqualTo("name");
                then(programViewDto.getIntroduction()).isEqualTo("introduction");
                then(programViewDto.getIntroductionDetail()).isEqualTo("introductionDetail");
                then(programViewDto.getRegion()).isEqualTo("region");
                then(programViewDto.getThemeName()).contains("theme");
                then(programViewDto.getViews()).isEqualTo(0L);
            }
        );

    }

    @Test
    @DisplayName("프로그램 이름으로 조회 테스트")
    void findByNameTest() {
        //given
        Theme theme1 = new Theme("theme1");
        Theme theme2 = new Theme("theme2");
        Program program = Program.builder()
                .name("name")
                .introduction("introduction")
                .introductionDetail("introductionDetail")
                .region("region")
                .themes(Set.of(theme1, theme2))
                .views(0L)
                .build();

        testEntityManager.persist(program);
        testEntityManager.flush();
        testEntityManager.clear();

        //when
        Optional<Program> optionalProgram = programRepository.findByName("name");
        //then
        then(optionalProgram).hasValueSatisfying(p -> {
                    then(p.getName()).isEqualTo("name");
                    then(p.getIntroduction()).isEqualTo("introduction");
                    then(p.getIntroductionDetail()).isEqualTo("introductionDetail");
                    then(p.getRegion()).isEqualTo("region");
                    then(p.getThemes()).containsOnly(theme2, theme1);
                    then(p.getViews()).isEqualTo(0L);
                }
        );
    }

    @Test
    @DisplayName("인기 프로그램 상위 10개 조회 테스트")
    void findTop10ByOrderByViewsDescTest() {
        //given
        IntStream.range(0, 20).forEach(i -> {
                    Program program = Program.builder()
                            .name("name" + i)
                            .introduction("introduction")
                            .introductionDetail("introductionDetail")
                            .region("region")
                            .themes(Set.of(new Theme("theme" + i)))
                            .views(0L + i)
                            .build();
                    testEntityManager.persist(program);
                    testEntityManager.flush();
                    testEntityManager.clear();
                }
        );
        //when
        List<Program> programs = programRepository.findTop10ByOrderByViewsDesc();
        //then
        then(programs).hasSize(10);
        int i = 0;
        for(Program p : programs) {
            then(p.getName()).isEqualTo("name" + (19 - i));
            then(p.getViews()).isEqualTo(19L - i);
            i++;
        }
    }
}