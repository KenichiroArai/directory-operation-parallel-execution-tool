package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 移動操作を実行するサービスのテストクラス。
 */
class MoveDirectoryServiceTest extends AbstractDirectoryServiceTest {

    /**
     * 移動サービスのインスタンスを生成します。
     *
     * @return 移動サービスのインスタンス
     */
    @Override
    protected AbstractDirectoryService createService() {
        return new MoveDirectoryService();
    }

    /**
     * 基本的なファイル移動操作のテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    void testBasicMoveOperation() throws IOException {
        // テストファイルを作成
        final Path testFile = this.sourceDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // 移動操作を実行
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        // 検証
        final Path movedFile = this.targetDir.resolve("test.txt");
        Assertions.assertTrue(Files.exists(movedFile), "移動先にファイルが存在すること");
        Assertions.assertFalse(Files.exists(testFile), "元のファイルが削除されていること");
        Assertions.assertEquals("test content", Files.readString(movedFile), "ファイルの内容が正しく移動されていること");
    }

    /**
     * 複雑なディレクトリ構造の移動テスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    void testComplexDirectoryStructureMove() throws IOException {
        // 複雑なディレクトリ構造を作成
        final Path subDir1 = this.sourceDir.resolve("subdir1");
        final Path subDir2 = this.sourceDir.resolve("subdir2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        Files.writeString(subDir1.resolve("file1.txt"), "content1");
        Files.writeString(subDir2.resolve("file2.txt"), "content2");
        Files.writeString(this.sourceDir.resolve("root.txt"), "root content");

        // 移動操作を実行
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        // ファイルが正しく移動されたことを検証
        Assertions.assertTrue(Files.exists(this.targetDir.resolve("subdir1/file1.txt")));
        Assertions.assertTrue(Files.exists(this.targetDir.resolve("subdir2/file2.txt")));
        Assertions.assertTrue(Files.exists(this.targetDir.resolve("root.txt")));

        Assertions.assertEquals("content1", Files.readString(this.targetDir.resolve("subdir1/file1.txt")));
        Assertions.assertEquals("content2", Files.readString(this.targetDir.resolve("subdir2/file2.txt")));
        Assertions.assertEquals("root content", Files.readString(this.targetDir.resolve("root.txt")));

        // 元のファイルとディレクトリが削除されていることを検証
        Assertions.assertFalse(Files.exists(this.sourceDir), "ソースディレクトリが削除されていること");
    }

    /**
     * 既存のファイルの上書き移動のテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    void testOverwriteExistingFile() throws IOException {
        // ソースファイルを作成
        final Path sourceFile = this.sourceDir.resolve("file.txt");
        Files.writeString(sourceFile, "new content");

        // ターゲットに既存のファイルを作成
        final Path targetFile = this.targetDir.resolve("file.txt");
        Files.writeString(targetFile, "old content");

        // 移動操作を実行
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        // 検証
        Assertions.assertTrue(Files.exists(targetFile), "ターゲットファイルが存在すること");
        Assertions.assertFalse(Files.exists(sourceFile), "ソースファイルが削除されていること");
        Assertions.assertEquals("new content", Files.readString(targetFile), "ファイルが正しく上書きされていること");
    }

    /**
     * 空のディレクトリ構造の移動テスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    void testMoveEmptyDirectoryStructure() throws IOException {
        // 空のディレクトリ構造を作成
        final Path subDir1 = this.sourceDir.resolve("empty1");
        final Path subDir2 = this.sourceDir.resolve("empty2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        // 移動操作を実行
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        // ディレクトリ構造が正しく移動されたことを検証
        Assertions.assertTrue(Files.isDirectory(this.targetDir.resolve("empty1")));
        Assertions.assertTrue(Files.isDirectory(this.targetDir.resolve("empty2")));
        Assertions.assertFalse(Files.exists(subDir1), "元の空ディレクトリが削除されていること");
        Assertions.assertFalse(Files.exists(subDir2), "元の空ディレクトリが削除されていること");
    }
}
