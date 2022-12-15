package com.ead.course.specifications;

import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.UUID;

public class SpecificationTemplate {

    @And({
            @Spec(path = "courseLevel", spec = Equal.class),
            @Spec(path = "courseStatus", spec = Equal.class),
            @Spec(path = "name", spec = Like.class)
    })
    public interface CourseSpec extends Specification<CourseModel> {
    }

    @Spec(path = "title", spec = Like.class)
    public interface ModuleSpec extends Specification<ModuleModel> {}

    @Spec(path = "title", spec = Like.class)
    public interface LessonSpec extends Specification<LessonModel> {}

    //Nesse caso para o Specification do controller de module a gente vai usar um criteria builder para especificar modulos dentro de um curso
    public static Specification<ModuleModel> moduleCourseId(final UUID courseId){
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Root<ModuleModel> module = root;
            Root<CourseModel> course = query.from(CourseModel.class);
            Expression<Collection<ModuleModel>> courseModules = course.get("modules");
            return criteriaBuilder.and(criteriaBuilder.equal(course.get("courseId"), courseId), criteriaBuilder.isMember(module, courseModules));

        };
    }

    public static Specification<LessonModel> lessonModuleId(final UUID moduleId){
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Root<LessonModel> lesson = root;
            Root<ModuleModel> module = query.from(ModuleModel.class);
            Expression<Collection<LessonModel>> moduleLessons = module.get("lessons");
            return criteriaBuilder.and(criteriaBuilder.equal(module.get("moduleId"), moduleId), criteriaBuilder.isMember(lesson, moduleLessons));

        };
    }

    public static Specification<CourseModel> courseUserId(final UUID userId){
        return (root, query, criteriaBuilder) -> {

            query.distinct(true); //Aqui defino que vou querer apenas dados que não se repetem
            Join<CourseModel, CourseUserModel> courseProd = root.join("coursesUsers"); //Nesta linha faço um simples join, passando as duas entidades e qual relação entre elas
            //EX: SELECT * ALL TB_USERS
            //JOIN TB_USERS_COURSES ON COURSE_ID = COURSE_ID
            return criteriaBuilder.equal(courseProd.get("userId"), userId);
        };
    }

}
