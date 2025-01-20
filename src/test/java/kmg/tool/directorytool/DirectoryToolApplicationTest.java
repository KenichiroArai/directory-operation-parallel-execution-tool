package kmg.tool.directorytool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * DirectoryToolApplicationのテストクラス。<br>
 * <p>
 * アプリケーションの主要な機能とエラーハンドリングをテストします。
 * </p>
 */
@SpringBootTest
@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(MockitoExtension.class)
public class DirectoryToolApplicationTest implements AutoCloseable {

    /** Logbackのリストアペンダー */
    private ListAppender<ILoggingEvent> listAppender;

    /** Loggerインスタンス */
    private Logger logger;

    /**
     * テストの前準備
     */
    @BeforeEach
    public void setUp() {

        // Logbackの設定
        this.logger = (Logger) LoggerFactory.getLogger(DirectoryToolApplication.class);
        this.listAppender = new ListAppender<>();
        this.listAppender.start();
        this.logger.addAppender(this.listAppender);

    }

    /**
     * テスト後のクリーンアップ
     */
    @AfterEach
    public void tearDown() {

        this.logger.detachAppender(this.listAppender);

    }

    /**
     * Springコンテキストが正常にロードされることを確認するテスト。
     */
    @SuppressWarnings("static-method")
    @Test
    public void testContextLoads() {

        /* 検証の実施 */
        Assertions.assertTrue(true, "Spring Contextが正常にロードされること");

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
    private static Path createTestDirectory(@TempDir final Path tempDir) throws IOException {

        final Path sourceDir = tempDir.resolve("test-source");
        Files.createDirectories(sourceDir);

        // テスト用のファイルとディレクトリを作成
        Files.createFile(sourceDir.resolve("test1.txt"));
        Files.createFile(sourceDir.resolve("test2.txt"));

        final Path subDir = sourceDir.resolve("subdir");
        Files.createDirectories(subDir);
        Files.createFile(subDir.resolve("test3.txt"));

        final Path result = sourceDir;
        return result;

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
    public void testMainMethodExecutesSuccessfullyInTestMode(@TempDir final Path tempDir) throws IOException {

        /* 期待値の定義 */
        final Path sourceDir = DirectoryToolApplicationTest.createTestDirectory(tempDir);
        final Path destDir   = tempDir.resolve("dest");

        /* 準備 */

        /* テスト対象の実行 */
        DirectoryToolApplication.main(new String[] {
                "COPY", sourceDir.toString(), destDir.toString()
        });

        /* 検証の準備 */
        final boolean actualDestDirExists = Files.exists(destDir);
        final boolean actualTest1Exists   = Files.exists(destDir.resolve("test1.txt"));
        final boolean actualTest2Exists   = Files.exists(destDir.resolve("test2.txt"));
        final boolean actualTest3Exists   = Files.exists(destDir.resolve("subdir/test3.txt"));

        final String logMessages = this.listAppender.list.stream().map(ILoggingEvent::getMessage)
                .collect(Collectors.joining(System.lineSeparator()));

        /* 検証の実施 */
        Assertions.assertTrue(actualDestDirExists, "destDirが存在すること");
        Assertions.assertTrue(actualTest1Exists, "test1.txtが存在すること");
        Assertions.assertTrue(actualTest2Exists, "test2.txtが存在すること");
        Assertions.assertTrue(actualTest3Exists, "subdir/test3.txtが存在すること");
        Assertions.assertEquals(true, logMessages.contains("処理が終了しました。"), "期待される処理終了メッセージが含まれていません。");

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
    public void testMainMethodExecutesSuccessfullyInNonTestMode(@TempDir final Path tempDir) throws IOException {

        /* 期待値の定義 */
        final Path sourceDir = DirectoryToolApplicationTest.createTestDirectory(tempDir);
        final Path destDir   = tempDir.resolve("dest");

        /* テスト対象の実行 */
        DirectoryToolApplication.main(new String[] {
                "COPY", sourceDir.toString(), destDir.toString()
        });

        /* 検証の準備 */
        final boolean actualDestDirExists = Files.exists(destDir);
        final boolean actualTest1Exists   = Files.exists(destDir.resolve("test1.txt"));
        final boolean actualTest2Exists   = Files.exists(destDir.resolve("test2.txt"));
        final boolean actualTest3Exists   = Files.exists(destDir.resolve("subdir/test3.txt"));

        final String logMessages = this.listAppender.list.stream().map(ILoggingEvent::getMessage)
                .collect(Collectors.joining(System.lineSeparator()));

        /* 検証の実施 */
        Assertions.assertTrue(actualDestDirExists, "destDirが存在すること");
        Assertions.assertTrue(actualTest1Exists, "test1.txtが存在すること");
        Assertions.assertTrue(actualTest2Exists, "test2.txtが存在すること");
        Assertions.assertTrue(actualTest3Exists, "subdir/test3.txt");
        Assertions.assertEquals(true, logMessages.contains("処理が終了しました。"), "期待される処理終了メッセージが含まれていません。");

    }

    /**
     * 引数不足時のエラーハンドリングをテスト。
     */
    @Test
    public void testMainMethodFailsWithInsufficientArguments() {

        /* 期待値の定義 */
        final String expected = "引数が不足しています。" + System.lineSeparator();

        /* 準備 */
        DirectoryToolApplication.main(new String[] {});

        /* 検証の準備 */
        final String actual = this.listAppender.list.stream()
                .filter(event -> "ERROR".equals(event.getLevel().toString())).map(ILoggingEvent::getMessage)
                .collect(Collectors.joining(System.lineSeparator()));

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "引数不足のエラーメッセージが期待通りであること");

    }

    /**
     * 無効な操作タイプが指定された場合のエラーハンドリングをテスト。
     */
    @Test
    public void testMainMethodFailsWithInvalidOperationType() {

        /* 期待値の定義 */
        final String expected = "無効なモードです。" + System.lineSeparator();

        /* 準備 */


        /* テスト対象の実行 */
        DirectoryToolApplication.main(new String[] {
                "INVALID", "/src", "/dest"
        });

        /* 検証の準備 */
        final String actual = this.listAppender.list.stream()
                .filter(event -> "ERROR".equals(event.getLevel().toString())).map(ILoggingEvent::getMessage)
                .collect(Collectors.joining(System.lineSeparator()));

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "無効な操作タイプのエラーメッセージが期待通りであること");

    }

    /**
     * 存在しないソースディレクトリが指定された場合のエラーハンドリングをテスト。
     */
    @Test
    public void testMainMethodFailsWithNonExistentSourceDirectory() {

        /* 期待値の定義 */
        final String expected = "ソースディレクトリが存在しません。" + System.lineSeparator();


        /* 準備 */
        DirectoryToolApplication.main(new String[] {
                "COPY", "/nonexistent", "/dest"
        });

        /* 検証の準備 */
        final String actual = this.listAppender.list.stream()
                .filter(event -> "ERROR".equals(event.getLevel().toString())).map(ILoggingEvent::getMessage)
                .collect(Collectors.joining(System.lineSeparator()));

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "存在しないソースディレクトリのエラーメッセージが期待通りであること");

    }

    /**
     * 無効なパスが指定された場合のエラーハンドリングをテスト。
     *
     * @param tempDir
     *                テスト用の一時ディレクトリ
     */
    @Test
    public void testMainMethodFailsWithInvalidPaths(@TempDir final Path tempDir) {

        /* 期待値の定義 */
        final String expected = "ソースディレクトリが存在しません。" + System.lineSeparator();


        /* 準備 */
        final Path nonExistentSource = tempDir.resolve("non-existent");
        final Path destDir           = tempDir.resolve("dest");

        DirectoryToolApplication.main(new String[] {
                "COPY", nonExistentSource.toString(), destDir.toString()
        });

        /* 検証の準備 */
        final String actual = this.listAppender.list.stream()
                .filter(event -> "ERROR".equals(event.getLevel().toString())).map(ILoggingEvent::getMessage)
                .collect(Collectors.joining(System.lineSeparator()));

        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "ソースディレクトリが存在しないメッセージが期待通りであること");


    }

    /**
     * テスト終了時のリソースクリーンアップ
     *
     * @throws IOException
     *                     クローズ処理中に発生する可能性のある例外
     */
    @Override
    public void close() throws IOException {

        // リソースのクリーンアップが必要な場合はここで実装
    }
}
