package kmg.tool.directorytool;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.context.SpringBootTest;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class DirectoryToolApplicationTest {

    @Test
    void contextLoads() {
        // Spring Contextが正常にロードされることを確認
    }

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

    @Test
    void mainMethodExecutesSuccessfullyInTestMode(@TempDir Path tempDir) throws IOException {
        // テストデータを作成
        Path sourceDir = createTestDirectory(tempDir);
        Path destDir = tempDir.resolve("dest");

        // テストモードを設定
        DirectoryToolApplication.setTestMode(true);

        // mainメソッドを実行（テストデータディレクトリを使用）
        DirectoryToolApplication
                .main(new String[] {sourceDir.toString(), destDir.toString(), "COPY"});

        // 結果の検証
        assertTrue(Files.exists(destDir));
        assertTrue(Files.exists(destDir.resolve("test1.txt")));
        assertTrue(Files.exists(destDir.resolve("test2.txt")));
        assertTrue(Files.exists(destDir.resolve("subdir/test3.txt")));
    }

    @Test
    void mainMethodExecutesSuccessfullyInNonTestMode(@TempDir Path tempDir) throws IOException {
        // テストデータを作成
        Path sourceDir = createTestDirectory(tempDir);
        Path destDir = tempDir.resolve("dest");

        // テストモードをオフに設定
        DirectoryToolApplication.setTestMode(false);
        DirectoryToolApplication.resetExitStatus();

        // System.exitを実行しないようにする
        System.setProperty("skipExit", "true");

        DirectoryToolApplication
                .main(new String[] {sourceDir.toString(), destDir.toString(), "COPY"});

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

    @Test
    void setTestModeTogglesBehavior() {
        // テストモードの設定を確認
        DirectoryToolApplication.setTestMode(true);
        assertTrue(DirectoryToolApplication.isTestMode());

        DirectoryToolApplication.setTestMode(false);
        assertFalse(DirectoryToolApplication.isTestMode());
    }
}
