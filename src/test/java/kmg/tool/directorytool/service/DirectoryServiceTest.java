package kmg.tool.directorytool.service;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import kmg.tool.directorytool.model.OperationMode;

/**
 * DirectoryServiceのファサードパターンとしての機能をテストするクラス。 各モードで適切なサービスが選択され、処理が委譲されることを確認する。
 */
class DirectoryServiceTest {

    /** テスト対象のDirectoryServiceインスタンス */
    private DirectoryService     directoryService;
    /** コピー処理を行うサービスのモック */
    private CopyDirectoryService copyService;
    /** 移動処理を行うサービスのモック */
    private MoveDirectoryService moveService;
    /** 差分比較を行うサービスのモック */
    private DiffDirectoryService diffService;

    /**
     * 各テストの前に実行される初期化メソッド。 各サービスのモックを作成し、DirectoryServiceに注入する。
     */
    @BeforeEach
    void setUp() {
        // 各サービスのモックを作成
        this.copyService = Mockito.mock(CopyDirectoryService.class);
        this.moveService = Mockito.mock(MoveDirectoryService.class);
        this.diffService = Mockito.mock(DiffDirectoryService.class);

        // DirectoryServiceにモックを注入
        this.directoryService = new DirectoryService(this.copyService, this.moveService, this.diffService);
    }

    /**
     * COPYモードで適切なサービスが呼び出されることをテスト。
     *
     * @throws IOException
     *                     ディレクトリ処理中にI/Oエラーが発生した場合
     */
    @Test
    void testCopyModeCallsCorrectService() throws IOException {
        String srcPath  = "source";
        String destPath = "target";

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
        String srcPath  = "source";
        String destPath = "target";

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
        String srcPath  = "source";
        String destPath = "target";

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
        String      srcPath           = "source";
        String      destPath          = "target";
        IOException expectedException = new IOException("Test error");

        Mockito.doThrow(expectedException).when(this.copyService).processDirectory(srcPath, destPath);

        try {
            this.directoryService.processDirectory(srcPath, destPath, OperationMode.COPY);
        } catch (IOException e) {
            org.junit.jupiter.api.Assertions.assertEquals(expectedException, e);
        }
    }
}
