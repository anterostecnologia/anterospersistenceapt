package br.com.anteros.persistence.apt;

import java.lang.annotation.Annotation;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;

import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.MappedSuperclass;
import br.com.anteros.persistence.metadata.annotation.Transient;


@SupportedAnnotationTypes("br.com.anteros.persistence.metadata.annotation.*")
public class AnterosPersistenceAnnotationProcessor extends AbstractDslProcessor {

    @Override
    protected Configuration createConfiguration(RoundEnvironment roundEnv) {
        Class<? extends Annotation> entity = Entity.class;
        Class<? extends Annotation> superType = MappedSuperclass.class;
        Class<? extends Annotation> embeddable = null;
        Class<? extends Annotation> embedded = null;
        Class<? extends Annotation> skip = Transient.class;
        return new AnterosAptConfiguration(roundEnv, processingEnv.getOptions(),
                entity, superType, embeddable, embedded, skip);
    }

}
