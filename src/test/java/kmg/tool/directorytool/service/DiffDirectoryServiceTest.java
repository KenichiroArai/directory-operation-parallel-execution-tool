package kmg.tool.directorytool.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 差分検出操作を実行するサービスのテストクラス。
 */
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DiffDirectoryServiceTest extends AbstractDirectoryServiceTest {

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
     * 基本的な差分検出のテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testBasicDiffOperation() throws IOException {

        // 標準出力をキャプチャするための設定
        final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
        final PrintStream           originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {

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

            // 差分検出を実行
            this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

            // 出力内容を検証（順序に依存しない方法）
            final String output = outContent.toString();

            // 各期待される出力が含まれているか確認
            Assertions.assertTrue(output.contains("ソースのみに存在: source_only.txt"), "ソースディレクトリにのみ存在するファイルが検出されること");
            Assertions.assertTrue(output.contains("差異あり: different.txt"), "内容が異なるファイルが検出されること");
            Assertions.assertTrue(output.contains("ターゲットのみに存在: target_only.txt"), "ターゲットディレクトリにのみ存在するファイルが検出されること");

        } finally {

            System.setOut(originalOut);

        }

    }

    /**
     * 複雑なディレクトリ構造の差分検出テスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testComplexDirectoryStructureDiff() throws IOException {

        final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
        final PrintStream           originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {

            // ソース側のディレクトリ構造を作成
            final Path sourceSubDir = this.sourceDir.resolve("subdir1");
            Files.createDirectories(sourceSubDir);
            Files.writeString(sourceSubDir.resolve("file1.txt"), "content1");

            // ターゲット側の異なるディレクトリ構造を作成
            final Path targetSubDir = this.targetDir.resolve("subdir2");
            Files.createDirectories(targetSubDir);
            Files.writeString(targetSubDir.resolve("file2.txt"), "content2");

            // 差分検出を実行
            this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

            // 出力内容を検証
            final String[] outputLines = outContent.toString().split(System.lineSeparator());

            // 期待値をプラットフォームの区切り文字で正規化
            final String expectedPath1 = "subdir1" + File.separator + "file1.txt";
            final String expectedPath2 = "subdir2" + File.separator + "file2.txt";

            Assertions.assertEquals("ソースディレクトリのみに存在するディレクトリ: subdir1", outputLines[0]);
            Assertions.assertEquals("ソースのみに存在: " + expectedPath1, outputLines[1]);
            Assertions.assertEquals("ターゲットディレクトリのみに存在するディレクトリ: subdir2", outputLines[2]);
            Assertions.assertEquals("ターゲットのみに存在: " + expectedPath2, outputLines[3]);

        } finally {

            System.setOut(originalOut);

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

        final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
        final PrintStream           originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {

            // 同じ内容のファイルを両方のディレクトリに作成
            final String content = "identical content";
            Files.writeString(this.sourceDir.resolve("same.txt"), content);
            Files.writeString(this.targetDir.resolve("same.txt"), content);

            // 差分検出を実行
            this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

            // 出力内容を検証（差分が報告されないことを確認）
            final String output = outContent.toString();
            Assertions.assertTrue(!output.contains("same.txt"), "同一内容のファイルは差分として報告されないこと");

        } finally {

            System.setOut(originalOut);

        }

    }

    /**
     * 空のディレクトリ構造の差分検出テスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testEmptyDirectoryDiff() throws IOException {

        final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
        final PrintStream           originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {

            // ソース側にのみ空のディレクトリを作成
            final Path sourceEmptyDir = this.sourceDir.resolve("empty_source");
            Files.createDirectories(sourceEmptyDir);

            // ターゲット側にのみ空のディレクトリを作成
            final Path targetEmptyDir = this.targetDir.resolve("empty_target");
            Files.createDirectories(targetEmptyDir);

            // 差分検出を実行
            this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

            // 出力内容を検証
            final String[] outputLines = outContent.toString().split(System.lineSeparator());
            Assertions.assertEquals("ソースディレクトリのみに存在するディレクトリ: empty_source", outputLines[0], "ソースの空ディレクトリが検出されること");
            Assertions.assertEquals("ターゲットディレクトリのみに存在するディレクトリ: empty_target", outputLines[1], "ターゲットの空ディレクトリが検出されること");

        } finally {

            System.setOut(originalOut);

        }

    }

    /**
     * 無効なパスのテスト
     */
    @Test
    public void testInvalidPaths() {

        final Path invalidSourcePath = Path.of("/invalid/source/path");
        final Path invalidTargetPath = Path.of("/invalid/target/path");

        // ソースディレクトリが存在しない場合
        final Exception sourceException = Assertions.assertThrows(IOException.class, () -> {

            this.service.processDirectory(invalidSourcePath.toString(), this.targetDir.toString());

        });
        Assertions.assertEquals("ソースディレクトリが存在しません。", sourceException.getMessage(),
                "エラーメッセージに'ソースディレクトリが存在しません。'が含まれること");

        // ターゲットディレクトリが存在しない場合
        final Exception targetException = Assertions.assertThrows(IOException.class, () -> {

            this.service.processDirectory(this.sourceDir.toString(), invalidTargetPath.toString());

        });
        Assertions.assertEquals("ターゲットディレクトリが存在しません。: " + invalidTargetPath, targetException.getMessage(),
                "エラーメッセージに'ターゲットディレクトリが存在しません。'が含まれること");

    }

    /**
     * ファイルとディレクトリの型の違いをテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testFileVsDirectoryDiff() throws IOException {

        final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
        final PrintStream           originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {

            // ソースにファイル、ターゲットに同名のディレクトリを作成
            Files.writeString(this.sourceDir.resolve("test"), "content");
            Files.createDirectory(this.targetDir.resolve("test"));

            // 逆のケース：ソースにディレクトリ、ターゲットに同名のファイル
            final Path sourceSubDir = this.sourceDir.resolve("test2");
            Files.createDirectory(sourceSubDir);
            Files.writeString(this.targetDir.resolve("test2"), "content");

            this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());

            // 出力内容を検証
            final Set<String> actualOutput   = Arrays.stream(outContent.toString().split(System.lineSeparator()))
                    .collect(Collectors.toSet());
            final Set<String> expectedOutput = Set.of("差異あり: test (ファイル vs ディレクトリ)", "差異あり: test2 (ディレクトリ vs ファイル)");

            Assertions.assertEquals(expectedOutput, actualOutput, "ファイルとディレクトリの型の違いが正しく検出されること");

        } finally {

            System.setOut(originalOut);

        }

    }

    /**
     * ルートディレクトリの処理が除外されることのテスト
     *
     * @throws IOException
     *                     ファイル操作時にエラーが発生した場合
     */
    @Test
    public void testRootDirectoryExclusion() throws IOException {

        final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
        final PrintStream           originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {

            this.service.processDirectory(this.sourceDir.toString(), this.targetDir.toString());
            final String output = outContent.toString();
            Assertions.assertFalse(output.contains(this.sourceDir.getFileName().toString()),
                    "ルートディレクトリ自体が差分として報告されないこと");

        } finally {

            System.setOut(originalOut);

        }

    }
}
