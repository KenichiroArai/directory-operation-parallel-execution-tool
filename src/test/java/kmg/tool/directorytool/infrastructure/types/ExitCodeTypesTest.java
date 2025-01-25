package kmg.tool.directorytool.infrastructure.types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ExitCodeTypesの列挙型のテストクラス
 */
public class ExitCodeTypesTest {

    /**
     * 列挙型の値が正しく定義されているかテスト
     */
    @SuppressWarnings("static-method")
    @Test
    public void testEnumValues() {

        /* 期待値の定義 */
        final int expectedLength = 5;

        /* 準備 */
        // 期待値の定義は不要

        /* テスト対象の実行 */
        final int actualLength = ExitCodeTypes.values().length;

        /* 検証の準備 */

        /* 検証の実施 */
        Assertions.assertEquals(expectedLength, actualLength, "列挙型は5つの値を持つこと");
        Assertions.assertTrue(ExitCodeTypesTest.containsEnumConstant("NONE"), "NONEが定義されていること");
        Assertions.assertTrue(ExitCodeTypesTest.containsEnumConstant("SUCCESS"), "SUCCESSが定義されていること");
        Assertions.assertTrue(ExitCodeTypesTest.containsEnumConstant("ARGUMENT_ERROR"), "ARGUMENT_ERRORが定義されていること");
        Assertions.assertTrue(ExitCodeTypesTest.containsEnumConstant("EXPECTED_ERROR"), "EXPECTED_ERRORが定義されていること");
        Assertions.assertTrue(ExitCodeTypesTest.containsEnumConstant("UNEXPECTED_ERROR"), "UNEXPECTED_ERRORが定義されていること");

    }

    /**
     * getEnumメソッドのテスト
     */
    @SuppressWarnings("static-method")
    @Test
    public void testGetEnum() {

        /* 期待値の定義 */
        final ExitCodeTypes expectedSuccess = ExitCodeTypes.SUCCESS;
        final ExitCodeTypes expectedNone    = ExitCodeTypes.NONE;

        /* テスト対象の実行 */
        final ExitCodeTypes actualSuccess = ExitCodeTypes.getEnum(0);
        final ExitCodeTypes actualInvalid = ExitCodeTypes.getEnum(999);

        /* 検証の実施 */
        Assertions.assertEquals(expectedSuccess, actualSuccess, "有効な値でSUCCESSが返されること");
        Assertions.assertEquals(expectedNone, actualInvalid, "無効な値でNONEが返されること");

    }

    /**
     * getInitValueメソッドのテスト
     */
    @SuppressWarnings("static-method")
    @Test
    public void testGetInitValue() {

        /* 期待値の定義 */
        final ExitCodeTypes expected = ExitCodeTypes.NONE;

        /* テスト対象の実行 */
        final ExitCodeTypes actual = ExitCodeTypes.getInitValue();

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "初期値としてNONEが返されること");

    }

    /**
     * getDefaultメソッドのテスト
     */
    @SuppressWarnings("static-method")
    @Test
    public void testGetDefault() {

        /* 期待値の定義 */
        final ExitCodeTypes expected = ExitCodeTypes.NONE;

        /* テスト対象の実行 */
        final ExitCodeTypes actual = ExitCodeTypes.getDefault();

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "デフォルト値としてNONEが返されること");

    }

    /**
     * getName、getValue、getメソッドのテスト
     */
    @SuppressWarnings("static-method")
    @Test
    public void testGetters() {

        /* 期待値の定義 */
        final String expectedSuccessName  = "正常";
        final int    expectedSuccessValue = 0;

        /* テスト対象の実行 */
        final ExitCodeTypes success = ExitCodeTypes.SUCCESS;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSuccessName, success.getName(), "getName()が正しい名称を返すこと");
        Assertions.assertEquals(expectedSuccessValue, success.getValue(), "getValue()が正しい値を返すこと");
        Assertions.assertEquals(expectedSuccessValue, success.get(), "get()が正しい値を返すこと");
        Assertions.assertEquals(String.valueOf(expectedSuccessValue), success.toString(), "toString()が正しい値を返すこと");

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

        for (final ExitCodeTypes exitCodeTypes : ExitCodeTypes.values()) {

            if (exitCodeTypes.name().equals(name)) {

                result = true;
                break;

            }

        }
        return result;

    }
}
