package kmg.tool.directorytool.domain.service.impl;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import kmg.tool.directorytool.domain.service.CopyDirectoryService;
import kmg.tool.directorytool.domain.service.DiffDirectoryService;
import kmg.tool.directorytool.domain.service.DirectoryService;
import kmg.tool.directorytool.domain.service.MoveDirectoryService;
import kmg.tool.directorytool.domain.service.impl.DirectoryServiceImpl;
import kmg.tool.directorytool.infrastructure.types.OperationModeTypes;

/**
 * DirectoryServiceのファサードパターンの機能をテストするクラス。 <br>
 * <p>
 * 各操作モードで適切なサービスが選択され、処理が正しく委譲されることを確認します。
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class DirectoryServiceImplTest {

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
     * テストの前準備を行います。 DirectoryServiceのインスタンスを生成し、必要なモックサービスを注入します。
     */
    @BeforeEach
    public void setUp() {

        this.directoryService = new DirectoryServiceImpl();
        ReflectionTestUtils.setField(this.directoryService, "copyService", this.copyService);
        ReflectionTestUtils.setField(this.directoryService, "moveService", this.moveService);
        ReflectionTestUtils.setField(this.directoryService, "diffService", this.diffService);

    }

    /**
     * COPYモードで適切なサービスが呼び出されることを検証します。
     *
     * @throws IOException
     *                     ディレクトリ処理中にI/Oエラーが発生した場合
     */
    @Test
    public void testCopyModeCallsCorrectService() throws IOException {

        /* 期待値の定義 */
        final String expectedSrcPath  = "source";
        final String expectedDestPath = "target";

        /* 準備 */

        /* テスト対象の実行 */
        this.directoryService.processDirectory(expectedSrcPath, expectedDestPath, OperationModeTypes.COPY);

        /* 検証の準備 */

        /* 検証の実施 */
        Mockito.verify(this.copyService).processDirectory(expectedSrcPath, expectedDestPath);
        Mockito.verifyNoInteractions(this.moveService);
        Mockito.verifyNoInteractions(this.diffService);

    }

    /**
     * MOVEモードで適切なサービスが呼び出されることを検証します。
     *
     * @throws IOException
     *                     ディレクトリ処理中にI/Oエラーが発生した場合
     */
    @Test
    public void testMoveModeCallsCorrectService() throws IOException {

        /* 期待値の定義 */
        final String expectedSrcPath  = "source";
        final String expectedDestPath = "target";

        /* 準備 */

        /* テスト対象の実行 */
        this.directoryService.processDirectory(expectedSrcPath, expectedDestPath, OperationModeTypes.MOVE);

        /* 検証の準備 */

        /* 検証の実施 */
        Mockito.verify(this.moveService).processDirectory(expectedSrcPath, expectedDestPath);
        Mockito.verifyNoInteractions(this.copyService);
        Mockito.verifyNoInteractions(this.diffService);

    }

    /**
     * DIFFモードで適切なサービスが呼び出されることを検証します。
     *
     * @throws IOException
     *                     ディレクトリ処理中にI/Oエラーが発生した場合
     */
    @Test
    public void testDiffModeCallsCorrectService() throws IOException {

        /* 期待値の定義 */
        final String expectedSrcPath  = "source";
        final String expectedDestPath = "target";

        /* 準備 */

        /* テスト対象の実行 */
        this.directoryService.processDirectory(expectedSrcPath, expectedDestPath, OperationModeTypes.DIFF);

        /* 検証の準備 */

        /* 検証の実施 */
        Mockito.verify(this.diffService).processDirectory(expectedSrcPath, expectedDestPath);
        Mockito.verifyNoInteractions(this.copyService);
        Mockito.verifyNoInteractions(this.moveService);

    }

    /**
     * サービス呼び出し時の例外が適切に伝播することを検証します。
     *
     * @throws IOException
     *                     ディレクトリ処理中にI/Oエラーが発生した場合
     */
    @Test
    public void testExceptionPropagation() throws IOException {

        /* 期待値の定義 */
        final String      expectedSrcPath   = "source";
        final String      expectedDestPath  = "target";
        final IOException expectedException = new IOException("Test error");

        /* 準備 */
        Mockito.doThrow(expectedException).when(this.copyService).processDirectory(expectedSrcPath, expectedDestPath);

        /* テスト対象の実行 */
        try {

            this.directoryService.processDirectory(expectedSrcPath, expectedDestPath, OperationModeTypes.COPY);

        } catch (final IOException actualException) {
            /* 検証の準備 */

            /* 検証の実施 */
            Assertions.assertEquals(expectedException, actualException, "期待した例外が発生すること");

        }

    }

    /**
     * NONEモードで適切な例外が発生することを検証します。
     */
    @Test
    public void testNoneModeThrowsException() {

        /* 期待値の定義 */
        final String expectedSrcPath  = "source";
        final String expectedDestPath = "target";
        final String expectedMessage  = "Unexpected value: NONE";

        /* テスト対象の実行と検証 */
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> this.directoryService.processDirectory(expectedSrcPath, expectedDestPath,
                        OperationModeTypes.NONE));

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "期待したエラーメッセージが返されること");

    }
}
