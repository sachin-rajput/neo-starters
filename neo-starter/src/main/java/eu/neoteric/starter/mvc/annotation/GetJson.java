package eu.neoteric.starter.mvc.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@RequestMapping(method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetJson {

    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] value() default {};
}