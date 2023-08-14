package io.javarig.generation;

import io.javarig.RandomInstanceGenerator;
import io.javarig.exception.*;
import io.javarig.generator.CollectionGenerator;
import io.javarig.testclasses.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class ObjectGenerationTest {
    private RandomInstanceGenerator randomInstanceGenerator;

    @BeforeEach
    public void setUp() {
        randomInstanceGenerator = new RandomInstanceGenerator();
    }

    @Test
    public void shouldReturnAnObjectInstance() {
        //when
        Object generated = randomInstanceGenerator.generate(TestClass.class);
        //then
        log.info("shouldReturnAnObjectInstance : {}", generated);
        assertThat(generated).isNotNull();
        assertThat(generated).isInstanceOf(TestClass.class);
    }

    @Test
    public void shouldThrowNoDefaultConstructorExceptionWhenGivenAClassWithNoDefaultConstructor() {
        //given
        Class<?> type = ClassWithNoDefaultConstructor.class;

        //then
        assertThatThrownBy(() -> {//when
            randomInstanceGenerator.generate(type);
        }).isInstanceOf(NoAccessibleDefaultConstructorException.class).hasMessage("Class %s does not have default constructor, or it's not accessible".formatted(type.getName()));
    }

    @Test
    public void shouldLetFieldNullWhenGivenAClassWithASetterWithNoFieldAssociatedToIt() {
        //given
        Type type = ClassWithNoFieldAssociatedToSetter.class;
        //when
        ClassWithNoFieldAssociatedToSetter generated = randomInstanceGenerator.generate(type);
        //then
        assertThat(generated).isNotNull().extracting(ClassWithNoFieldAssociatedToSetter::getS).isNull();
        assertThat(generated).extracting(ClassWithNoFieldAssociatedToSetter::getA).isNotNull();
    }

    @Test
    public void shouldThrowNoAccessibleDefaultConstructorExceptionWhenGivenAClassWithAPrivateConstructor() {
        //given
        Type type = ClassWithPrivateDefaultConstructor.class;

        //then
        assertThatThrownBy(() -> {//when
            randomInstanceGenerator.generate(type);
        }).isInstanceOf(NoAccessibleDefaultConstructorException.class).hasMessage("Class %s does not have default constructor, or it's not accessible".formatted(type.getTypeName())).hasCauseInstanceOf(NoSuchMethodException.class);
    }

    @Test
    public void shouldThrowAbstractClassInstantiationExceptionWhenGivenAnAbstractClass() {
        //given
        Type type = AbstractClass.class;

        //then
        assertThatThrownBy(() -> {//when
            randomInstanceGenerator.generate(type);
        }).isInstanceOf(AbstractClassInstantiationException.class).hasMessage("%s is abstract. Can't instantiate an abstract class".formatted(type.getTypeName())).hasCauseInstanceOf(InstantiationException.class);
    }

    @Test
    public void shouldThrowInvocationSetterExceptionWhenSetterThrowsAnException() {
        //given
        Type type = ClassWithSetterThatThrowsAnException.class;
        String setterName = "setA";

        //then
        assertThatThrownBy(() -> {//when
            randomInstanceGenerator.generate(type);
        }).isInstanceOf(InvocationSetterException.class).hasMessage("got an exception while invoking setter %s in class %s".formatted(setterName, type.getTypeName())).hasCauseInstanceOf(InvocationTargetException.class);
    }

    @Test
    public void shouldThrowNestedObjectExceptionWhenGivenSelfContainingClass() {
        assertThatThrownBy(() -> randomInstanceGenerator.generate(SelfContainingNestedClassTest.class)).isInstanceOf(NestedObjectRecursionException.class);
    }

    @Test
    public void shouldReturnObjectForNestedClasses() {
        //when
        Object generated = randomInstanceGenerator.generate(NestedClass.class);
        //then
        log.info("shouldReturnAnObjectInstance : {}", generated);
        assertThat(generated).isNotNull().isInstanceOf(NestedClass.class);
        assertThat(generated).extracting("testClass").isNotNull().isInstanceOf(TestClass.class);

    }

    @Test
    public void shouldThrowInstanceGenerationExceptionWhenConstructorThrowsAnException() {
        //given
        Class<?> type = ClassWithDefaultConstructorThrowingException.class;

        //then
        assertThatThrownBy(() -> {//when
            randomInstanceGenerator.generate(type);
        }).isInstanceOf(InstanceGenerationException.class)
                .hasCauseInstanceOf(InvocationTargetException.class);

    }

    @Test
    public void shouldGenerateObjectIgnoringFieldsWithNonPublicSetters() {
        //given
        Object generatedObject = randomInstanceGenerator.generate(ClassWithSomeNonPublicSetters.class);
        // then
        assertThat(generatedObject)
                .isNotNull()
                .isInstanceOf(ClassWithSomeNonPublicSetters.class);
        ClassWithSomeNonPublicSetters generatedClassWithSomeNonPublicSetters = (ClassWithSomeNonPublicSetters) generatedObject;
        assertThat(generatedClassWithSomeNonPublicSetters)
                .extracting(ClassWithSomeNonPublicSetters::getIntegerWithPrivateSetter)
                .isNull();
        assertThat(generatedClassWithSomeNonPublicSetters)
                .extracting(ClassWithSomeNonPublicSetters::getIntegerWithProtectedSetter)
                .isNull();
        assertThat(generatedClassWithSomeNonPublicSetters)
                .extracting(ClassWithSomeNonPublicSetters::getFloatWithDefaultAccessModifierSetter)
                .isNull();
        assertThat(generatedClassWithSomeNonPublicSetters)
                .extracting(ClassWithSomeNonPublicSetters::getDoubleWithPublicSetter)
                .isNotNull();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void shouldThrowIllegalArgumentExceptionWhenGivenANullType() {
        Type type = null;
        assertThatThrownBy(() -> {//when
            randomInstanceGenerator.generate(type);
        }).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldGenerateObjectWithFieldsContainingSetterPrefix() {
        //given
        Object generatedObject = randomInstanceGenerator.generate(ClassWithFieldContainingSetterPrefix.class);
        // then
        assertThat(generatedObject)
                .isNotNull()
                .isInstanceOf(ClassWithFieldContainingSetterPrefix.class);
        ClassWithFieldContainingSetterPrefix generatedObjectWithFieldContainingSetterPrefix = (ClassWithFieldContainingSetterPrefix) generatedObject;
        assertThat(generatedObjectWithFieldContainingSetterPrefix)
                .extracting(ClassWithFieldContainingSetterPrefix::getSettersList)
                .asList()
                .isNotNull()
                .hasSizeBetween(CollectionGenerator.DEFAULT_MIN_SIZE_INCLUSIVE, CollectionGenerator.DEFAULT_MAX_SIZE_EXCLUSIVE)// could be better if we had access to collectionGenerator defaultMin defaultMax size as public static fields
                .element(0)
                .isNotNull()
                .asString()
                .isNotEmpty();
    }

    @Test
    public void shouldGenerateBaseFieldsAndInheritedFieldsWhenGivenClassExtendingAnotherClass() {
        //given
        Object generatedObject = randomInstanceGenerator.generate(BaseClass.class);
        // then
        assertThat(generatedObject)
                .isNotNull()
                .isInstanceOf(BaseClass.class);
        BaseClass baseClass = (BaseClass) generatedObject;
        assertThat(baseClass)
                .extracting(BaseClass::getBaseField)
                .isNotNull();
                // FIXME: this fails .isInstanceOf(int.class);
        assertThat(baseClass)
                .extracting(BaseClass::getInheritedField)
                .isNotNull()
                .isInstanceOf(Float.class);
    }

    @ParameterizedTest
    @ValueSource(classes = {String.class, Integer.class, Double.class, BaseClass.class})
    public void shouldGenerateAGenericClass(Type genericType) {
        //given
        Object generatedObject = randomInstanceGenerator.generate(GenericTestClass.class, genericType);
        // then
        assertThat(generatedObject)
                .isNotNull()
                .isInstanceOf(GenericTestClass.class);
        GenericTestClass<String> genericTestClass = (GenericTestClass) generatedObject;
        assertThat(genericTestClass)
                .extracting(GenericTestClass::getList)
                .asList()
                .isNotNull()
                .hasSizeBetween(CollectionGenerator.DEFAULT_MIN_SIZE_INCLUSIVE, CollectionGenerator.DEFAULT_MAX_SIZE_EXCLUSIVE)// could be better if we had access to collectionGenerator defaultMin defaultMax size as public static fields
                .element(0)
                .isInstanceOf((Class<?>) genericType);
    }

    @Test
    public void shouldGenerateAGenericClassWithRightOrderTypes() {
        //given
        Class<String> classParam1 = String.class;
        Class<Integer> classParam2 = Integer.class;
        Object generatedObject = randomInstanceGenerator.generate(GenericTestClass2.class, classParam1, classParam2);
        // then
        assertThat(generatedObject)
                .isNotNull()
                .isInstanceOf(GenericTestClass2.class);

        GenericTestClass2<String, Integer> genericTestClass2 = (GenericTestClass2) generatedObject;
        assertThat(genericTestClass2)
                .extracting(GenericTestClass2::getGenericClass3)
                .isNotNull()
                .isInstanceOf(GenericTestClass3.class)
                .extracting(GenericTestClass3::getT)
                .isNotNull()
                .isInstanceOf(classParam2);

        assertThat(genericTestClass2)
                .extracting(GenericTestClass2::getGenericClass3)
                .isNotNull()
                .isInstanceOf(GenericTestClass3.class)
                .extracting(GenericTestClass3::getK)
                .isNotNull()
                .isInstanceOf(classParam1);
    }
}
