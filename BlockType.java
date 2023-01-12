import java.util.Arrays;
/**
 * @author (Daniel Furrer, Christian Cidecian)
 * @version (v2.69)
 */
public enum BlockType {
    I(0),
    J(1),
    L(2),
    O(3),
    S(4),
    T(5),
    Z(6),
/*    U(7),
    B(8),
    D(9),
    R(10)*/;

    BlockType(int value) {
        this.value = value;
    }

    private int value;

    public static BlockType fromInt(int number) {
        return Arrays.stream(BlockType.values()).filter(blockType -> blockType.value == number).findFirst().get();
    }
}