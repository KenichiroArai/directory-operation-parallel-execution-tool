package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

/**
 * ディレクトリ操作サービスのテストのための基底クラス。 共通のセットアップ、検証、クリーンアップ機能を提供する。
 */
public abstract class AbstractDirectoryServiceTest {

    @TempDir
    protected Path tempDir;

    protected Path sourceDir;
    protected Path targetDir;
    protected AbstractDirectoryService service;

    /**
     * テストの前準備
     */
    @BeforeEach
    public void setUp() throws IOException {
        // テスト用のディレクトリ構造を作成
        sourceDir = tempDir.resolve("source");
        targetDir = tempDir.resolve("target");
        Files.createDirectories(sourceDir);
        Files.createDirectories(targetDir);

        // テスト対象のサービスを初期化
        service = createService();
    }

    /**
     * テスト対象のサービスを作成する
     */
    protected abstract AbstractDirectoryService createService();

    /**
     * 存在しないソースディレクトリに対するテスト
     */
    @org.junit.jupiter.api.Test
    void testNonExistentSourceDirectory() {
        Path nonExistentDir = tempDir.resolve("non-existent");

        IOException exception = assertThrows(IOException.class,
                () -> service.processDirectory(nonExistentDir.toString(), targetDir.toString()));

        assertEquals("Source directory does not exist", exception.getMessage());
    }

    /**
     * ソースパスがディレクトリでない場合のテスト
     */
    @org.junit.jupiter.api.Test
    void testSourcePathNotDirectory() throws IOException {
        Path sourceFile = tempDir.resolve("source.txt");
        Files.writeString(sourceFile, "test");

        IOException exception = assertThrows(IOException.class,
                () -> service.processDirectory(sourceFile.toString(), targetDir.toString()));

        assertEquals("Source path is not a directory", exception.getMessage());
    }

    /**
     * ターゲットパスが既存のファイルの場合のテスト
     */
    @org.junit.jupiter.api.Test
    void testTargetPathExistsAsFile() throws IOException {
        Files.createDirectories(sourceDir);
        Path targetFile = tempDir.resolve("target_file.txt");
        Files.writeString(targetFile, "existing file");

        IOException exception = assertThrows(IOException.class,
                () -> service.processDirectory(sourceDir.toString(), targetFile.toString()));

        assertEquals("Destination path exists but is not a directory", exception.getMessage());
    }

    /**
     * 空のディレクトリに対する操作のテスト
     */
    @org.junit.jupiter.api.Test
    void testEmptyDirectoryOperation() throws IOException {
        // 操作を実行
        service.processDirectory(sourceDir.toString(), targetDir.toString());

        // 検証
        assertTrue(Files.exists(targetDir), "ターゲットディレクトリが作成されていること");
        assertTrue(Files.isDirectory(targetDir), "ターゲットパスがディレクトリであること");
    }

    /**
     * テスト後のクリーンアップ処理
     */
    @AfterEach
    public void tearDown() throws IOException {
        // テスト後のクリーンアップ
        if (Files.exists(sourceDir)) {
            Files.walk(sourceDir).sorted((a, b) -> b.toString().length() - a.toString().length())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // クリーンアップ中のエラーは無視
                        }
                    });
        }

        if (Files.exists(targetDir)) {
            Files.walk(targetDir).sorted((a, b) -> b.toString().length() - a.toString().length())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // クリーンアップ中のエラーは無視
                        }
                    });
        }
    }
}
