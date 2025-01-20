package kmg.tool.directorytool.runner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

        /**
         * Logbackのリストアペンダー
         */
        private ListAppender<ILoggingEvent> listAppender;

        /**
         * Loggerインスタンス
         */
        private Logger logger;

        /**
         * テストの前準備
         */
        @BeforeEach
        public void setUp() {

                this.runner = new DirectoryToolAr();
                ReflectionTestUtils.setField(this.runner, "directoryService", this.directoryService);

                // Logbackの設定
                logger = (Logger) LoggerFactory.getLogger(DirectoryToolAr.class);
                listAppender = new ListAppender<>();
                listAppender.start();
                logger.addAppender(listAppender);

        }

        /**
         * 正常系のコピー操作のテスト
         *
         * @throws Exception
         *                   テスト実行中に発生する可能性のある例外
         */
        @Test
        public void testSuccessfulCopyOperation() throws Exception {

                // 準備
                Mockito.when(this.applicationArguments.getNonOptionArgs())
                                .thenReturn(Arrays.asList("COPY", "source", "target"));

                // テスト対象の実行
                this.runner.run(this.applicationArguments);

                // 検証
                Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.COPY);
                List<ILoggingEvent> logsList = listAppender.list;
                Assertions.assertTrue(logsList.stream()
                                .anyMatch(event -> event.getMessage().contains("ディレクトリ操作の処理が終了しました。")));

        }

        /**
         * 正常系の移動操作のテスト
         *
         * @throws Exception
         *                   テスト実行中に発生する可能性のある例外
         */
        @Test
        public void testSuccessfulMoveOperation() throws Exception {

                // 準備
                Mockito.when(this.applicationArguments.getNonOptionArgs())
                                .thenReturn(Arrays.asList("MOVE", "source", "target"));

                // テスト対象の実行
                this.runner.run(this.applicationArguments);

                // 検証
                Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.MOVE);
                List<ILoggingEvent> logsList = listAppender.list;
                Assertions.assertTrue(logsList.stream()
                                .anyMatch(event -> event.getMessage().contains("ディレクトリ操作の処理が終了しました。")));

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
                Mockito.when(this.applicationArguments.getNonOptionArgs())
                                .thenReturn(Arrays.asList("source", "target"));

                // テスト対象の実行
                this.runner.run(this.applicationArguments);

                // 検証
                Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                                ArgumentMatchers.any(), ArgumentMatchers.any());
                List<ILoggingEvent> logsList = listAppender.list;
                Assertions.assertTrue(logsList.stream().anyMatch(event -> event.getMessage().contains("使用方法:")));
                Assertions.assertTrue(logsList.stream()
                                .anyMatch(event -> event.getMessage().contains("モデルの種類: COPY, MOVE, DIFF")));

        }

        /**
         * 無効な操作モードが指定された場合のテスト
         *
         * @throws Exception
         *                   テスト実行中に発生する可能性のある例外
         */
        @Test
        public void testInvalidMode() throws Exception {

                // 準備
                Mockito.when(this.applicationArguments.getNonOptionArgs())
                                .thenReturn(Arrays.asList("INVALID", "source", "target"));

                // テスト対象の実行
                this.runner.run(this.applicationArguments);

                // 検証
                Mockito.verify(this.directoryService, Mockito.never()).processDirectory(ArgumentMatchers.any(),
                                ArgumentMatchers.any(), ArgumentMatchers.any());
                List<ILoggingEvent> logsList = listAppender.list;
                Assertions.assertTrue(logsList.stream()
                                .anyMatch(event -> event.getMessage().contains("無効なモードが選択されています。: [INVALID]")));

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
                Mockito.when(this.applicationArguments.getNonOptionArgs())
                                .thenReturn(Arrays.asList("COPY", "source", "target"));
                final String errorMessage = "Test error message";
                Mockito.doThrow(new IOException(errorMessage)).when(this.directoryService).processDirectory(
                                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

                // テスト対象の実行
                this.runner.run(this.applicationArguments);

                // 検証
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

                // 準備
                Mockito.when(this.applicationArguments.getNonOptionArgs())
                                .thenReturn(Arrays.asList("DIFF", "source", "target"));

                // テスト対象の実行
                this.runner.run(this.applicationArguments);

                // 検証
                Mockito.verify(this.directoryService).processDirectory("source", "target", OperationMode.DIFF);
                List<ILoggingEvent> logsList = listAppender.list;
                Assertions.assertTrue(logsList.stream()
                                .anyMatch(event -> event.getMessage().contains("ディレクトリ操作の処理が終了しました。")));

        }

        /**
         * テスト後のクリーンアップ
         */
        @AfterEach
        public void tearDown() {

                logger.detachAppender(listAppender);

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
