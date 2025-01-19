package kmg.tool.directorytool.runner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.ApplicationArguments;
import org.springframework.test.util.ReflectionTestUtils;

import kmg.tool.directorytool.model.OperationMode;
import kmg.tool.directorytool.service.DirectoryService;

/**
 * DirectoryToolArのテストクラス
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DirectoryToolArTest implements AutoCloseable {

    /** テスト対象のDirectoryServiceモック */
    @Mock
    private DirectoryService directoryService;

    /** テスト対象のApplicationArgumentsモック */
    @Mock
    private ApplicationArguments applicationArguments;

    /** テスト対象のDirectoryToolArインスタンス */
    private DirectoryToolAr runner;

    /** テスト用の出力ストリーム */
    private final ByteArrayOutputStream outputStream;

    /** 標準出力の元のPrintStream */
    private final PrintStream originalOut = System.out;

    /**
     * テスト用の出力ストリームを初期化するコンストラクタ
     */
    public DirectoryToolArTest() {

        this.outputStream = new ByteArrayOutputStream();

    }

    /**
     * テストの前準備
     */
    @BeforeEach
    public void setUp() {

        this.runner = new DirectoryToolAr();
        ReflectionTestUtils.setField(this.runner, "directoryService", this.directoryService);
        System.setOut(new PrintStream(this.outputStream));

    }

    /**
     * 正常系のコピー操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testSuccessfulCopyOperation() throws Exception {

        // 期待値の定義
        /* 期待値の定義 */
        final String expected = String.format("ディレクトリ操作の処理が終了しました。%s", System.lineSeparator());

        // 準備
        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("COPY", "source", "target"));

        // テスト対象の実行
        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        // 検証の準備
        /* 検証の準備 */
        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.COPY);
        final String actual = this.outputStream.toString();

        // 検証の実施
        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "コピー操作の結果が期待通りであること");

    }

    /**
     * 正常系の移動操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testSuccessfulMoveOperation() throws Exception {

        // 期待値の定義
        /* 期待値の定義 */
        final String expected = String.format("ディレクトリ操作の処理が終了しました。%s", System.lineSeparator());

        // 準備
        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("MOVE", "source", "target"));

        // テスト対象の実行
        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        // 検証の準備
        /* 検証の準備 */
        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.MOVE);
        final String actual = this.outputStream.toString();

        // 検証の実施
        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "移動操作の結果が期待通りであること");

    }

    /**
     * 引数が不足している場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testInsufficientArguments() throws Exception {

        // 準備
        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs()).thenReturn(Arrays.asList("source", "target"));

        // テスト対象の実行
        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        // 検証の実施
        /* 検証の実施 */
        Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
        final String output = this.outputStream.toString();
        Assertions.assertTrue(output.contains("使用方法:"), "使用方法が表示されること");
        Assertions.assertTrue(output.contains("モデルの種類: COPY, MOVE, DIFF"), "モデルの種類が表示されること");

    }

    /**
     * 無効な操作モードが指定された場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testInvalidMode() throws Exception {

        // 期待値の定義
        /* 期待値の定義 */
        final String expected = String.format("無効なモードが選択されています。: [INVALID]%s有効なモードの種類: COPY, MOVE, DIFF%s",
                System.lineSeparator(), System.lineSeparator());

        // 準備
        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("INVALID", "source", "target"));

        // テスト対象の実行
        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        // 検証の準備
        /* 検証の準備 */
        Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
        final String actual = this.outputStream.toString();

        // 検証の実施
        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "無効なモードのエラーメッセージが期待通りであること");

    }

    /**
     * IOExceptionが発生した場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testIOException() throws Exception {

        // 準備
        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("COPY", "source", "target"));
        final String errorMessage = "Test error message";
        Mockito.doThrow(new IOException(errorMessage)).when(this.directoryService)
                .processDirectory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

        // テスト対象の実行
        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        // 検証の実施
        /* 検証の実施 */
        Assertions.assertEquals(1, this.runner.getExitCode(), "IOException発生時の終了コードが期待通りであること");

    }

    /**
     * DIFFモードの操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testDiffOperation() throws Exception {

        // 期待値の定義
        /* 期待値の定義 */
        final String expected = String.format("ディレクトリ操作の処理が終了しました。%s", System.lineSeparator());

        // 準備
        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("DIFF", "source", "target"));

        // テスト対象の実行
        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        // 検証の準備
        /* 検証の準備 */
        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.DIFF);
        final String actual = this.outputStream.toString();

        // 検証の実施
        /* 検証の実施 */
        Assertions.assertEquals(expected, actual, "DIFF操作の結果が期待通りであること");

    }

    /**
     * テスト後のクリーンアップ
     *
     * @throws IOException
     *                     クローズ処理中に発生する可能性のある例外
     */
    @AfterEach
    public void tearDown() throws IOException {

        System.setOut(this.originalOut);

    }

    /**
     * テスト終了時のリソースクリーンアップ
     *
     * @throws IOException
     *                     クローズ処理中に発生する可能性のある例外
     */
    @Override
    public void close() throws IOException {

        this.outputStream.close();

    }
}
