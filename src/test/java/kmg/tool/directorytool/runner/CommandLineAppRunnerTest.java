package kmg.tool.directorytool.runner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import kmg.tool.directorytool.model.OperationMode;
import kmg.tool.directorytool.service.DirectoryService;

/**
 * CommandLineAppRunnerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class CommandLineAppRunnerTest {

    /**
     * テスト対象のDirectoryServiceモック
     */
    @Mock
    private DirectoryService directoryService;

    /**
     * テスト対象のCommandLineAppRunnerインスタンス
     */
    private CommandLineAppRunner runner;

    /**
     * テスト用の出力ストリーム
     */
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    /**
     * 標準出力の元のPrintStream
     */
    private final PrintStream originalOut = System.out;

    /**
     * テストの前準備
     */
    @BeforeEach
    void setUp() {
        this.runner = new CommandLineAppRunner(this.directoryService);
        System.setOut(new PrintStream(this.outputStream));
    }

    /**
     * 正常系のコピー操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    void testSuccessfulCopyOperation() throws Exception {
        final String[] args = {
                "source", "target", "COPY"
        };
        this.runner.run(args);

        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.COPY);
        Assertions.assertTrue(this.outputStream.toString().contains("Operation completed successfully"));
    }

    /**
     * 正常系の移動操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    void testSuccessfulMoveOperation() throws Exception {
        final String[] args = {
                "source", "target", "MOVE"
        };
        this.runner.run(args);

        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.MOVE);
        Assertions.assertTrue(this.outputStream.toString().contains("Operation completed successfully"));
    }

    /**
     * 引数が不足している場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    void testInsufficientArguments() throws Exception {
        final String[] args = {
                "source", "target"
        };
        this.runner.run(args);

        Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
        final String output = this.outputStream.toString();
        Assertions.assertTrue(output.contains("Usage:"));
        Assertions.assertTrue(output.contains("Modes: COPY, MOVE, DIFF"));
    }

    /**
     * 無効な操作モードが指定された場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    void testInvalidMode() throws Exception {
        final String[] args = {
                "source", "target", "INVALID"
        };
        this.runner.run(args);

        Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertTrue(this.outputStream.toString().contains("Invalid mode: INVALID"));
    }

    /**
     * IOExceptionが発生した場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    void testIOException() throws Exception {
        final String[] args         = {
                "source", "target", "COPY"
        };
        final String   errorMessage = "Test error message";
        Mockito.doThrow(new IOException(errorMessage)).when(this.directoryService)
                .processDirectory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

        this.runner.run(args);

        Assertions.assertTrue(this.outputStream.toString().contains("Error: " + errorMessage));
    }

    /**
     * DIFFモードの操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    void testDiffOperation() throws Exception {
        final String[] args = {
                "source", "target", "DIFF"
        };
        this.runner.run(args);

        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.DIFF);
        Assertions.assertTrue(this.outputStream.toString().contains("Operation completed successfully"));
    }

    /**
     * テスト後のクリーンアップ
     */
    @AfterEach
    void tearDown() {
        System.setOut(this.originalOut);
    }
}
