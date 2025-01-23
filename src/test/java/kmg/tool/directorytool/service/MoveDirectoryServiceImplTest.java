package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import kmg.tool.directorytool.service.impl.AbstractDirectoryServiceImpl;
import kmg.tool.directorytool.service.impl.MoveDirectoryServiceImpl;

/**
 * 移動操作を実行するサービスのテストクラス。
 */
@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(MockitoExtension.class)
public class MoveDirectoryServiceImplTest extends AbstractDirectoryServiceImplTest {

    /** ロガー */
    private Logger logger;

    /** ログアペンダー */
    private ListAppender<ILoggingEvent> listAppender;

    /**
     * 移動サービスのインスタンスを生成します。
     *
     * @return 移動サービスのインスタンス
     */
    @Override
    protected AbstractDirectoryServiceImpl createService() {

        final AbstractDirectoryServiceImpl result = new MoveDirectoryServiceImpl();
        return result;

    }

    /**
     * テストの前準備
     */
    @BeforeEach
    @Override
    public void setUp() throws IOException {

        super.setUp();

        // ロガーとアペンダーの設定
        this.logger = (Logger) LoggerFactory.getLogger(MoveDirectoryServiceImpl.class);
        this.listAppender = new ListAppender<>();
        this.listAppender.start();
        this.logger.addAppender(this.listAppender);

    }

    /**
     * テスト後のクリーンアップ
     */
    @Override
    @AfterEach
    public void tearDown() {

        this.logger.detachAppender(this.listAppender);

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
        final String expectedErrorMessageFormat = "パス '%s' の削除に失敗しました";
        final String expectedErrorMessage       = String.format(expectedErrorMessageFormat, this.sourceDir);

        /* 期待値の定義 */
        final String[] expectedOutputLines = {
                expectedErrorMessage,
        };

        /* 準備 */

        // テストファイルの作成
        final Path   testFile    = this.sourceDir.resolve("test.txt");
        final String testContent = "test content";
        Files.writeString(testFile, testContent);

        // テスト用のサブクラスを作成
        class TestMoveDirectoryService extends MoveDirectoryServiceImpl {

            @Override
            protected void deleteIfExists(final Path path) throws IOException {

                throw new IOException(String.format(expectedErrorMessageFormat, path));

            }
        }

        // テスト用のサービスを作成
        final TestMoveDirectoryService testService = new TestMoveDirectoryService();

        /* テスト対象の実行 */
        testService.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final boolean  actualSourceExists = Files.exists(this.sourceDir);
        final boolean  actualTargetExists = Files.exists(this.targetDir.resolve("test.txt"));
        final String[] actualLogMessages  = this.listAppender.list.stream().map(ILoggingEvent::getFormattedMessage)
                .toArray(String[]::new);

        /* 検証の実施 */
        Assertions.assertTrue(actualSourceExists, "ソースファイルが削除されずに残っていること");
        Assertions.assertTrue(actualTargetExists, "ターゲットファイルが正しく作成されていること");
        Assertions.assertEquals(expectedOutputLines.length, actualLogMessages.length, "ログメッセージの数が正しいこと");

        for (int i = 0; i < expectedOutputLines.length; i++) {

            final String expectedLine = expectedOutputLines[i];
            Assertions.assertEquals(expectedLine, actualLogMessages[i],
                    String.format("メッセージ%d: 出力に「%s」が含まれること", i + 1, expectedLine));

        }

    }

}
