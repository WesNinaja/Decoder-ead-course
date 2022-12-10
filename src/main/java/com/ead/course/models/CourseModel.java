package com.ead.course.models;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_COURSES")
public class CourseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID courseId;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, length = 250)
    private String description;
    @Column
    private String imageUrl;
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime creationDate;
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastUpdateDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseStatus courseStatus;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseLevel courseLevel;
    @Column(nullable = false)
    private UUID userInstructor;
    /***
     * Um curso para vários módulos
     * Vou passar em módulo qual vai ser a chave estrangeira, desssa forma o Hibernate vai criar a associação
     * Usamos o SET no caso, porque assim o Hibernate numa consulta de course, pode trazer vários tipos de coleções, diferente do list
     * O Hibernate não consegue lidar com list, e gera multiplas querys desnecessárias
     * Com o writeOnly ele só vai mostrar a lista quando houve uma escrita, vai mostrar esse campo quando tiver uma deserialização com escrita
     * <p>
     * Fetch type laze para carregar apenas dados do tipo module quando eu realmente for utilizá-lo
     * E com o orphanRemoval eu informo que se tiver algum modulo que não tiver vinculo com curso, ele tbm vai ser deletado.
     * Com o CascadeType.ALL eu delego ao jpa para deletar todos os modulos relacionados ao curso. Só que desse jeito a performance é pior, pq faço um comando pra deletar curso, e mais outros comandos pra deletar cada modulo.
     * Vamos delegar pra nossa aplicação fazer essa deleção com o @Transactional, assim vamos ter um controle maior do que está acontecendo
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<ModuleModel> modules;

}
