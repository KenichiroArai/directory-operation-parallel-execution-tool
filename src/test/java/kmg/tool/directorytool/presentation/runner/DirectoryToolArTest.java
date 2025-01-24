package kmg.tool.directorytool.presentation.runner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.test.util.ReflectionTestUtils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import kmg.tool.directorytool.domain.service.DirectoryService;
import kmg.tool.directorytool.infrastructure.types.ExitCodeTypes;
import kmg.tool.directorytool.infrastructure.types.OperationModeTypes;

/**
 * DirectoryToolArのテストクラス
 */
@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(MockitoExtension.class)
public class DirectoryToolArTest {

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
     * テスト後のクリーンアップ
     */
    @AfterEach
    public void tearDown() {

        this.logger.detachAppender(this.listAppender);

    }

    /**
     * 正常系のコピー操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testSuccessfulCopyOperation() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
                "ディレクトリ操作の処理が終了しました。"
        };

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("COPY", "source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証の準備 */
        final List<ILoggingEvent> logsList   = this.listAppender.list;
        final String[]            actualMsgs = logsList.stream().map(ILoggingEvent::getMessage).toArray(String[]::new);

        /* 検証 */
        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationModeTypes.COPY);

        // ログのチェック
        for (int i = 0; i < expectedMsgs.length; i++) {

            Assertions.assertEquals(expectedMsgs[i], actualMsgs[i], String.format("メッセージが一致しません: %s", expectedMsgs[i]));

        }

    }

    /**
     * 正常系の移動操作のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testSuccessfulMoveOperation() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
                "ディレクトリ操作の処理が終了しました。"
        };

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("MOVE", "source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証の準備 */
        final List<ILoggingEvent> logsList   = this.listAppender.list;
        final String[]            actualMsgs = logsList.stream().map(ILoggingEvent::getMessage).toArray(String[]::new);

        /* 検証 */
        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationModeTypes.MOVE);

        // ログのチェック
        for (int i = 0; i < expectedMsgs.length; i++) {

            Assertions.assertEquals(expectedMsgs[i], actualMsgs[i], String.format("メッセージが一致しません: %s", expectedMsgs[i]));

        }

    }

    /**
     * 引数が不足している場合のテスト
     *
     * @throws Exception
     *                   テスト実行中に発生する可能性のある例外
     */
    @Test
    public void testInsufficientArguments() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
                "使用方法: <mode> <src> <dest>"
        };

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs()).thenReturn(Arrays.asList("source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証の準備 */
        final List<ILoggingEvent> logsList   = this.listAppender.list;
        final String[]            actualMsgs = logsList.stream()
                .flatMap(event -> Arrays.stream(event.getMessage().split(System.lineSeparator())))
                .toArray(String[]::new);

        /* 検証 */
        // 引数のチェック
        Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());

        // ログのチェック
        for (int i = 0; i < expectedMsgs.length; i++) {

            Assertions.assertEquals(expectedMsgs[i], actualMsgs[i], String.format("メッセージが一致しません: %s", expectedMsgs[i]));

        }

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
                "無効なモードが選択されています。: [INVALID]", "有効なモードの種類: COPY, MOVE, DIFF"
        };

        /* 準備 */
        this.listAppender.list.clear();
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("INVALID", "source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証の準備 */
        Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
        final List<ILoggingEvent> logsList   = this.listAppender.list;
        final String[]            actualMsgs = logsList.stream()
                .flatMap(event -> Arrays.stream(event.getMessage().split(System.lineSeparator())))
                .toArray(String[]::new);

        /* 検証 */
        // ログのチェック
        for (int i = 0; i < expectedMsgs.length; i++) {

            Assertions.assertEquals(expectedMsgs[i], actualMsgs[i], String.format("メッセージが一致しません: %s", expectedMsgs[i]));

        }

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

        /* 期待値の定義 */
        final String[] expectedMsgs = {
                "ディレクトリ操作の処理が終了しました。"
        };

        /* 準備 */
        Mockito.when(this.applicationArguments.getNonOptionArgs())
                .thenReturn(Arrays.asList("DIFF", "source", "target"));

        /* テスト対象の実行 */
        this.runner.run(this.applicationArguments);

        /* 検証の準備 */
        final List<ILoggingEvent> logsList   = this.listAppender.list;
        final String[]            actualMsgs = logsList.stream().map(ILoggingEvent::getMessage).toArray(String[]::new);

        /* 検証 */
        Mockito.verify(this.directoryService).processDirectory("source", "target", OperationModeTypes.DIFF);

        // ログのチェック
        for (int i = 0; i < expectedMsgs.length; i++) {

            Assertions.assertEquals(expectedMsgs[i], actualMsgs[i], String.format("メッセージが一致しません: %s", expectedMsgs[i]));

        }

    }

    /**
     * 通常のgetExitCodeメソッドのテスト
     */
    @Test
    public void testGetExitCode() {

        /* 準備 */
        ReflectionTestUtils.setField(this.runner, "exitCode", ExitCodeTypes.ARGUMENT_ERROR);

        /* テスト対象の実行 */
        final int result = this.runner.getExitCode();

        /* 検証 */
        Assertions.assertEquals(1, result, "設定された終了コードが正しく返されること");

    }

    /**
     * 例外を受け取るgetExitCodeメソッドのテスト
     */
    @Test
    public void testGetExitCodeWithException() {

        /* 準備 */
        final Exception testException = new RuntimeException("テスト例外");

        /* テスト対象の実行 */
        final int result = this.runner.getExitCode(testException);

        /* 検証 */
        Assertions.assertEquals(3, result, "例外発生時の終了コードが3であること");

    }
}
