package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * コピー操作を実行するサービスのテストクラス。
 */
class CopyDirectoryServiceTest extends AbstractDirectoryServiceTest {

    @Override
    protected AbstractDirectoryService createService() {
        return new CopyDirectoryService();
    }

    /**
     * 基本的なファイルコピー操作のテスト
     */
    @Test
    void testBasicCopyOperation() throws IOException {
        // テストファイルを作成
        Path testFile = sourceDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // コピー操作を実行
        service.processDirectory(sourceDir.toString(), targetDir.toString());

        // 検証
        Path copiedFile = targetDir.resolve("test.txt");
        assertTrue(Files.exists(copiedFile), "コピーされたファイルが存在すること");
        assertTrue(Files.exists(testFile), "元のファイルが残っていること");
        assertEquals("test content", Files.readString(copiedFile), "ファイルの内容が正しくコピーされていること");
    }

    /**
     * 複雑なディレクトリ構造のコピーテスト
     */
    @Test
    void testComplexDirectoryStructureCopy() throws IOException {
        // 複雑なディレクトリ構造を作成
        Path subDir1 = sourceDir.resolve("subdir1");
        Path subDir2 = sourceDir.resolve("subdir2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        Files.writeString(subDir1.resolve("file1.txt"), "content1");
        Files.writeString(subDir2.resolve("file2.txt"), "content2");
        Files.writeString(sourceDir.resolve("root.txt"), "root content");

        // コピー操作を実行
        service.processDirectory(sourceDir.toString(), targetDir.toString());

        // すべてのファイルとディレクトリが正しくコピーされたことを検証
        assertTrue(Files.exists(targetDir.resolve("subdir1/file1.txt")));
        assertTrue(Files.exists(targetDir.resolve("subdir2/file2.txt")));
        assertTrue(Files.exists(targetDir.resolve("root.txt")));

        assertEquals("content1", Files.readString(targetDir.resolve("subdir1/file1.txt")));
        assertEquals("content2", Files.readString(targetDir.resolve("subdir2/file2.txt")));
        assertEquals("root content", Files.readString(targetDir.resolve("root.txt")));
    }

    /**
     * 既存のファイルの上書きコピーのテスト
     */
    @Test
    void testOverwriteExistingFile() throws IOException {
        // ソースファイルを作成
        Path sourceFile = sourceDir.resolve("file.txt");
        Files.writeString(sourceFile, "new content");

        // ターゲットに既存のファイルを作成
        Path targetFile = targetDir.resolve("file.txt");
        Files.writeString(targetFile, "old content");

        // コピー操作を実行
        service.processDirectory(sourceDir.toString(), targetDir.toString());

        // 検証
        assertTrue(Files.exists(targetFile), "ターゲットファイルが存在すること");
        assertEquals("new content", Files.readString(targetFile), "ファイルが正しく上書きされていること");
    }

    /**
     * 空のディレクトリ構造のコピーテスト
     */
    @Test
    void testCopyEmptyDirectoryStructure() throws IOException {
        // 空のディレクトリ構造を作成
        Path subDir1 = sourceDir.resolve("empty1");
        Path subDir2 = sourceDir.resolve("empty2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        // コピー操作を実行
        service.processDirectory(sourceDir.toString(), targetDir.toString());

        // ディレクトリ構造が正しくコピーされたことを検証
        assertTrue(Files.isDirectory(targetDir.resolve("empty1")));
        assertTrue(Files.isDirectory(targetDir.resolve("empty2")));
    }
}
