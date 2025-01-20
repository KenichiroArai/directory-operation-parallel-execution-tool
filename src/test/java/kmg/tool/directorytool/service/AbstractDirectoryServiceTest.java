package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

/**
 * ディレクトリ操作サービスのテストのための基底クラス。 共通のセットアップ、検証、クリーンアップ機能を提供する。
 */
public abstract class AbstractDirectoryServiceTest {

    /** テスト用の一時ディレクトリ */
    @TempDir
    protected Path tempDir;

    /** テスト用のソースディレクトリ */
    protected Path sourceDir;

    /** テスト用のターゲットディレクトリ */
    protected Path targetDir;

    /** テスト対象のディレクトリ操作サービス */
    protected AbstractDirectoryService service;

    /**
     * テスト対象のサービスを作成する
     *
     * @return テスト対象のサービスインスタンス
     */
    protected abstract AbstractDirectoryService createService();

    /**
     * テストの前準備
     *
     * @throws IOException
     *                     ディレクトリの作成に失敗した場合
     */
    @BeforeEach
    public void setUp() throws IOException {

        // テスト用のディレクトリ構造を作成
        this.sourceDir = this.tempDir.resolve("source");
        this.targetDir = this.tempDir.resolve("target");
        Files.createDirectories(this.sourceDir);
        Files.createDirectories(this.targetDir);

        // テスト対象のサービスを初期化
        this.service = this.createService();

    }

    /**
     * 存在しないソースディレクトリに対する操作をテストする。
     * <p>
     * 存在しないディレクトリに対して処理を実行した場合、適切な例外がスローされることを確認する。
     * </p>
     */
    @org.junit.jupiter.api.Test
    public void testNonExistentSourceDirectory() {

        final Path nonExistentDir = this.tempDir.resolve("non-existent");

        final IOException exception = Assertions.assertThrows(IOException.class,
                () -> this.service.processDirectory(nonExistentDir.toString(), this.targetDir.toString()));

        Assertions.assertEquals("ソースディレクトリが存在しません。", exception.getMessage());

    }

