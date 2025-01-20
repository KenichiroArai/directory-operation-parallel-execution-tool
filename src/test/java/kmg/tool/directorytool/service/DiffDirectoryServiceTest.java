package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * 差分検出操作を実行するサービスのテストクラス。
 */
@Execution(ExecutionMode.SAME_THREAD)
public class DiffDirectoryServiceTest extends AbstractDirectoryServiceTest {

    /** ロガー */
    private Logger logger;

    /** ログアペンダー */
    private ListAppender<ILoggingEvent> listAppender;

    /**
     * 差分検出サービスのインスタンスを生成します。
     *
     * @return 差分検出サービスのインスタンス
     */
    @Override
    protected AbstractDirectoryService createService() {

        final AbstractDirectoryService result = new DiffDirectoryService();
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
        this.logger = (Logger) LoggerFactory.getLogger(DiffDirectoryService.class);
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
     * 基本的な差分検出のテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testBasicDiffOperation() throws IOException {

        /* 期待値の定義 */
        final String expectedSourceOnlyMessage = "ソースのみに存在: source_only.txt";
        final String expectedDifferentMessage  = "差異あり: different.txt";
        final String expectedTargetOnlyMessage = "ターゲットのみに存在: target_only.txt";

        /* 準備 */
        // ソースディレクトリにのみ存在するファイルを作成
        final Path sourceOnlyFile = this.sourceDir.resolve("source_only.txt");
        Files.writeString(sourceOnlyFile, "source content");

        // 両方のディレクトリに存在するが内容が異なるファイル
        final Path sourceFile = this.sourceDir.resolve("different.txt");
        final Path targetFile = this.targetDir.resolve("different.txt");
        Files.writeString(sourceFile, "source content");
        Files.writeString(targetFile, "target content");

        // ターゲットディレクトリにのみ存在するファイル
        final Path targetOnlyFile = this.targetDir.resolve("target_only.txt");
        Files.writeString(targetOnlyFile, "target content");

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final List<String> logMessages = this.listAppender.list.stream().map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());

        /* 検証の実施 */
        Assertions.assertEquals(true, logMessages.contains(expectedSourceOnlyMessage), "ソースディレクトリにのみ存在するファイルが検出されること");
        Assertions.assertEquals(true, logMessages.contains(expectedDifferentMessage), "内容が異なるファイルが検出されること");
        Assertions.assertEquals(true, logMessages.contains(expectedTargetOnlyMessage),
                "ターゲットディレクトリにのみ存在するファイルが検出されること");

    }

    /**
     * 複雑なディレクトリ構造の差分検出テスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testComplexDirectoryStructureDiff() throws IOException {

        /* 期待値の定義 */
        final String   expectedPath1       = Paths.get("subdir1", "file1.txt").toString();
        final String   expectedPath2       = Paths.get("subdir2", "file2.txt").toString();
        final String[] expectedOutputLines = {
                "ソースディレクトリのみに存在するディレクトリ: subdir1", "ソースのみに存在: " + expectedPath1, "ターゲットディレクトリのみに存在するディレクトリ: subdir2",
                "ターゲットのみに存在: " + expectedPath2
        };

        /* 準備 */
        // ソース側のディレクトリ構造を作成
        final Path sourceSubDir = this.sourceDir.resolve("subdir1");
        Files.createDirectories(sourceSubDir);
        Files.writeString(sourceSubDir.resolve("file1.txt"), "content1");

