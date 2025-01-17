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
 * DirectoryToolApplicationのテストクラス。 アプリケーションの主要な機能とエラーハンドリングをテストします。
 */
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class DirectoryToolApplicationTest {

    /**
     * Springコンテキストが正常にロードされることを確認するテスト。
     */
    @Test
    void contextLoads() {
        // Spring Contextが正常にロードされることを確認
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

        return sourceDir;
    }

    /**
     * テストモードでのmainメソッドの正常実行をテスト。
     * 
     * @param tempDir
     *                テスト用の一時ディレクトリ
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @Test
    static void mainMethodExecutesSuccessfullyInTestMode(@TempDir final Path tempDir) throws IOException {
        // テストデータを作成
        final Path sourceDir = createTestDirectory(tempDir);
        final Path destDir   = tempDir.resolve("dest");

        // テストモードを設定
        DirectoryToolApplication.setTestMode(true);

        // mainメソッドを実行（テストデータディレクトリを使用）
        DirectoryToolApplication.main(new String[] {
                sourceDir.toString(), destDir.toString(), "COPY"
        });

        // 結果の検証
        Assertions.assertTrue(Files.exists(destDir));
        Assertions.assertTrue(Files.exists(destDir.resolve("test1.txt")));
        Assertions.assertTrue(Files.exists(destDir.resolve("test2.txt")));
        Assertions.assertTrue(Files.exists(destDir.resolve("subdir/test3.txt")));
    }

    /**
     * 非テストモードでのmainメソッドの正常実行をテスト。
     * 
     * @param tempDir
     *                テスト用の一時ディレクトリ
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @Test
    static void mainMethodExecutesSuccessfullyInNonTestMode(@TempDir final Path tempDir) throws IOException {
        // テストデータを作成
        final Path sourceDir = createTestDirectory(tempDir);
        final Path destDir   = tempDir.resolve("dest");

        // テストモードをオフに設定
        DirectoryToolApplication.setTestMode(false);
        DirectoryToolApplication.resetExitStatus();

        // System.exitを実行しないようにする
        System.setProperty("skipExit", "true");

        DirectoryToolApplication.main(new String[] {
                sourceDir.toString(), destDir.toString(), "COPY"
        });

        // アプリケーションが終了しようとしたことを確認
        Assertions.assertTrue(DirectoryToolApplication.hasExited());

        // 結果の検証
        Assertions.assertTrue(Files.exists(destDir));
        Assertions.assertTrue(Files.exists(destDir.resolve("test1.txt")));
        Assertions.assertTrue(Files.exists(destDir.resolve("test2.txt")));
        Assertions.assertTrue(Files.exists(destDir.resolve("subdir/test3.txt")));

        // プロパティをリセット
        System.clearProperty("skipExit");
    }

    /**
     * テストモード設定の切り替え機能をテスト。
     */
    @Test
    static void setTestModeTogglesBehavior() {
        // テストモードの設定を確認
        DirectoryToolApplication.setTestMode(true);
        Assertions.assertTrue(DirectoryToolApplication.isTestMode());

        DirectoryToolApplication.setTestMode(false);
        Assertions.assertFalse(DirectoryToolApplication.isTestMode());
    }

    /**
     * 引数不足時のエラーハンドリングをテスト。
     * 
     * @param output
     *               テスト出力のキャプチャ
     */
    @Test
    static void mainMethodFailsWithInsufficientArguments(final CapturedOutput output) {
        DirectoryToolApplication.setTestMode(true);
        DirectoryToolApplication.resetExitStatus();

        DirectoryToolApplication.main(new String[] {});
        Assertions.assertTrue(DirectoryToolApplication.hasExited(), "Should exit with insufficient arguments");
        Assertions.assertEquals("引数が不足しています。" + System.lineSeparator(), output.getErr());
    }

    /**
     * 無効な操作タイプが指定された場合のエラーハンドリングをテスト。
     * 
     * @param output
     *               テスト出力のキャプチャ
     */
    @Test
    static void mainMethodFailsWithInvalidOperationType(final CapturedOutput output) {
        DirectoryToolApplication.setTestMode(true);
        DirectoryToolApplication.resetExitStatus();

        DirectoryToolApplication.main(new String[] {
                "/src", "/dest", "INVALID"
        });
        Assertions.assertTrue(DirectoryToolApplication.hasExited(), "Should exit with invalid operation type");
        Assertions.assertEquals("無効なモードです。" + System.lineSeparator(), output.getErr());
    }

    /**
     * 存在しないソースディレクトリが指定された場合のエラーハンドリングをテスト。
     * 
     * @param output
     *               テスト出力のキャプチャ
     */
    @Test
    static void mainMethodFailsWithNonExistentSourceDirectory(final CapturedOutput output) {
        DirectoryToolApplication.setTestMode(true);
        DirectoryToolApplication.resetExitStatus();

        DirectoryToolApplication.main(new String[] {
                "/nonexistent", "/dest", "COPY"
        });
        Assertions.assertTrue(DirectoryToolApplication.hasExited(), "Should exit with non-existent source directory");
        Assertions.assertEquals("ソースディレクトリが存在しません。" + System.lineSeparator(), output.getErr());
    }

    /**
     * エラー時の終了処理をテスト。
     * 
     * @param output
     *               テスト出力のキャプチャ
     */
    @Test
    static void testExitWithError(final CapturedOutput output) {
        // テストモードがtrueの場合
        DirectoryToolApplication.setTestMode(true);
        Assertions.assertTrue(DirectoryToolApplication.exitWithError());
        Assertions.assertTrue(DirectoryToolApplication.hasExited());
        Assertions.assertEquals("", output.toString());
        System.out.println(output.toString());
        Assertions.assertTrue(output.toString().isEmpty());

        // テストモードがfalseでskipExitがtrueの場合
        DirectoryToolApplication.setTestMode(false);
        System.setProperty("skipExit", "true");
        Assertions.assertTrue(DirectoryToolApplication.exitWithError());
        Assertions.assertTrue(DirectoryToolApplication.hasExited());
        Assertions.assertTrue(output.toString().isEmpty());

        // テストモードがfalseでskipExitがfalseの場合
        System.clearProperty("skipExit");
        Assertions.assertTrue(DirectoryToolApplication.exitWithError());
        Assertions.assertTrue(DirectoryToolApplication.hasExited());
        Assertions.assertTrue(output.toString().isEmpty());

        // 状態をリセット
        DirectoryToolApplication.resetExitStatus();
    }

    /**
     * 無効なパスが指定された場合のエラーハンドリングをテスト。
     * 
     * @param tempDir
     *                テスト用の一時ディレクトリ
     * @param output
     *                テスト出力のキャプチャ
     */
    @Test
    static void mainMethodFailsWithInvalidPaths(@TempDir final Path tempDir, final CapturedOutput output) {
        DirectoryToolApplication.setTestMode(true);
        DirectoryToolApplication.resetExitStatus();

        // 存在しないソースディレクトリ
        final Path nonExistentSource = tempDir.resolve("non-existent");
        final Path destDir           = tempDir.resolve("dest");

        DirectoryToolApplication.main(new String[] {
                nonExistentSource.toString(), destDir.toString(), "COPY"
        });
        Assertions.assertTrue(DirectoryToolApplication.hasExited(), "Should exit with non-existent source directory");
        Assertions.assertTrue(output.toString().contains("ソースディレクトリが存在しません。"),
                "Should display source directory not found message");
    }
}
