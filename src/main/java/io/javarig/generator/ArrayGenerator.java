package io.javarig.generator;

import io.javarig.RandomInstanceGenerator;
import io.javarig.exception.InstanceGenerationException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

@Setter
@Getter
public class ArrayGenerator extends AbstractTypeGenerator implements CollectionGenerator {
    private int minSizeInclusive = DEFAULT_MIN_SIZE_INCLUSIVE;
    private int maxSizeExclusive = DEFAULT_MAX_SIZE_EXCLUSIVE;

    public ArrayGenerator(Type type, RandomInstanceGenerator randomInstanceGenerator) {
        super(type, randomInstanceGenerator);
    }


    @Override
    public Object generate() throws InstanceGenerationException {
        Class<?> arrayParameterType = ((Class<?>) getType()).componentType();
        List<Object> objectList = getRandomInstanceGenerator()
                .generate(List.class, getMinSizeInclusive(), getMaxSizeExclusive(), arrayParameterType);
        Object[] arrayInstance = (Object[]) Array.newInstance(arrayParameterType, 0);
        return objectList.toArray(arrayInstance);
    }
}
