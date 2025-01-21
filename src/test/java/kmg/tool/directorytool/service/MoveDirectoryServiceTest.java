package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

/**
 * 移動操作を実行するサービスのテストクラス。
 */
@ExtendWith(MockitoExtension.class)
public class MoveDirectoryServiceTest extends AbstractDirectoryServiceTest {

    /**
     * 移動サービスのインスタンスを生成します。
     *
     * @return 移動サービスのインスタンス
     */
    @Override
    protected AbstractDirectoryService createService() {

        final AbstractDirectoryService result = new MoveDirectoryService();
        return result;

    }

    /**
     * 基本的なファイル移動操作のテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testBasicMoveOperation() throws IOException {

        /* 期待値の定義 */
        final String expectedContent = "test content";

        /* 準備 */
        final Path testFile = this.sourceDir.resolve("test.txt");
        Files.writeString(testFile, expectedContent);

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final Path    movedFile          = this.targetDir.resolve("test.txt");
        final boolean actualTargetExists = Files.exists(movedFile);
        final boolean actualSourceExists = Files.exists(testFile);
        final String  actualContent      = Files.readString(movedFile);

        /* 検証の実施 */
        Assertions.assertTrue(actualTargetExists, "移動先にファイルが存在すること");
        Assertions.assertFalse(actualSourceExists, "元のファイルが削除されていること");
        Assertions.assertEquals(expectedContent, actualContent, "ファイルの内容が正しく移動されていること");

    }

    /**
     * 複雑なディレクトリ構造の移動テスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testComplexDirectoryStructureMove() throws IOException {

        /* 期待値の定義 */
        final String expectedContent1    = "content1";
        final String expectedContent2    = "content2";
        final String expectedRootContent = "root content";

        /* 準備 */
        final Path subDir1 = this.sourceDir.resolve("subdir1");
        final Path subDir2 = this.sourceDir.resolve("subdir2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        Files.writeString(subDir1.resolve("file1.txt"), expectedContent1);
        Files.writeString(subDir2.resolve("file2.txt"), expectedContent2);
        Files.writeString(this.sourceDir.resolve("root.txt"), expectedRootContent);

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final boolean actualFile1Exists  = Files.exists(this.targetDir.resolve("subdir1/file1.txt"));
        final boolean actualFile2Exists  = Files.exists(this.targetDir.resolve("subdir2/file2.txt"));
        final boolean actualRootExists   = Files.exists(this.targetDir.resolve("root.txt"));
        final String  actualContent1     = Files.readString(this.targetDir.resolve("subdir1/file1.txt"));
        final String  actualContent2     = Files.readString(this.targetDir.resolve("subdir2/file2.txt"));
        final String  actualRootContent  = Files.readString(this.targetDir.resolve("root.txt"));
        final boolean actualSourceExists = Files.exists(this.sourceDir);

        /* 検証の実施 */
        Assertions.assertTrue(actualFile1Exists, "subdir1/file1.txtが存在すること");
        Assertions.assertTrue(actualFile2Exists, "subdir2/file2.txtが存在すること");
        Assertions.assertTrue(actualRootExists, "root.txtが存在すること");
        Assertions.assertEquals(expectedContent1, actualContent1, "file1.txtの内容が正しいこと");
        Assertions.assertEquals(expectedContent2, actualContent2, "file2.txtの内容が正しいこと");
        Assertions.assertEquals(expectedRootContent, actualRootContent, "root.txtの内容が正しいこと");
        Assertions.assertFalse(actualSourceExists, "ソースディレクトリが削除されていること");

    }

    /**
     * 既存のファイルの上書き移動のテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testOverwriteExistingFile() throws IOException {

        /* 期待値の定義 */
        final String expectedContent = "new content";

        /* 準備 */
        final Path testSourceFile = this.sourceDir.resolve("file.txt");
        final Path testTargetFile = this.targetDir.resolve("file.txt");
        Files.writeString(testSourceFile, expectedContent);
        Files.writeString(testTargetFile, "old content");

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final boolean actualTargetExists = Files.exists(testTargetFile);
        final boolean actualSourceExists = Files.exists(testSourceFile);
        final String  actualContent      = Files.readString(testTargetFile);

        /* 検証の実施 */
        Assertions.assertTrue(actualTargetExists, "ターゲットファイルが存在すること");
        Assertions.assertFalse(actualSourceExists, "ソースファイルが削除されていること");
        Assertions.assertEquals(expectedContent, actualContent, "ファイルが正しく上書きされていること");

    }

    /**
     * 空のディレクトリ構造の移動テスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testMoveEmptyDirectoryStructure() throws IOException {

        /* 準備 */
        final Path subDir1 = this.sourceDir.resolve("empty1");
        final Path subDir2 = this.sourceDir.resolve("empty2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final boolean actualEmpty1Exists     = Files.isDirectory(this.targetDir.resolve("empty1"));
        final boolean actualEmpty2Exists     = Files.isDirectory(this.targetDir.resolve("empty2"));
        final boolean actualSourceDir1Exists = Files.exists(subDir1);
        final boolean actualSourceDir2Exists = Files.exists(subDir2);

        /* 検証の実施 */
        Assertions.assertTrue(actualEmpty1Exists, "empty1ディレクトリが存在すること");
        Assertions.assertTrue(actualEmpty2Exists, "empty2ディレクトリが存在すること");
        Assertions.assertFalse(actualSourceDir1Exists, "元の空ディレクトリが削除されていること");
        Assertions.assertFalse(actualSourceDir2Exists, "元の空ディレクトリが削除されていること");

    }

    /**
     * ファイル削除失敗時のテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testDeleteFailure() throws IOException {

        /* 期待値の定義 */
        final String expectedContent = "test content";

        /* 準備 */
        final Path testSubDir = this.sourceDir.resolve("subdir");
        Files.createDirectories(testSubDir);
        final Path testFile = testSubDir.resolve("test.txt");
        Files.writeString(testFile, expectedContent);

        // ファイルを読み取り専用に設定する
        testFile.toFile().setReadOnly();

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final boolean actualTargetFileExists = Files.exists(this.targetDir.resolve("subdir/test.txt"));
        final boolean actualSourceFileExists = Files.exists(testFile);
        final String  actualContent          = Files.readString(this.targetDir.resolve("subdir/test.txt"));

        /* 検証の実施 */
        Assertions.assertTrue(actualTargetFileExists, "ターゲットファイルが存在すること");
        Assertions.assertFalse(actualSourceFileExists, "読み取り専用ファイルが削除されていること");
        Assertions.assertEquals(expectedContent, actualContent, "移動されたファイルの内容が正しいこと");

    }

    /**
     * postProcessメソッドのcatchブロックのテスト（ログ出力の検証）
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testPostProcessCatchBlock() throws IOException {

        /* 準備 */
        // Logbackのテストアペンダーを設定
        Logger                      logger       = (Logger) LoggerFactory.getLogger(MoveDirectoryService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        final Path testSubDir = this.sourceDir.resolve("subdir");
        Files.createDirectories(testSubDir);
        final Path testFile = testSubDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // ファイルを読み取り専用に設定し、削除できないようにする
        testFile.toFile().setReadOnly();
        testSubDir.toFile().setReadOnly();

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証 */
        // エラーログが出力されていることを確認
        Assertions.assertTrue(
                listAppender.list.stream().anyMatch(
                        event -> event.getMessage().contains("パス") && event.getMessage().contains("の削除に失敗しました")),
                "削除失敗のログが出力されていること");

        /* クリーンアップ */
        // ファイルの権限を元に戻す
        testFile.toFile().setWritable(true);
        testSubDir.toFile().setWritable(true);
        Files.deleteIfExists(testFile);
        Files.deleteIfExists(testSubDir);
        logger.detachAppender(listAppender);

    }
}
