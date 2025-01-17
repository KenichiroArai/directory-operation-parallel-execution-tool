package kmg.tool.directorytool;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.context.SpringBootTest;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

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
    private Path createTestDirectory(@TempDir Path tempDir) throws IOException {
        Path sourceDir = tempDir.resolve("test-source");
        Files.createDirectories(sourceDir);

        // テスト用のファイルとディレクトリを作成
        Files.createFile(sourceDir.resolve("test1.txt"));
        Files.createFile(sourceDir.resolve("test2.txt"));

        Path subDir = sourceDir.resolve("subdir");
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
    void mainMethodExecutesSuccessfullyInTestMode(@TempDir Path tempDir) throws IOException {
        // テストデータを作成
        Path sourceDir = createTestDirectory(tempDir);
        Path destDir   = tempDir.resolve("dest");

        // テストモードを設定
        DirectoryToolApplication.setTestMode(true);

        // mainメソッドを実行（テストデータディレクトリを使用）
        DirectoryToolApplication.main(new String[] {
                sourceDir.toString(), destDir.toString(), "COPY"
        });

        // 結果の検証
        assertTrue(Files.exists(destDir));
        assertTrue(Files.exists(destDir.resolve("test1.txt")));
        assertTrue(Files.exists(destDir.resolve("test2.txt")));
        assertTrue(Files.exists(destDir.resolve("subdir/test3.txt")));
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
    void mainMethodExecutesSuccessfullyInNonTestMode(@TempDir Path tempDir) throws IOException {
        // テストデータを作成
        Path sourceDir = createTestDirectory(tempDir);
        Path destDir   = tempDir.resolve("dest");

        // テストモードをオフに設定
        DirectoryToolApplication.setTestMode(false);
        DirectoryToolApplication.resetExitStatus();

        // System.exitを実行しないようにする
        System.setProperty("skipExit", "true");

        DirectoryToolApplication.main(new String[] {
                sourceDir.toString(), destDir.toString(), "COPY"
        });

        // アプリケーションが終了しようとしたことを確認
        assertTrue(DirectoryToolApplication.hasExited());

        // 結果の検証
        assertTrue(Files.exists(destDir));
        assertTrue(Files.exists(destDir.resolve("test1.txt")));
        assertTrue(Files.exists(destDir.resolve("test2.txt")));
        assertTrue(Files.exists(destDir.resolve("subdir/test3.txt")));

        // プロパティをリセット
        System.clearProperty("skipExit");
    }

    /**
     * テストモード設定の切り替え機能をテスト。
     */
    @Test
    void setTestModeTogglesBehavior() {
        // テストモードの設定を確認
        DirectoryToolApplication.setTestMode(true);
        assertTrue(DirectoryToolApplication.isTestMode());

        DirectoryToolApplication.setTestMode(false);
        assertFalse(DirectoryToolApplication.isTestMode());
    }

    /**
     * 引数不足時のエラーハンドリングをテスト。
     *
     * @param output
     *               テスト出力のキャプチャ
     */
    @Test
    void mainMethodFailsWithInsufficientArguments(CapturedOutput output) {
        DirectoryToolApplication.setTestMode(true);
        DirectoryToolApplication.resetExitStatus();

        DirectoryToolApplication.main(new String[] {});
        assertTrue(DirectoryToolApplication.hasExited(), "Should exit with insufficient arguments");
        assertEquals("引数が不足しています。" + System.lineSeparator(), output.getErr());
    }

    /**
     * 無効な操作タイプが指定された場合のエラーハンドリングをテスト。
     *
     * @param output
     *               テスト出力のキャプチャ
     */
    @Test
    void mainMethodFailsWithInvalidOperationType(CapturedOutput output) {
        DirectoryToolApplication.setTestMode(true);
        DirectoryToolApplication.resetExitStatus();

        DirectoryToolApplication.main(new String[] {
                "/src", "/dest", "INVALID"
        });
        assertTrue(DirectoryToolApplication.hasExited(), "Should exit with invalid operation type");
        assertEquals("無効なモードです。" + System.lineSeparator(), output.getErr());
    }

    /**
     * 存在しないソースディレクトリが指定された場合のエラーハンドリングをテスト。
     *
     * @param output
     *               テスト出力のキャプチャ
     */
    @Test
    void mainMethodFailsWithNonExistentSourceDirectory(CapturedOutput output) {
        DirectoryToolApplication.setTestMode(true);
        DirectoryToolApplication.resetExitStatus();

        DirectoryToolApplication.main(new String[] {
                "/nonexistent", "/dest", "COPY"
        });
        assertTrue(DirectoryToolApplication.hasExited(), "Should exit with non-existent source directory");
        assertEquals("ソースディレクトリが存在しません。" + System.lineSeparator(), output.getErr());
    }

    /**
     * エラー時の終了処理をテスト。
     *
     * @param output
     *               テスト出力のキャプチャ
     */
    @Test
    void testExitWithError(CapturedOutput output) {
        // テストモードがtrueの場合
        DirectoryToolApplication.setTestMode(true);
        assertTrue(DirectoryToolApplication.exitWithError());
        assertTrue(DirectoryToolApplication.hasExited());
        assertEquals("", output.toString());
        System.out.println(output.toString());
        assertTrue(output.toString().isEmpty());

        // テストモードがfalseでskipExitがtrueの場合
        DirectoryToolApplication.setTestMode(false);
        System.setProperty("skipExit", "true");
        assertTrue(DirectoryToolApplication.exitWithError());
        assertTrue(DirectoryToolApplication.hasExited());
        assertTrue(output.toString().isEmpty());

        // テストモードがfalseでskipExitがfalseの場合
        System.clearProperty("skipExit");
        assertTrue(DirectoryToolApplication.exitWithError());
        assertTrue(DirectoryToolApplication.hasExited());
        assertTrue(output.toString().isEmpty());

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
    void mainMethodFailsWithInvalidPaths(@TempDir Path tempDir, CapturedOutput output) {
        DirectoryToolApplication.setTestMode(true);
        DirectoryToolApplication.resetExitStatus();

        // 存在しないソースディレクトリ
        Path nonExistentSource = tempDir.resolve("non-existent");
        Path destDir           = tempDir.resolve("dest");

        DirectoryToolApplication.main(new String[] {
                nonExistentSource.toString(), destDir.toString(), "COPY"
        });
        assertTrue(DirectoryToolApplication.hasExited(), "Should exit with non-existent source directory");
        assertTrue(output.toString().contains("ソースディレクトリが存在しません。"),
                "Should display source directory not found message");
    }
}
