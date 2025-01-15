package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * 移動操作を実行するサービスのテストクラス。
 */
class MoveDirectoryServiceTest extends AbstractDirectoryServiceTest {

    @Override
    protected AbstractDirectoryService createService() {
        return new MoveDirectoryService();
    }

    /**
     * 基本的なファイル移動操作のテスト
     */
    @Test
    void testBasicMoveOperation() throws IOException {
        // テストファイルを作成
        Path testFile = sourceDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // 移動操作を実行
        service.processDirectory(sourceDir.toString(), targetDir.toString());

        // 検証
        Path movedFile = targetDir.resolve("test.txt");
        assertTrue(Files.exists(movedFile), "移動先にファイルが存在すること");
        assertFalse(Files.exists(testFile), "元のファイルが削除されていること");
        assertEquals("test content", Files.readString(movedFile), "ファイルの内容が正しく移動されていること");
    }

    /**
     * 複雑なディレクトリ構造の移動テスト
     */
    @Test
    void testComplexDirectoryStructureMove() throws IOException {
        // 複雑なディレクトリ構造を作成
        Path subDir1 = sourceDir.resolve("subdir1");
        Path subDir2 = sourceDir.resolve("subdir2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        Files.writeString(subDir1.resolve("file1.txt"), "content1");
        Files.writeString(subDir2.resolve("file2.txt"), "content2");
        Files.writeString(sourceDir.resolve("root.txt"), "root content");

        // 移動操作を実行
        service.processDirectory(sourceDir.toString(), targetDir.toString());

        // ファイルが正しく移動されたことを検証
        assertTrue(Files.exists(targetDir.resolve("subdir1/file1.txt")));
        assertTrue(Files.exists(targetDir.resolve("subdir2/file2.txt")));
        assertTrue(Files.exists(targetDir.resolve("root.txt")));

        assertEquals("content1", Files.readString(targetDir.resolve("subdir1/file1.txt")));
        assertEquals("content2", Files.readString(targetDir.resolve("subdir2/file2.txt")));
        assertEquals("root content", Files.readString(targetDir.resolve("root.txt")));

        // 元のファイルとディレクトリが削除されていることを検証
        assertFalse(Files.exists(sourceDir), "ソースディレクトリが削除されていること");
    }

    /**
     * 既存のファイルの上書き移動のテスト
     */
    @Test
    void testOverwriteExistingFile() throws IOException {
        // ソースファイルを作成
        Path sourceFile = sourceDir.resolve("file.txt");
        Files.writeString(sourceFile, "new content");

        // ターゲットに既存のファイルを作成
        Path targetFile = targetDir.resolve("file.txt");
        Files.writeString(targetFile, "old content");

        // 移動操作を実行
        service.processDirectory(sourceDir.toString(), targetDir.toString());

        // 検証
        assertTrue(Files.exists(targetFile), "ターゲットファイルが存在すること");
        assertFalse(Files.exists(sourceFile), "ソースファイルが削除されていること");
        assertEquals("new content", Files.readString(targetFile), "ファイルが正しく上書きされていること");
    }

    /**
     * 空のディレクトリ構造の移動テスト
     */
    @Test
    void testMoveEmptyDirectoryStructure() throws IOException {
        // 空のディレクトリ構造を作成
        Path subDir1 = sourceDir.resolve("empty1");
        Path subDir2 = sourceDir.resolve("empty2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        // 移動操作を実行
        service.processDirectory(sourceDir.toString(), targetDir.toString());

        // ディレクトリ構造が正しく移動されたことを検証
        assertTrue(Files.isDirectory(targetDir.resolve("empty1")));
        assertTrue(Files.isDirectory(targetDir.resolve("empty2")));
        assertFalse(Files.exists(subDir1), "元の空ディレクトリが削除されていること");
        assertFalse(Files.exists(subDir2), "元の空ディレクトリが削除されていること");
    }
}
