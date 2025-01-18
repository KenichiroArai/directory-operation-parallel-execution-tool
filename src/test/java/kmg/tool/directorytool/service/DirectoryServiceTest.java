package kmg.tool.directorytool.service;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import kmg.tool.directorytool.model.OperationMode;

/**
 * DirectoryServiceのファサードパターンとしての機能をテストするクラス。 各モードで適切なサービスが選択され、処理が委譲されることを確認する。
 */
@ExtendWith(MockitoExtension.class)
class DirectoryServiceTest {

    /** テスト対象のDirectoryServiceインスタンス */
    @Mock
    private DirectoryService directoryService;

    /** コピー処理を行うサービスのモック */
    @Mock
    private CopyDirectoryService copyService;

    /** 移動処理を行うサービスのモック */
    @Mock
    private MoveDirectoryService moveService;

    /** 差分比較を行うサービスのモック */
    @Mock
    private DiffDirectoryService diffService;

    /**
     * テストの前準備を行う。 DirectoryServiceのインスタンスを生成し、必要なモックサービスを注入する。
     */
    @BeforeEach
    void setUp() {
        this.directoryService = new DirectoryService();
        ReflectionTestUtils.setField(this.directoryService, "copyService", this.copyService);
        ReflectionTestUtils.setField(this.directoryService, "moveService", this.moveService);
        ReflectionTestUtils.setField(this.directoryService, "diffService", this.diffService);
    }

    /**
     * COPYモードで適切なサービスが呼び出されることをテスト。
     *
     * @throws IOException
     *                     ディレクトリ処理中にI/Oエラーが発生した場合
     */
    @Test
    void testCopyModeCallsCorrectService() throws IOException {
        final String srcPath  = "source";
        final String destPath = "target";

        this.directoryService.processDirectory(srcPath, destPath, OperationMode.COPY);

        Mockito.verify(this.copyService).processDirectory(srcPath, destPath);
        Mockito.verifyNoInteractions(this.moveService);
        Mockito.verifyNoInteractions(this.diffService);
    }

    /**
     * MOVEモードで適切なサービスが呼び出されることをテスト。
     *
     * @throws IOException
     *                     ディレクトリ処理中にI/Oエラーが発生した場合
     */
    @Test
    void testMoveModeCallsCorrectService() throws IOException {
        final String srcPath  = "source";
        final String destPath = "target";

        this.directoryService.processDirectory(srcPath, destPath, OperationMode.MOVE);

        Mockito.verify(this.moveService).processDirectory(srcPath, destPath);
        Mockito.verifyNoInteractions(this.copyService);
        Mockito.verifyNoInteractions(this.diffService);
    }

    /**
     * DIFFモードで適切なサービスが呼び出されることをテスト。
     *
     * @throws IOException
     *                     ディレクトリ処理中にI/Oエラーが発生した場合
     */
    @Test
    void testDiffModeCallsCorrectService() throws IOException {
        final String srcPath  = "source";
        final String destPath = "target";

        this.directoryService.processDirectory(srcPath, destPath, OperationMode.DIFF);

        Mockito.verify(this.diffService).processDirectory(srcPath, destPath);
        Mockito.verifyNoInteractions(this.copyService);
        Mockito.verifyNoInteractions(this.moveService);
    }

    /**
     * サービス呼び出し時の例外が適切に伝播することをテスト。
     *
     * @throws IOException
     *                     ディレクトリ処理中にI/Oエラーが発生した場合
     */
    @Test
    void testExceptionPropagation() throws IOException {
        final String      srcPath           = "source";
        final String      destPath          = "target";
        final IOException expectedException = new IOException("Test error");

        Mockito.doThrow(expectedException).when(this.copyService).processDirectory(srcPath, destPath);

        try {
            this.directoryService.processDirectory(srcPath, destPath, OperationMode.COPY);
        } catch (final IOException e) {
            org.junit.jupiter.api.Assertions.assertEquals(expectedException, e);
        }
    }
}
