package kmg.tool.directorytool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/**
 * DirectoryToolApplicationのテストクラス。<br>
 * <p>
 * アプリケーションの主要な機能とエラーハンドリングをテストします。
 * </p>
 */
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
public class DirectoryToolApplicationTest {

    /**
     * Springコンテキストが正常にロードされることを確認するテスト。
     */
    @SuppressWarnings("static-method")
    @Test
    public void testContextLoads() {

        /* 検証の実施 */
        Assertions.assertTrue(true, "Spring Contextが正常にロードされること");

    }

    /**
     * テスト用のディレクトリ構造を作成するヘルパーメソッド。
     *
     * @param tempDir
     *                テスト用の一時ディレクトリ
     * @return 作成されたソースディレクトリのPath
     * @throws IOException
     *                     ディレクトリやファイルの作成に失敗した場合
     */
    private static Path createTestDirectory(@TempDir final Path tempDir) throws IOException {

        final Path sourceDir = tempDir.resolve("test-source");
        Files.createDirectories(sourceDir);

        // テスト用のファイルとディレクトリを作成
        Files.createFile(sourceDir.resolve("test1.txt"));
        Files.createFile(sourceDir.resolve("test2.txt"));

        final Path subDir = sourceDir.resolve("subdir");
        Files.createDirectories(subDir);
        Files.createFile(subDir.resolve("test3.txt"));

        final Path result = sourceDir;
        return result;

    }

    /**
     * テストモードでのmainメソッドの正常実行をテスト。
     *
     * @param tempDir
     *                テスト用の一時ディレクトリ
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @SuppressWarnings("static-method")
    @Test
    public void testMainMethodExecutesSuccessfullyInTestMode(@TempDir final Path tempDir) throws IOException {

        /* 期待値の定義 */
        final Path sourceDir = DirectoryToolApplicationTest.createTestDirectory(tempDir);
        final Path destDir   = tempDir.resolve("dest");

        /* 準備 */
        DirectoryToolApplication.main(new String[] {
                "COPY", sourceDir.toString(), destDir.toString()
        });

        /* テスト対象の実行 */
        DirectoryToolApplication.main(new String[] {
                "COPY", sourceDir.toString(), destDir.toString()
        });

        /* 検証の準備 */
        final boolean actualDestDirExists = Files.exists(destDir);
        final boolean actualTest1Exists   = Files.exists(destDir.resolve("test1.txt"));
        final boolean actualTest2Exists   = Files.exists(destDir.resolve("test2.txt"));
        final boolean actualTest3Exists   = Files.exists(destDir.resolve("subdir/test3.txt"));

        /* 検証の実施 */
        Assertions.assertTrue(actualDestDirExists, "destDirが存在すること");
        Assertions.assertTrue(actualTest1Exists, "test1.txtが存在すること");
        Assertions.assertTrue(actualTest2Exists, "test2.txtが存在すること");
        Assertions.assertTrue(actualTest3Exists, "subdir/test3.txtが存在すること");

    }

    /**
     * 非テストモードでのmainメソッドの正常実行をテスト。
     *
     * @param tempDir
     *                テスト用の一時ディレクトリ
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @SuppressWarnings("static-method")
    @Test
    public void testMainMethodExecutesSuccessfullyInNonTestMode(@TempDir final Path tempDir) throws IOException {

        /* 期待値の定義 */
        final Path sourceDir = DirectoryToolApplicationTest.createTestDirectory(tempDir);
        final Path destDir   = tempDir.resolve("dest");

        /* 準備 */
        System.setProperty("skipExit", "true");
        DirectoryToolApplication.main(new String[] {
                "COPY", sourceDir.toString(), destDir.toString()
        });

        /* テスト対象の実行 */
        DirectoryToolApplication.main(new String[] {
                "COPY", sourceDir.toString(), destDir.toString()
        });

        /* 検証の準備 */
        final boolean actualDestDirExists = Files.exists(destDir);
        final boolean actualTest1Exists   = Files.exists(destDir.resolve("test1.txt"));
        final boolean actualTest2Exists   = Files.exists(destDir.resolve("test2.txt"));
        final boolean actualTest3Exists   = Files.exists(destDir.resolve("subdir/test3.txt"));

        /* 検証の実施 */
        Assertions.assertTrue(actualDestDirExists, "destDirが存在すること");
        Assertions.assertTrue(actualTest1Exists, "test1.txtが存在すること");
        Assertions.assertTrue(actualTest2Exists, "test2.txtが存在すること");
        Assertions.assertTrue(actualTest3Exists, "subdir/test3.txt");

    }

    /**
     * 引数不足時のエラーハンドリングをテスト。
     *
     * @param output
     *               テスト出力のキャプチャ
     */
    @SuppressWarnings("static-method")
    @Test
    public void testMainMethodFailsWithInsufficientArguments(final CapturedOutput output) {

        /* 期待値の定義 */
        final String expected = "引数が不足しています。" + System.lineSeparator();

        /* 準備 */
        DirectoryToolApplication.main(new String[] {});

        /* 検証の準備 */
        final String actual = output.getErr();

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "引数不足のエラーメッセージが期待通りであること");

    }

    /**
     * 無効な操作タイプが指定された場合のエラーハンドリングをテスト。
     *
     * @param output
     *               テスト出力のキャプチャ
     */
    @SuppressWarnings("static-method")
    @Test
    public void testMainMethodFailsWithInvalidOperationType(final CapturedOutput output) {

        /* 期待値の定義 */
        final String expected = "無効なモードです。" + System.lineSeparator();

        /* 準備 */
        DirectoryToolApplication.main(new String[] {
                "INVALID", "/src", "/dest"
        });

        /* 検証の準備 */
        final String actual = output.getErr();

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "無効な操作タイプのエラーメッセージが期待通りであること");

    }

    /**
     * 存在しないソースディレクトリが指定された場合のエラーハンドリングをテスト。
     *
     * @param output
     *               テスト出力のキャプチャ
     */
    @SuppressWarnings("static-method")
    @Test
    public void testMainMethodFailsWithNonExistentSourceDirectory(final CapturedOutput output) {

        /* 期待値の定義 */
        final String expected = "ソースディレクトリが存在しません。" + System.lineSeparator();

        /* 準備 */
        DirectoryToolApplication.main(new String[] {
                "COPY", "/nonexistent", "/dest"
        });

        /* 検証の準備 */
        final String actual = output.getErr();

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "存在しないソースディレクトリのエラーメッセージが期待通りであること");

    }

    /**
     * 無効なパスが指定された場合のエラーハンドリングをテスト。
     *
     * @param tempDir
     *                テスト用の一時ディレクトリ
     * @param output
     *                テスト出力のキャプチャ
     */
    @SuppressWarnings("static-method")
    @Test
    public void testMainMethodFailsWithInvalidPaths(@TempDir final Path tempDir, final CapturedOutput output) {

        /* 期待値の定義 */
        final String expected = "ソースディレクトリが存在しません。" + System.lineSeparator();

        /* 準備 */
        final Path nonExistentSource = tempDir.resolve("non-existent");
        final Path destDir           = tempDir.resolve("dest");

        DirectoryToolApplication.main(new String[] {
                "COPY", nonExistentSource.toString(), destDir.toString()
        });

        /* 検証の準備 */
        final String actual = output.getErr();

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "ソースディレクトリが存在しないメッセージが期待通りであること");

    }
}
