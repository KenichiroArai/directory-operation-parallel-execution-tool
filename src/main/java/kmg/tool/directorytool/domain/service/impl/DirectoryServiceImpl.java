package kmg.tool.directorytool.domain.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kmg.tool.directorytool.domain.service.AbstractDirectoryService;
import kmg.tool.directorytool.domain.service.CopyDirectoryService;
import kmg.tool.directorytool.domain.service.DiffDirectoryService;
import kmg.tool.directorytool.domain.service.DirectoryService;
import kmg.tool.directorytool.domain.service.MoveDirectoryService;
import kmg.tool.directorytool.infrastructure.types.OperationModeTypes;

/**
 * ディレクトリ操作のファサードとして機能するサービスクラス。 <br>
 * <p>
 * 操作モードに応じて適切なサービスクラスに処理を委譲する。<br>
 * Spring Frameworkのサービスレイヤーとして実装され、DIコンテナによって管理される。
 * </p>
 * <p>
 * このクラスは以下の操作をサポートする：
 * <ul>
 * <li>ディレクトリのコピー
 * <li>ディレクトリの移動
 * <li>ディレクトリの差分比較
 * </ul>
 *
 * @author kmg
 * @version 1.0
 * @see CopyDirectoryService
 * @see MoveDirectoryService
 * @see DiffDirectoryService
 */
@Service
public class DirectoryServiceImpl implements DirectoryService {

    /** ディレクトリのコピー操作を実行するサービス */
    @Autowired
    private CopyDirectoryService copyService;

    /** ディレクトリの移動操作を実行するサービス */
    @Autowired
    private MoveDirectoryService moveService;

    /** ディレクトリの差分比較操作を実行するサービス */
    @Autowired
    private DiffDirectoryService diffService;

    /**
     * スレッドプールのサイズを設定します。 全ての実装サービス（コピー、移動、差分比較）のスレッドプールサイズを更新します。
     *
     * @param threadPoolSize
     *                       スレッドプールのサイズ。0以下の場合はデフォルト値（利用可能なプロセッサ数）が使用されます。
     */
    @Override
    public void setThreadPoolSize(final int threadPoolSize) {

        this.copyService.setThreadPoolSize(threadPoolSize);
        this.moveService.setThreadPoolSize(threadPoolSize);
        this.diffService.setThreadPoolSize(threadPoolSize);

    }

    /**
     * 指定されたソースディレクトリをターゲットディレクトリに対して処理する。<br>
     * <p>
     * 処理内容は指定された操作モード（COPY、MOVE、またはDIFF）に依存する。<br>
     * モードに応じて適切なサービスに処理を委譲する。
     * </p>
     *
     * @param srcPath
     *                           ソースディレクトリのパス（存在するディレクトリである必要がある）
     * @param destPath
     *                           ターゲットディレクトリのパス
     * @param operationModeTypes
     *                           操作モードの種類
     * @throws IOException
     *                     ディレクトリの読み書き中にエラーが発生した場合、 またはソースディレクトリが存在しない場合
     */
    @Override
    public void processDirectory(final String srcPath, final String destPath,
            final OperationModeTypes operationModeTypes) throws IOException {

        final AbstractDirectoryService service = switch (operationModeTypes) {

            case NONE ->
                throw new IllegalArgumentException(String.format("Unexpected value: %s", operationModeTypes.getName()));
            case COPY -> this.copyService;
            case MOVE -> this.moveService;
            case DIFF -> this.diffService;

        };
        service.processDirectory(srcPath, destPath);

    }
}
