import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * this is the test class for which we will be trying to generate random objects
 * it is annotated with @Setter
 * @see lombok.Setter
 * @author obaydah bouifadene
 */
@NoArgsConstructor
@Setter
@ToString
public class TestClass {
    private final String a = "";
    private Integer b;
    private String alpha;
    private Short shortNumber;
//    private List<String> list;
}
