package kmg.tool.directorytool.service;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import kmg.tool.directorytool.model.OperationMode;

/**
 * DirectoryServiceのファサードパターンとしての機能をテストするクラス。
 * 各モードで適切なサービスが選択され、処理が委譲されることを確認する。
 */
class DirectoryServiceTest {

    private DirectoryService directoryService;
    private CopyDirectoryService copyService;
    private MoveDirectoryService moveService;
    private DiffDirectoryService diffService;

    @BeforeEach
    void setUp() {
        // 各サービスのモックを作成
        copyService = Mockito.mock(CopyDirectoryService.class);
        moveService = Mockito.mock(MoveDirectoryService.class);
        diffService = Mockito.mock(DiffDirectoryService.class);

        // DirectoryServiceにモックを注入
        directoryService = new DirectoryService(copyService, moveService, diffService);
    }

    /**
     * COPYモードで適切なサービスが呼び出されることをテスト
     */
    @Test
    void testCopyModeCallsCorrectService() throws IOException {
        String srcPath = "source";
        String destPath = "target";

        directoryService.processDirectory(srcPath, destPath, OperationMode.COPY);

        Mockito.verify(copyService).processDirectory(srcPath, destPath);
        Mockito.verifyNoInteractions(moveService);
        Mockito.verifyNoInteractions(diffService);
    }

    /**
     * MOVEモードで適切なサービスが呼び出されることをテスト
     */
    @Test
    void testMoveModeCallsCorrectService() throws IOException {
        String srcPath = "source";
        String destPath = "target";

        directoryService.processDirectory(srcPath, destPath, OperationMode.MOVE);

        Mockito.verify(moveService).processDirectory(srcPath, destPath);
        Mockito.verifyNoInteractions(copyService);
        Mockito.verifyNoInteractions(diffService);
    }

    /**
     * DIFFモードで適切なサービスが呼び出されることをテスト
     */
    @Test
    void testDiffModeCallsCorrectService() throws IOException {
        String srcPath = "source";
        String destPath = "target";

        directoryService.processDirectory(srcPath, destPath, OperationMode.DIFF);

        Mockito.verify(diffService).processDirectory(srcPath, destPath);
        Mockito.verifyNoInteractions(copyService);
        Mockito.verifyNoInteractions(moveService);
    }

    /**
     * サービス呼び出し時の例外が適切に伝播することをテスト
     */
    @Test
    void testExceptionPropagation() throws IOException {
        String srcPath = "source";
        String destPath = "target";
        IOException expectedException = new IOException("Test error");

        Mockito.doThrow(expectedException)
                .when(copyService)
                .processDirectory(srcPath, destPath);

        try {
            directoryService.processDirectory(srcPath, destPath, OperationMode.COPY);
        } catch (IOException e) {
            org.junit.jupiter.api.Assertions.assertEquals(expectedException, e);
        }
    }
}
