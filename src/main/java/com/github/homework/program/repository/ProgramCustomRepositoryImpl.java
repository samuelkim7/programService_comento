package com.github.homework.program.repository;


import com.github.homework.program.domain.Program;
import com.github.homework.program.model.ProgramViewDto;
import com.github.homework.theme.domain.Theme;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.homework.program.domain.QProgram.program;
import static com.github.homework.theme.domain.QTheme.theme;

public class ProgramCustomRepositoryImpl extends QuerydslRepositorySupport implements ProgramCustomRepository {

    public ProgramCustomRepositoryImpl() {
        super(Program.class);
    }

    @Override
    public Page<ProgramViewDto> findBy(Pageable pageable) {
        JPQLQuery<Program> query = Objects.requireNonNull(getQuerydsl())
                .applyPagination(pageable, from(program)
                        .join(program.themes, theme).fetchJoin()
                        .distinct()
                );

        List<ProgramViewDto> collect = query.fetch().stream()
                .map(p -> new ProgramViewDto(
                                p.getId(),
                                p.getName(),
                                p.getIntroduction(),
                                p.getIntroductionDetail(),
                                p.getRegion(),
                                p.getThemes().stream().sorted(Comparator.comparing(Theme::getName)).map(Theme::getName).collect(Collectors.joining(",")),
                                p.getViews()
                        )
                ).collect(Collectors.toList());
        return PageableExecutionUtils.getPage(collect, pageable, query::fetchCount);
    }
}