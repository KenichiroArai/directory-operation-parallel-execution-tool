package kmg.tool.directorytool.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * OperationModeの列挙型のテストクラス
 */
class OperationModeTest {

    /**
     * 列挙型の値が正しく定義されているかテスト
     */
    @Test
    static void testEnumValues() {
        Assertions.assertEquals(3, OperationMode.values().length, "列挙型は3つの値を持つこと");
        Assertions.assertTrue(containsEnumConstant("COPY"), "COPYが定義されていること");
        Assertions.assertTrue(containsEnumConstant("MOVE"), "MOVEが定義されていること");
        Assertions.assertTrue(containsEnumConstant("DIFF"), "DIFFが定義されていること");
    }

    /**
     * valueOf操作が正しく機能するかテスト
     */
    @Test
    static void testValueOf() {
        Assertions.assertEquals(OperationMode.COPY, OperationMode.valueOf("COPY"), "COPYの文字列変換が正しいこと");
        Assertions.assertEquals(OperationMode.MOVE, OperationMode.valueOf("MOVE"), "MOVEの文字列変換が正しいこと");
        Assertions.assertEquals(OperationMode.DIFF, OperationMode.valueOf("DIFF"), "DIFFの文字列変換が正しいこと");
    }

    /**
     * 無効な値に対する例外処理のテスト
     */
    @Test
    static void testInvalidValue() {
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> OperationMode.valueOf("INVALID"), "無効な値でIllegalArgumentExceptionがスローされること");
        Assertions.assertTrue(exception.getMessage().contains("INVALID"), "例外メッセージに無効な値が含まれていること");
    }

    /**
     * 列挙型に指定された名前の定数が含まれているかを確認
     *
     * @param name
     *             確認する定数名
     * @return 定数が存在する場合はtrue
     */
    private static boolean containsEnumConstant(final String name) {
        for (final OperationMode mode : OperationMode.values()) {
            if (mode.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