        // ターゲット側の異なるディレクトリ構造を作成
        final Path targetSubDir = this.targetDir.resolve("subdir2");
        Files.createDirectories(targetSubDir);
        Files.writeString(targetSubDir.resolve("file2.txt"), "content2");

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final List<String> logMessages = this.listAppender.list.stream().map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());

        /* 検証の実施 */
        for (final String expectedLine : expectedOutputLines) {

            Assertions.assertEquals(true, logMessages.contains(expectedLine), "出力に「" + expectedLine + "」が含まれること");

        }

    }

    /**
     * 同一内容のファイルの差分検出テスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testIdenticalFiles() throws IOException {

        /* 期待値の定義 */
        final String expectedFileName = "same.txt";

        /* 準備 */
        // 同じ内容のファイルを両方のディレクトリに作成
        final String content = "identical content";
        Files.writeString(this.sourceDir.resolve(expectedFileName), content);
        Files.writeString(this.targetDir.resolve(expectedFileName), content);

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final List<String> logMessages = this.listAppender.list.stream().map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());

        /* 検証の実施 */
        Assertions.assertFalse(logMessages.stream().anyMatch(msg -> msg.contains(expectedFileName)),
                "同一内容のファイルは差分として報告されないこと");

    }

    /**
     * 空のディレクトリ構造の差分検出テスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testEmptyDirectoryDiff() throws IOException {

        /* 期待値の定義 */
        final String[] expectedOutputLines = {
                "ソースディレクトリのみに存在するディレクトリ: empty_source", "ターゲットディレクトリのみに存在するディレクトリ: empty_target"
        };

        /* 準備 */
        // ソース側にのみ空のディレクトリを作成
        final Path sourceEmptyDir = this.sourceDir.resolve("empty_source");
        Files.createDirectories(sourceEmptyDir);

        // ターゲット側にのみ空のディレクトリを作成
        final Path targetEmptyDir = this.targetDir.resolve("empty_target");
        Files.createDirectories(targetEmptyDir);

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final List<String> logMessages = this.listAppender.list.stream().map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());

        /* 検証の実施 */
        for (final String expectedLine : expectedOutputLines) {

            Assertions.assertEquals(true, logMessages.contains(expectedLine), "出力に「" + expectedLine + "」が含まれること");

        }

    }

    /**
     * 無効なパスのテスト
     */
    @Test
    public void testInvalidPaths() {

        /* 期待値の定義 */
        final Path   invalidSourcePath          = Path.of("/invalid/source/path");
        final Path   invalidTargetPath          = Path.of("/invalid/target/path");
        final String expectedSourceErrorMessage = "ソースディレクトリが存在しません。";
        final String expectedTargetErrorMessage = "ターゲットディレクトリが存在しません。: " + invalidTargetPath;

        /* 準備 */

        /* テスト対象の実行と検証の準備 */
        final Exception actualSourceException = Assertions.assertThrows(IOException.class, () -> {

            this.service.processDirectory(invalidSourcePath.toString(), this.targetDir.toString());

        });

        final Exception actualTargetException = Assertions.assertThrows(IOException.class, () -> {

            this.service.processDirectory(this.sourceDir.toString(), invalidTargetPath.toString());

        });

        /* 検証の実施 */
        Assertions.assertEquals(expectedSourceErrorMessage, actualSourceException.getMessage(),
                "ソースディレクトリ不在時のエラーメッセージが一致すること");
        Assertions.assertEquals(expectedTargetErrorMessage, actualTargetException.getMessage(),
                "ターゲットディレクトリ不在時のエラーメッセージが一致すること");

    }

    /**
     * ファイルとディレクトリの型の違いをテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testFileVsDirectoryDiff() throws IOException {

        /* 期待値の定義 */
        final Set<String> expectedOutput = Set.of("差異あり: test (ファイル vs ディレクトリ)", "差異あり: test2 (ディレクトリ vs ファイル)");

        /* 準備 */
        // ソースにファイル、ターゲットに同名のディレクトリを作成
        Files.writeString(this.sourceDir.resolve("test"), "content");
        Files.createDirectory(this.targetDir.resolve("test"));

        // 逆のケース：ソースにディレクトリ、ターゲットに同名のファイル
        final Path sourceSubDir = this.sourceDir.resolve("test2");
        Files.createDirectory(sourceSubDir);
        Files.writeString(this.targetDir.resolve("test2"), "content");

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final Set<String> actualOutput = this.listAppender.list.stream().map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toSet());

        /* 検証の実施 */
        Assertions.assertEquals(expectedOutput, actualOutput, "ファイルとディレクトリの型の違いが正しく検出されること");

    }

    /**
     * ルートディレクトリの処理が除外されることのテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testRootDirectoryExclusion() throws IOException {

        /* 期待値の定義 */
        final String expectedNotContainPath = this.sourceDir.getFileName().toString();

        /* 準備 */

        /* テスト対象の実行 */
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証の準備 */
        final List<String> logMessages = this.listAppender.list.stream().map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());

        /* 検証の実施 */
        Assertions.assertFalse(logMessages.stream().anyMatch(msg -> msg.contains(expectedNotContainPath)),
                "ルートディレクトリ自体が差分として報告されないこと");

    }
}
