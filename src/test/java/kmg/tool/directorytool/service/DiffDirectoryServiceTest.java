package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 差分検出操作を実行するサービスのテストクラス。
 */
@ExtendWith(SpringExtension.class)
public class DiffDirectoryServiceTest extends AbstractDirectoryServiceTest {

    @Override
    protected AbstractDirectoryService createService() {
        return new DiffDirectoryService();
    }

    /**
     * 基本的な差分検出のテスト
     */
    @Test
    void testBasicDiffOperation() throws IOException {
        // 標準出力をキャプチャするための設定
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // ソースディレクトリにのみ存在するファイルを作成
            Path sourceOnlyFile = sourceDir.resolve("source_only.txt");
            Files.writeString(sourceOnlyFile, "source content");

            // 両方のディレクトリに存在するが内容が異なるファイル
            Path sourceFile = sourceDir.resolve("different.txt");
            Path targetFile = targetDir.resolve("different.txt");
            Files.writeString(sourceFile, "source content");
            Files.writeString(targetFile, "target content");

            // ターゲットディレクトリにのみ存在するファイル
            Path targetOnlyFile = targetDir.resolve("target_only.txt");
            Files.writeString(targetOnlyFile, "target content");

            // 差分検出を実行
            service.processDirectory(sourceDir.toString(), targetDir.toString());

            // 出力内容を検証
            String output = outContent.toString();
            assertTrue(output.contains("Only in source: source_only.txt"),
                    "ソースディレクトリにのみ存在するファイルが検出されること");
            assertTrue(output.contains("Different: different.txt"), "内容が異なるファイルが検出されること");
            assertTrue(output.contains("Only in destination: target_only.txt"),
                    "ターゲットディレクトリにのみ存在するファイルが検出されること");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * 複雑なディレクトリ構造の差分検出テスト
     */
    @Test
    void testComplexDirectoryStructureDiff() throws IOException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // ソース側のディレクトリ構造を作成
            Path sourceSubDir = sourceDir.resolve("subdir1");
            Files.createDirectories(sourceSubDir);
            Files.writeString(sourceSubDir.resolve("file1.txt"), "content1");

            // ターゲット側の異なるディレクトリ構造を作成
            Path targetSubDir = targetDir.resolve("subdir2");
            Files.createDirectories(targetSubDir);
            Files.writeString(targetSubDir.resolve("file2.txt"), "content2");

            // 差分検出を実行
            service.processDirectory(sourceDir.toString(), targetDir.toString());

            // 出力内容を検証
            String output = outContent.toString();
            assertTrue(output.contains("Directory only in source: subdir1"),
                    "ソースディレクトリにのみ存在するディレクトリが検出されること");
            assertTrue(output.contains("Only in source: subdir1/file1.txt"),
                    "ソースディレクトリ内のファイルが検出されること");
            assertTrue(output.contains("Directory only in destination: subdir2"),
                    "ターゲットディレクトリにのみ存在するディレクトリが検出されること");
            assertTrue(output.contains("Only in destination: subdir2/file2.txt"),
                    "ターゲットディレクトリ内のファイルが検出されること");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * 同一内容のファイルの差分検出テスト
     */
    @Test
    void testIdenticalFiles() throws IOException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // 同じ内容のファイルを両方のディレクトリに作成
            String content = "identical content";
            Files.writeString(sourceDir.resolve("same.txt"), content);
            Files.writeString(targetDir.resolve("same.txt"), content);

            // 差分検出を実行
            service.processDirectory(sourceDir.toString(), targetDir.toString());

            // 出力内容を検証（差分が報告されないことを確認）
            String output = outContent.toString();
            assertTrue(!output.contains("same.txt"), "同一内容のファイルは差分として報告されないこと");
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * 空のディレクトリ構造の差分検出テスト
     */
    @Test
    void testEmptyDirectoryDiff() throws IOException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // ソース側にのみ空のディレクトリを作成
            Path sourceEmptyDir = sourceDir.resolve("empty_source");
            Files.createDirectories(sourceEmptyDir);

            // ターゲット側にのみ空のディレクトリを作成
            Path targetEmptyDir = targetDir.resolve("empty_target");
            Files.createDirectories(targetEmptyDir);

            // 差分検出を実行
            service.processDirectory(sourceDir.toString(), targetDir.toString());

            // 出力内容を検証
            String output = outContent.toString();
            assertTrue(output.contains("Directory only in source: empty_source"),
                    "ソースの空ディレクトリが検出されること");
            assertTrue(output.contains("Directory only in destination: empty_target"),
                    "ターゲットの空ディレクトリが検出されること");
        } finally {
            System.setOut(originalOut);
        }
    }
}
