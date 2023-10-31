package io.javarig.generator.collection.set;

import io.javarig.RandomInstanceGenerator;
import io.javarig.generator.collection.CollectionGenerator;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.Set;


@Getter
@Setter
@SuppressWarnings({"rawtypes"})
public abstract class SetGenerator extends CollectionGenerator<Set> {
    public SetGenerator(Type type, RandomInstanceGenerator randomInstanceGenerator) {
        super(type, randomInstanceGenerator);
    }
}
