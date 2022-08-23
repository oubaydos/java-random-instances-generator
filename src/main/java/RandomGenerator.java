import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Random;

/**
 * this will be responsible for generating random instance of a class
 * it will be using java reflection api to access setter methods of a class
 * setter methods = public methods that starts with "set"
 *
 * @author obaydah bouifadene
 * @see java.lang.reflect
 */
public class RandomGenerator {
    private final Random random = new Random();

    /**
     * if type.getName() in {"int", "java.lang.Integer"}
     *
     * @return a random int between 0 and Integer.MAX_VALUE
     */
    private int getRandomInt() {
        return random.nextInt(0, Integer.MAX_VALUE);
    }
    private short getRandomShort(){
        return (short) random.nextInt(Short.MAX_VALUE + 1);
    }
    /**
     * if type.getName() is "java.lang.String"
     *
     * @return a random string of length 10
     * @see org.apache.commons.lang3.RandomStringUtils
     */
    private String getRandomString() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    /**
     * if type extends Enum<>
     * @param enumClass the enum class : to be passed as EnumName.class for example
     * @return a random Enum of the given type or null if the enum is empty
     */
    public  <T extends Enum<T>> T getRandomEnum(@NotNull Class<T> enumClass) {
        EnumSet<T> enumSet = EnumSet.allOf(enumClass);
        if (enumSet.isEmpty()) {
            return null;
        }
        return enumSet.stream().skip(new Random().nextInt(enumSet.size())).findFirst().orElse(null);
    }

    /**
     * the facade to the other generation methods
     * will be the base method for generating random values for all types of fields
     *
     * @param type the type of the field
     * @return a random value of the according type Object
     */
    public Object getRandomObject(Type type) {
        if (type == Integer.class || type == int.class) {
            return getRandomInt();
        } else if (type == String.class) {
            return getRandomString();
        }
        else if (type==Short.class || type ==short.class){
            return getRandomShort();
        }
        return null;
    }
}
