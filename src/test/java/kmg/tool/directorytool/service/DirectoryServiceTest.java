package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kmg.tool.directorytool.model.OperationMode;

/**
 * DirectoryServiceのテストクラス 正常系、異常系、エッジケースを網羅的にテストする
 */
class DirectoryServiceTest {

    private DirectoryService directoryService;
    private CopyDirectoryService copyService;
    private MoveDirectoryService moveService;
    private DiffDirectoryService diffService;

    @TempDir
    Path tempDir;

    private Path sourceDir;
    private Path targetDir;

    /**
     * テストの前準備
     */
    @BeforeEach
    public void setUp() throws IOException {
        // 各サービスのインスタンスを作成
        copyService = new CopyDirectoryService();
        moveService = new MoveDirectoryService();
        diffService = new DiffDirectoryService();

        // DirectoryServiceに依存サービスを注入
        directoryService = new DirectoryService(copyService, moveService, diffService);

        // テスト用のディレクトリ構造を作成
        sourceDir = tempDir.resolve("source");
        targetDir = tempDir.resolve("target");
        Files.createDirectories(sourceDir);
        Files.createDirectories(targetDir);
    }

    /**
     * 基本的なファイルコピー操作のテスト
     */
    @Test
    void testCopyOperation() throws IOException {
        // テストファイルを作成
        Path testFile = sourceDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // コピー操作を実行
        directoryService.processDirectory(sourceDir.toString(), targetDir.toString(),
                OperationMode.COPY);

        // 検証
        Path copiedFile = targetDir.resolve("test.txt");
        assertTrue(Files.exists(copiedFile), "コピーされたファイルが存在すること");
        assertTrue(Files.exists(testFile), "元のファイルが残っていること");
        assertEquals("test content", Files.readString(copiedFile), "ファイルの内容が正しくコピーされていること");
    }

    /**
     * 基本的なファイル移動操作のテスト
     */
    @Test
    void testMoveOperation() throws IOException {
        // テストファイルを作成
        Path testFile = sourceDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // 移動操作を実行
        directoryService.processDirectory(sourceDir.toString(), targetDir.toString(),
                OperationMode.MOVE);

        // 検証
        Path movedFile = targetDir.resolve("test.txt");
        assertTrue(Files.exists(movedFile), "移動先にファイルが存在すること");
        assertFalse(Files.exists(testFile), "元のファイルが削除されていること");
        assertEquals("test content", Files.readString(movedFile), "ファイルの内容が正しく移動されていること");
    }

    /**
     * 存在しないソースディレクトリに対するテスト
     */
    @Test
    void testNonExistentSourceDirectory() {
        Path nonExistentDir = tempDir.resolve("non-existent");

        IOException exception = assertThrows(IOException.class,
                () -> directoryService.processDirectory(nonExistentDir.toString(),
                        targetDir.toString(), OperationMode.COPY));

        assertEquals("Source directory does not exist", exception.getMessage());
    }

    /**
     * ソースパスがディレクトリでない場合のテスト
     */
    @Test
    void testSourcePathNotDirectory() throws IOException {
        Path sourceFile = tempDir.resolve("source.txt");
        Files.writeString(sourceFile, "test");

        IOException exception = assertThrows(IOException.class, () -> directoryService
                .processDirectory(sourceFile.toString(), targetDir.toString(), OperationMode.COPY));

        assertEquals("Source path is not a directory", exception.getMessage());
    }

    /**
     * ターゲットパスが既存のファイルの場合のテスト
     */
    @Test
    void testTargetPathExistsAsFile() throws IOException {
        Files.createDirectories(sourceDir);
        Path targetFile = tempDir.resolve("target_file.txt");
        Files.writeString(targetFile, "existing file");

        IOException exception = assertThrows(IOException.class, () -> directoryService
                .processDirectory(sourceDir.toString(), targetFile.toString(), OperationMode.COPY));

        assertEquals("Destination path exists but is not a directory", exception.getMessage());
    }

