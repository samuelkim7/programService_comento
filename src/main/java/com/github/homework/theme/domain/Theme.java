package com.github.homework.theme.domain;


import com.github.homework.program.domain.Program;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
    name = "theme_seq_generator",
    sequenceName = "theme_seq", allocationSize = 10)
@EqualsAndHashCode(of = {"id", "name"})
@ToString(of = {"id","name"})
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "theme_seq_generator")
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    public Theme(String name) {
        assert !name.isEmpty() : "empty name";
        this.name = name;
    }

    public void setProgram(Program program) {
        this.program = program;
    }
}
