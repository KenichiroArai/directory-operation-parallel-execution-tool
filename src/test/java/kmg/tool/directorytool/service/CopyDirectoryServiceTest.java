package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * コピー操作を実行するサービスのテストクラス。
 */
public class CopyDirectoryServiceTest extends AbstractDirectoryServiceTest {

    /**
     * コピーサービスのインスタンスを生成します。
     *
     * @return コピーサービスのインスタンス
     */
    @Override
    protected AbstractDirectoryService createService() {
        final AbstractDirectoryService result = new CopyDirectoryService();
        return result;
    }

    /**
     * 基本的なファイルコピー操作のテスト
     *
     * @throws IOException
     *                     ファイル操作時に発生する可能性のあるIO例外
     */
    @Test
    public void testBasicCopyOperation() throws IOException {

        /* 期待値の定義 */
        final String expectedFileContent = "test content";

        /* 準備 */
        final Path testFile = this.sourceDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final Path    actualCopiedFile        = this.targetDir.resolve("test.txt");
        final boolean actualCopiedFileExists  = Files.exists(actualCopiedFile);
        final boolean actualTestFileExists    = Files.exists(testFile);
        final String  actualCopiedFileContent = Files.readString(actualCopiedFile);

        /* 検証の実施 */
        Assertions.assertTrue(actualCopiedFileExists, "コピーされたファイルが存在すること");
        Assertions.assertTrue(actualTestFileExists, "元のファイルが残っていること");
        Assertions.assertEquals(expectedFileContent, actualCopiedFileContent, "ファイルの内容が正しくコピーされていること");
    }

    /**
     * 複雑なディレクトリ構造のコピーテスト
     *
     * @throws IOException
     *                     ファイル操作時に発生する可能性のあるIO例外
     */
    @Test
    public void testComplexDirectoryStructureCopy() throws IOException {

        /* 期待値の定義 */
        final String expectedContent1    = "content1";
        final String expectedContent2    = "content2";
        final String expectedRootContent = "root content";

        /* 準備 */
        final Path subDir1 = this.sourceDir.resolve("subdir1");
        final Path subDir2 = this.sourceDir.resolve("subdir2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);
        Files.writeString(subDir1.resolve("file1.txt"), "content1");
        Files.writeString(subDir2.resolve("file2.txt"), "content2");
        Files.writeString(this.sourceDir.resolve("root.txt"), "root content");

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final boolean actualFile1Exists    = Files.exists(this.targetDir.resolve("subdir1/file1.txt"));
        final boolean actualFile2Exists    = Files.exists(this.targetDir.resolve("subdir2/file2.txt"));
        final boolean actualRootFileExists = Files.exists(this.targetDir.resolve("root.txt"));
        final String  actualContent1       = Files.readString(this.targetDir.resolve("subdir1/file1.txt"));
        final String  actualContent2       = Files.readString(this.targetDir.resolve("subdir2/file2.txt"));
        final String  actualRootContent    = Files.readString(this.targetDir.resolve("root.txt"));

        /* 検証の実施 */
        Assertions.assertTrue(actualFile1Exists, "file1.txtが存在すること");
        Assertions.assertTrue(actualFile2Exists, "file2.txtが存在すること");
        Assertions.assertTrue(actualRootFileExists, "root.txtが存在すること");
        Assertions.assertEquals(expectedContent1, actualContent1, "file1.txtの内容が正しいこと");
        Assertions.assertEquals(expectedContent2, actualContent2, "file2.txtの内容が正しいこと");
        Assertions.assertEquals(expectedRootContent, actualRootContent, "root.txtの内容が正しいこと");
    }

    /**
     * 既存のファイルの上書きコピーのテスト
     *
     * @throws IOException
     *                     ファイル操作時に発生する可能性のあるIO例外
     */
    @Test
    public void testOverwriteExistingFile() throws IOException {

        /* 期待値の定義 */
        final String expectedContent = "new content";

        /* 準備 */
        final Path sourceFile = this.sourceDir.resolve("file.txt");
        final Path targetFile = this.targetDir.resolve("file.txt");
        Files.writeString(sourceFile, "new content");
        Files.writeString(targetFile, "old content");

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final boolean actualFileExists = Files.exists(targetFile);
        final String  actualContent    = Files.readString(targetFile);

        /* 検証の実施 */
        Assertions.assertTrue(actualFileExists, "ターゲットファイルが存在すること");
        Assertions.assertEquals(expectedContent, actualContent, "ファイルが正しく上書きされていること");
    }

    /**
     * 空のディレクトリ構造のコピーテスト
     *
     * @throws IOException
     *                     ファイル操作時に発生する可能性のあるIO例外
     */
    @Test
    public void testCopyEmptyDirectoryStructure() throws IOException {

        /* 準備 */
        final Path subDir1 = this.sourceDir.resolve("empty1");
        final Path subDir2 = this.sourceDir.resolve("empty2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final boolean actualEmpty1Exists = Files.isDirectory(this.targetDir.resolve("empty1"));
        final boolean actualEmpty2Exists = Files.isDirectory(this.targetDir.resolve("empty2"));

        /* 検証の実施 */
        Assertions.assertTrue(actualEmpty1Exists, "empty1ディレクトリが存在すること");
        Assertions.assertTrue(actualEmpty2Exists, "empty2ディレクトリが存在すること");
    }
}