    /**
     * 複雑なディレクトリ構造のコピーテスト
     */
    @Test
    void testCopyComplexDirectoryStructure() throws IOException {
        // 複雑なディレクトリ構造を作成
        Path subDir1 = sourceDir.resolve("subdir1");
        Path subDir2 = sourceDir.resolve("subdir2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        Files.writeString(subDir1.resolve("file1.txt"), "content1");
        Files.writeString(subDir2.resolve("file2.txt"), "content2");
        Files.writeString(sourceDir.resolve("root.txt"), "root content");

        // コピー操作を実行
        directoryService.processDirectory(sourceDir.toString(), targetDir.toString(),
                OperationMode.COPY);

        // すべてのファイルとディレクトリが正しくコピーされたことを検証
        assertTrue(Files.exists(targetDir.resolve("subdir1/file1.txt")));
        assertTrue(Files.exists(targetDir.resolve("subdir2/file2.txt")));
        assertTrue(Files.exists(targetDir.resolve("root.txt")));

        assertEquals("content1", Files.readString(targetDir.resolve("subdir1/file1.txt")));
        assertEquals("content2", Files.readString(targetDir.resolve("subdir2/file2.txt")));
        assertEquals("root content", Files.readString(targetDir.resolve("root.txt")));
    }

    /**
     * 空のディレクトリに対する操作のテスト
     */
    @Test
    void testEmptyDirectoryOperation() throws IOException {
        // コピー操作を実行
        directoryService.processDirectory(sourceDir.toString(), targetDir.toString(),
                OperationMode.COPY);

        // 検証
        assertTrue(Files.exists(targetDir), "ターゲットディレクトリが作成されていること");
        assertTrue(Files.isDirectory(targetDir), "ターゲットパスがディレクトリであること");

        List<Path> targetContents = Files.list(targetDir).collect(Collectors.toList());
        assertEquals(0, targetContents.size(), "ターゲットディレクトリが空であること");
    }

    /**
     * DIFFモードで内容が異なるファイルを検出するテスト
     */
    @Test
    void testDiffWithDifferentContent() throws IOException {
        // ソースディレクトリにファイルを作成
        Path sourceFile = sourceDir.resolve("test.txt");
        Files.writeString(sourceFile, "source content");

        // ターゲットディレクトリにファイルを作成
        Path targetFile = targetDir.resolve("test.txt");
        Files.writeString(targetFile, "different content");

        // DIFFモードで実行
        directoryService.processDirectory(sourceDir.toString(), targetDir.toString(),
                OperationMode.DIFF);

        // 検証
        assertTrue(Files.exists(sourceFile), "ソースファイルが存在すること");
        assertTrue(Files.exists(targetFile), "ターゲットファイルが存在すること");
        assertFalse(Files.readString(sourceFile).equals(Files.readString(targetFile)),
                "ファイルの内容が異なること");
    }

    /**
     * DIFFモードでターゲットディレクトリに追加のファイルがある場合のテスト
     */
    @Test
    void testDiffWithExtraFilesInTarget() throws IOException {
        // ソースディレクトリにファイルを作成
        Path sourceFile = sourceDir.resolve("common.txt");
        Files.writeString(sourceFile, "common content");

        // ターゲットディレクトリに追加のファイルを作成
        Path targetCommonFile = targetDir.resolve("common.txt");
        Path targetExtraFile = targetDir.resolve("extra.txt");
        Files.writeString(targetCommonFile, "common content");
        Files.writeString(targetExtraFile, "extra content");

        // DIFFモードで実行
        directoryService.processDirectory(sourceDir.toString(), targetDir.toString(),
                OperationMode.DIFF);

        // 検証
        assertTrue(Files.exists(sourceFile), "共通ファイルがソースに存在すること");
        assertTrue(Files.exists(targetExtraFile), "追加ファイルがターゲットに存在すること");
    }

    /**
     * テスト後のクリーンアップ処理
     */
    @AfterEach
    public void tearDown() throws IOException {
        // テスト後のクリーンアップ
        if (Files.exists(sourceDir)) {
            Files.walk(sourceDir).sorted((a, b) -> b.toString().length() - a.toString().length())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // クリーンアップ中のエラーは無視
                        }
                    });
        }

        if (Files.exists(targetDir)) {
            Files.walk(targetDir).sorted((a, b) -> b.toString().length() - a.toString().length())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // クリーンアップ中のエラーは無視
                        }
                    });
        }
    }
}
