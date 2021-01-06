package com.github.homework.program.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProgram is a Querydsl query type for Program
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QProgram extends EntityPathBase<Program> {

    private static final long serialVersionUID = -777124316L;

    public static final QProgram program = new QProgram("program");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath introduction = createString("introduction");

    public final StringPath introductionDetail = createString("introductionDetail");

    public final StringPath name = createString("name");

    public final StringPath region = createString("region");

    public final SetPath<com.github.homework.theme.domain.Theme, com.github.homework.theme.domain.QTheme> themes = this.<com.github.homework.theme.domain.Theme, com.github.homework.theme.domain.QTheme>createSet("themes", com.github.homework.theme.domain.Theme.class, com.github.homework.theme.domain.QTheme.class, PathInits.DIRECT2);

    public final NumberPath<Long> views = createNumber("views", Long.class);

    public QProgram(String variable) {
        super(Program.class, forVariable(variable));
    }

    public QProgram(Path<? extends Program> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProgram(PathMetadata metadata) {
        super(Program.class, metadata);
    }

}