    /**
     * ソースパスがファイルの場合の動作をテストする。
     * <p>
     * ディレクトリではなくファイルを指定した場合、適切な例外がスローされることを確認する。
     * </p>
     *
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @org.junit.jupiter.api.Test
    public void testSourcePathNotDirectory() throws IOException {

        final Path sourceFile = this.tempDir.resolve("source.txt");
        Files.writeString(sourceFile, "test");

        final IOException exception = Assertions.assertThrows(IOException.class,
                () -> this.service.processDirectory(sourceFile.toString(), this.targetDir.toString()));

        Assertions.assertEquals("ソースパスはディレクトリではありません。", exception.getMessage());

    }

    /**
     * ターゲットパスが既存のファイルの場合の動作をテストする。
     * <p>
     * ターゲットパスとして既存のファイルを指定した場合、適切な例外がスローされることを確認する。
     * </p>
     *
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @org.junit.jupiter.api.Test
    public void testTargetPathExistsAsFile() throws IOException {

        Files.createDirectories(this.sourceDir);
        final Path targetFile = this.tempDir.resolve("target_file.txt");
        Files.writeString(targetFile, "existing file");

        final IOException exception = Assertions.assertThrows(IOException.class,
                () -> this.service.processDirectory(this.sourceDir.toString(), targetFile.toString()));

        Assertions.assertEquals("宛先パスは存在しますが、ディレクトリではありません。", exception.getMessage());

    }

    /**
     * 空のディレクトリに対する操作をテストする。
     * <p>
     * 空のディレクトリに対して処理を実行した場合、正常にターゲットディレクトリが作成されることを確認する。
     * </p>
     *
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @org.junit.jupiter.api.Test
    public void testEmptyDirectoryOperation() throws IOException {

        // 操作を実行
        this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

        /* 検証 */
        Assertions.assertTrue(Files.exists(this.targetDir), "ターゲットディレクトリが作成されていること");
        Assertions.assertTrue(Files.isDirectory(this.targetDir), "ターゲットパスがディレクトリであること");

    }

    /**
     * サイズの異なるファイルの比較をテストする。
     * <p>
     * 異なるサイズのファイルを比較した場合、falseが返されることを確認する。
     * </p>
     *
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @org.junit.jupiter.api.Test
    public void testCompareFilesWithDifferentSizes() throws IOException {

        // 異なるサイズのファイルを作成
        final Path file1 = this.tempDir.resolve("file1.txt");
        final Path file2 = this.tempDir.resolve("file2.txt");
        Files.writeString(file1, "content1");
        Files.writeString(file2, "different content");

        Assertions.assertFalse(AbstractDirectoryService.compareFiles(file1, file2), "異なるサイズのファイルは不一致となるべき");

    }

    /**
     * ファイル処理時の例外発生をテストする。
     * <p>
     * ファイル処理中に例外が発生した場合、適切に処理されることを確認する。
     * </p>
     *
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @org.junit.jupiter.api.Test
    public void testFileProcessingException() throws IOException {

        // テスト用のファイルを作成
        final Path sourceFile = this.sourceDir.resolve("test.txt");
        Files.writeString(sourceFile, "test content");

        // サービスをオーバーライドして例外をシミュレート
        final AbstractDirectoryService errorService = new AbstractDirectoryService() {

            @Override
            protected void processPath(final Path sourcePath, final Path targetPath, final Path relativePath)
                    throws IOException {

                throw new IOException("ディレクトリの処理に失敗しました。");

            }

            @Override
            protected void postProcess(final Path source, final Path destination) throws IOException {

                // 何もしない
            }
        };

        // 処理を実行し、例外が発生することを確認
        final IOException exception = Assertions.assertThrows(IOException.class,
                () -> errorService.processDirectory(this.sourceDir.toString(), this.targetDir.toString()));
        Assertions.assertEquals("ディレクトリの処理に失敗しました。", exception.getMessage());

    }

    /**
     * タスク実行のタイムアウトをテストする。
     * <p>
     * 処理が制限時間を超えた場合、適切にタイムアウト例外がスローされることを確認する。
     * </p>
     *
     * @throws IOException
     *                     ファイル操作に失敗した場合
     */
    @org.junit.jupiter.api.Test
    public void testTaskExecutionTimeout() throws IOException {

        // 長時間実行されるタスクを作成
        final Path sourceFile = this.sourceDir.resolve("test.txt");
        Files.writeString(sourceFile, "test content");

        // サービスをオーバーライドして長時間のタスクをシミュレート
        final AbstractDirectoryService slowService = new AbstractDirectoryService() {

            @Override
            protected void processPath(final Path sourcePath, final Path targetPath, final Path relativePath)
                    throws IOException {

                try {

                    Thread.sleep(31000); // 31秒間スリープ（タイムアウトは30秒）

                } catch (@SuppressWarnings("unused") final InterruptedException ignored) {

                    Thread.currentThread().interrupt();

                }

            }

            @Override
            protected void postProcess(final Path source, final Path destination) throws IOException {

                // 何もしない
            }
        };

        // タイムアウトで例外が発生することを確認
        final IOException exception = Assertions.assertThrows(IOException.class,
                () -> slowService.processDirectory(this.sourceDir.toString(), this.targetDir.toString()));
        Assertions.assertNotNull(exception.getMessage(), "タイムアウトエラーメッセージが存在すること");

    }

    /**
     * テスト後のクリーンアップを実行する。 一時ファイルとディレクトリを削除する。
     *
     * @throws IOException
     *                     クリーンアップ操作に失敗した場合
     */
    @AfterEach
    public void tearDown() throws IOException {

        // テスト後のクリーンアップ
        if (Files.exists(this.sourceDir)) {

            try (Stream<Path> walk = Files.walk(this.sourceDir)) {

                walk.sorted((a, b) -> b.toString().length() - a.toString().length()).forEach(path -> {

                    try {

                        Files.delete(path);

                    } catch (@SuppressWarnings("unused") final IOException ignored) {

                        // クリーンアップ中のエラーは無視
                    }

                });

            }

        }

        if (Files.exists(this.targetDir)) {

            try (Stream<Path> walk = Files.walk(this.targetDir)) {

                walk.sorted((a, b) -> b.toString().length() - a.toString().length()).forEach(path -> {

                    try {

                        Files.delete(path);

                    } catch (@SuppressWarnings("unused") final IOException ignored) {

                        // クリーンアップ中のエラーは無視
                    }

                });

            }

        }

    }
}
