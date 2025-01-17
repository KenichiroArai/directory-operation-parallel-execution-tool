package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * コピー操作を実行するサービスのテストクラス。
 */
class CopyDirectoryServiceTest extends AbstractDirectoryServiceTest {

    /**
     * コピーサービスのインスタンスを生成します。
     *
     * @return コピーサービスのインスタンス
     */
    @Override
    protected AbstractDirectoryService createService() {
        return new CopyDirectoryService();
    }

    /**
     * 基本的なファイルコピー操作のテスト
     *
     * @throws IOException
     *                     ファイル操作時に発生する可能性のあるIO例外
     */
    @Test
    void testBasicCopyOperation() throws IOException {
        // テストファイルを作成
        final Path testFile = this.sourceDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // コピー操作を実行
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        // 検証
        final Path copiedFile = this.targetDir.resolve("test.txt");
        Assertions.assertTrue(Files.exists(copiedFile), "コピーされたファイルが存在すること");
        Assertions.assertTrue(Files.exists(testFile), "元のファイルが残っていること");
        Assertions.assertEquals("test content", Files.readString(copiedFile), "ファイルの内容が正しくコピーされていること");
    }

    /**
     * 複雑なディレクトリ構造のコピーテスト
     *
     * @throws IOException
     *                     ファイル操作時に発生する可能性のあるIO例外
     */
    @Test
    void testComplexDirectoryStructureCopy() throws IOException {
        // 複雑なディレクトリ構造を作成
        final Path subDir1 = this.sourceDir.resolve("subdir1");
        final Path subDir2 = this.sourceDir.resolve("subdir2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        Files.writeString(subDir1.resolve("file1.txt"), "content1");
        Files.writeString(subDir2.resolve("file2.txt"), "content2");
        Files.writeString(this.sourceDir.resolve("root.txt"), "root content");

        // コピー操作を実行
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        // すべてのファイルとディレクトリが正しくコピーされたことを検証
        Assertions.assertTrue(Files.exists(this.targetDir.resolve("subdir1/file1.txt")));
        Assertions.assertTrue(Files.exists(this.targetDir.resolve("subdir2/file2.txt")));
        Assertions.assertTrue(Files.exists(this.targetDir.resolve("root.txt")));

        Assertions.assertEquals("content1", Files.readString(this.targetDir.resolve("subdir1/file1.txt")));
        Assertions.assertEquals("content2", Files.readString(this.targetDir.resolve("subdir2/file2.txt")));
        Assertions.assertEquals("root content", Files.readString(this.targetDir.resolve("root.txt")));
    }

    /**
     * 既存のファイルの上書きコピーのテスト
     *
     * @throws IOException
     *                     ファイル操作時に発生する可能性のあるIO例外
     */
    @Test
    void testOverwriteExistingFile() throws IOException {
        // ソースファイルを作成
        final Path sourceFile = this.sourceDir.resolve("file.txt");
        Files.writeString(sourceFile, "new content");

        // ターゲットに既存のファイルを作成
        final Path targetFile = this.targetDir.resolve("file.txt");
        Files.writeString(targetFile, "old content");

        // コピー操作を実行
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        // 検証
        Assertions.assertTrue(Files.exists(targetFile), "ターゲットファイルが存在すること");
        Assertions.assertEquals("new content", Files.readString(targetFile), "ファイルが正しく上書きされていること");
    }

    /**
     * 空のディレクトリ構造のコピーテスト
     *
     * @throws IOException
     *                     ファイル操作時に発生する可能性のあるIO例外
     */
    @Test
    void testCopyEmptyDirectoryStructure() throws IOException {
        // 空のディレクトリ構造を作成
        final Path subDir1 = this.sourceDir.resolve("empty1");
        final Path subDir2 = this.sourceDir.resolve("empty2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        // コピー操作を実行
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        // ディレクトリ構造が正しくコピーされたことを検証
        Assertions.assertTrue(Files.isDirectory(this.targetDir.resolve("empty1")));
        Assertions.assertTrue(Files.isDirectory(this.targetDir.resolve("empty2")));
    }
}
