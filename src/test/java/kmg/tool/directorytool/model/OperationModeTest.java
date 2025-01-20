package kmg.tool.directorytool.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * OperationModeの列挙型のテストクラス
 */
public class OperationModeTest {

    /**
     * 列挙型の値が正しく定義されているかテスト
     */
    @Test
    public static void testEnumValues() {

        /* 期待値の定義 */
        final int expectedLength = 3;

        /* 準備 */
        // 期待値の定義は不要

        /* テスト対象の実行 */
        final int actualLength = OperationMode.values().length;

        /* 検証の準備 */

        /* 検証の実施 */
        Assertions.assertEquals(expectedLength, actualLength, "列挙型は3つの値を持つこと");
        Assertions.assertEquals(true, OperationModeTest.containsEnumConstant("COPY"), "COPYが定義されていること");
        Assertions.assertEquals(true, OperationModeTest.containsEnumConstant("MOVE"), "MOVEが定義されていること");
        Assertions.assertEquals(true, OperationModeTest.containsEnumConstant("DIFF"), "DIFFが定義されていること");

    }

    /**
     * valueOf操作が正しく機能するかテスト
     */
    @Test
    public static void testValueOf() {

        /* 期待値の定義 */
        final OperationMode expectedCopy = OperationMode.COPY;
        final OperationMode expectedMove = OperationMode.MOVE;
        final OperationMode expectedDiff = OperationMode.DIFF;

        /* 準備 */
        // 期待値の定義は不要

        /* テスト対象の実行 */
        final OperationMode actualCopy = OperationMode.valueOf("COPY");
        final OperationMode actualMove = OperationMode.valueOf("MOVE");
        final OperationMode actualDiff = OperationMode.valueOf("DIFF");

        /* 検証の準備 */

        /* 検証の実施 */
        Assertions.assertEquals(expectedCopy, actualCopy, "COPYの文字列変換が正しいこと");
        Assertions.assertEquals(expectedMove, actualMove, "MOVEの文字列変換が正しいこと");
        Assertions.assertEquals(expectedDiff, actualDiff, "DIFFの文字列変換が正しいこと");

    }

    /**
     * 無効な値に対する例外処理のテスト
     */
    @Test
    public static void testInvalidValue() {

        /* 期待値の定義 */
        final String invalidValue = "INVALID";

        /* 準備 */
        // 期待値の定義は不要

        /* テスト対象の実行 */
        final IllegalArgumentException actualException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> OperationMode.valueOf(invalidValue), "無効な値でIllegalArgumentExceptionがスローされること");

        /* 検証の準備 */

        /* 検証の実施 */
        Assertions.assertEquals(true, actualException.getMessage().contains(invalidValue), "例外メッセージに無効な値が含まれていること");

    }

    /**
     * 列挙型に指定された名前の定数が含まれているかを確認
     *
     * @param name
     *             確認する定数名
     * @return 定数が存在する場合はtrue
     */
    private static boolean containsEnumConstant(final String name) {

        boolean result = false;

        for (final OperationMode mode : OperationMode.values()) {

            if (mode.name().equals(name)) {

                result = true;
                break;

            }

        }
        return result;

    }
}
