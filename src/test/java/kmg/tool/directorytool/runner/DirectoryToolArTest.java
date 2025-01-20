package kmg.tool.directorytool.runner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.test.util.ReflectionTestUtils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import kmg.tool.directorytool.model.OperationMode;
import kmg.tool.directorytool.service.DirectoryService;

/**
 * DirectoryToolArのテストクラス
 */
@Execution(ExecutionMode.SAME_THREAD)
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

    /** Logbackのリストアペンダー */
    private ListAppender<ILoggingEvent> listAppender;

    /** Loggerインスタンス */
    private Logger logger;

    /**
     * テストの前準備
     */
    @BeforeEach
    public void setUp() {

        this.runner = new DirectoryToolAr();
        ReflectionTestUtils.setField(this.runner, "directoryService", this.directoryService);

        // Logbackの設定
        this.logger = (Logger) LoggerFactory.getLogger(DirectoryToolAr.class);
        this.listAppender = new ListAppender<>();
        this.listAppender.start();
        this.logger.addAppender(this.listAppender);

    }

    /**
     * 正常系のコピー操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testSuccessfulCopyOperation() throws Exception {

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("COPY", "source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証 */
        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.COPY);
        final List<ILoggingEvent> logsList = this.listAppender.list;
        Assertions.assertEquals(true,
                logsList.stream().anyMatch(event -> event.getMessage().contains("ディレクトリ操作の処理が終了しました。")),
                "処理終了メッセージが含まれていること");

    }

    /**
     * 正常系の移動操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testSuccessfulMoveOperation() throws Exception {

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("MOVE", "source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証 */
        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.MOVE);
        final List<ILoggingEvent> logsList = this.listAppender.list;
        Assertions.assertEquals(true,
                logsList.stream().anyMatch(event -> event.getMessage().contains("ディレクトリ操作の処理が終了しました。")),
                "処理終了メッセージが含まれていること");

    }

    /**
     * 引数が不足している場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testInsufficientArguments() throws Exception {

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs()).thenReturn(Arrays.asList("source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証 */
        Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
        final List<ILoggingEvent> logsList = this.listAppender.list;
        Assertions.assertEquals(true, logsList.stream().anyMatch(event -> event.getMessage().contains("使用方法:")),
                "使用方法メッセージが含まれていること");
        Assertions.assertEquals(true,
                logsList.stream().anyMatch(event -> event.getMessage().contains("モデルの種類: COPY, MOVE, DIFF")),
                "モデルの種類メッセージが含まれていること");

    }

    /**
     * 無効な操作モードが指定された場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testInvalidMode() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
                "無効なモードが選択されています。: [INVALID]", "有効なモードの種類: COPY, MOVE, DIFF",
        };
        final String   expectedMsg  = String.join(System.lineSeparator(), expectedMsgs);

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("INVALID", "source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証の準備 */
        Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
        final List<ILoggingEvent> logsList = this.listAppender.list;

        final String actualMsg = logsList.stream().map(ILoggingEvent::getMessage)
                .collect(Collectors.joining(System.lineSeparator()));

        /* 検証 */
        Assertions.assertEquals(expectedMsg, actualMsg, "エラーメッセージが期待通りであること");

    }

    /**
     * IOExceptionが発生した場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testIOException() throws Exception {

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("COPY", "source", "target"));
        final String errorMessage = "Test error message";
        Mockito.doThrow(new IOException(errorMessage)).when(this.directoryService)
                .processDirectory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証 */
        Assertions.assertEquals(1, this.runner.getExitCode());

    }

    /**
     * DIFFモードの操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testDiffOperation() throws Exception {

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("DIFF", "source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証 */
        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.DIFF);
        final List<ILoggingEvent> logsList = this.listAppender.list;
        Assertions.assertEquals(true,
                logsList.stream().anyMatch(event -> event.getMessage().contains("ディレクトリ操作の処理が終了しました。")),
                "処理終了メッセージが含まれていること");

    }

    /**
     * テスト後のクリーンアップ
     */
    @AfterEach
    public void tearDown() {

        this.logger.detachAppender(this.listAppender);

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
