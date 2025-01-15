package kmg.tool.directorytool.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * OperationModeの列挙型のテストクラス
 */
class OperationModeTest {

    /**
     * 列挙型の値が正しく定義されているかテスト
     */
    @Test
    void testEnumValues() {
        assertEquals(3, OperationMode.values().length, "列挙型は3つの値を持つこと");
        assertTrue(containsEnumConstant("COPY"), "COPYが定義されていること");
        assertTrue(containsEnumConstant("MOVE"), "MOVEが定義されていること");
        assertTrue(containsEnumConstant("DIFF"), "DIFFが定義されていること");
    }

    /**
     * valueOf操作が正しく機能するかテスト
     */
    @Test
    void testValueOf() {
        assertEquals(OperationMode.COPY, OperationMode.valueOf("COPY"), "COPYの文字列変換が正しいこと");
        assertEquals(OperationMode.MOVE, OperationMode.valueOf("MOVE"), "MOVEの文字列変換が正しいこと");
        assertEquals(OperationMode.DIFF, OperationMode.valueOf("DIFF"), "DIFFの文字列変換が正しいこと");
    }

    /**
     * 無効な値に対する例外処理のテスト
     */
    @Test
    void testInvalidValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> OperationMode.valueOf("INVALID"),
                "無効な値でIllegalArgumentExceptionがスローされること"
        );
        assertTrue(exception.getMessage().contains("INVALID"), "例外メッセージに無効な値が含まれていること");
    }

    /**
     * 列挙型に指定された名前の定数が含まれているかを確認
     *
     * @param name 確認する定数名
     * @return 定数が存在する場合はtrue
     */
    private boolean containsEnumConstant(String name) {
        for (OperationMode mode : OperationMode.values()) {
            if (mode.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
