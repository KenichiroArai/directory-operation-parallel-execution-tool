package kmg.tool.directorytool.runner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import kmg.tool.directorytool.model.OperationMode;
import kmg.tool.directorytool.service.DirectoryService;

/**
 * CommandLineAppRunnerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class CommandLineAppRunnerTest {

    @Mock
    private DirectoryService directoryService;

    private CommandLineAppRunner runner;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    /**
     * テストの前準備
     */
    @BeforeEach
    void setUp() {
        runner = new CommandLineAppRunner(directoryService);
        System.setOut(new PrintStream(outputStream));
    }

    /**
     * 正常系のコピー操作のテスト
     */
    @Test
    void testSuccessfulCopyOperation() throws Exception {
        String[] args = {"source", "target", "COPY"};
        runner.run(args);

        verify(directoryService).processDirectory("source", "target", OperationMode.COPY);
        assertTrue(outputStream.toString().contains("Operation completed successfully"));
    }

    /**
     * 正常系の移動操作のテスト
     */
    @Test
    void testSuccessfulMoveOperation() throws Exception {
        String[] args = {"source", "target", "MOVE"};
        runner.run(args);

        verify(directoryService).processDirectory("source", "target", OperationMode.MOVE);
        assertTrue(outputStream.toString().contains("Operation completed successfully"));
    }

    /**
     * 引数が不足している場合のテスト
     */
    @Test
    void testInsufficientArguments() throws Exception {
        String[] args = {"source", "target"};
        runner.run(args);

        verify(directoryService, never()).processDirectory(any(), any(), any());
        String output = outputStream.toString();
        assertTrue(output.contains("Usage:"));
        assertTrue(output.contains("Modes: COPY, MOVE, DIFF"));
    }

    /**
     * 無効な操作モードが指定された場合のテスト
     */
    @Test
    void testInvalidMode() throws Exception {
        String[] args = {"source", "target", "INVALID"};
        runner.run(args);

        verify(directoryService, never()).processDirectory(any(), any(), any());
        assertTrue(outputStream.toString().contains("Invalid mode: INVALID"));
    }

    /**
     * IOExceptionが発生した場合のテスト
     */
    @Test
    void testIOException() throws Exception {
        String[] args = {"source", "target", "COPY"};
        String errorMessage = "Test error message";
        doThrow(new IOException(errorMessage))
            .when(directoryService)
            .processDirectory(any(), any(), any());

        runner.run(args);

        assertTrue(outputStream.toString().contains("Error: " + errorMessage));
    }

    /**
     * DIFFモードの操作のテスト
     */
    @Test
    void testDiffOperation() throws Exception {
        String[] args = {"source", "target", "DIFF"};
        runner.run(args);

        verify(directoryService).processDirectory("source", "target", OperationMode.DIFF);
        assertTrue(outputStream.toString().contains("Operation completed successfully"));
    }

    /**
     * テスト後のクリーンアップ
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
