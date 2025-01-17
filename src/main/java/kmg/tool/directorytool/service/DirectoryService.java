package kmg.tool.directorytool.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import kmg.tool.directorytool.model.OperationMode;

/**
 * ディレクトリ操作のファサードとして機能するサービスクラス。 操作モードに応じて適切なサービスクラスに処理を委譲する。 Spring Frameworkのサービスレイヤーとして実装され、DIコンテナによって管理される。
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
public class DirectoryService {

    /** ディレクトリのコピー操作を実行するサービス。 DIコンテナによって注入される。 */
    private final CopyDirectoryService copyService;

    /** ディレクトリの移動操作を実行するサービス。 DIコンテナによって注入される。 */
    private final MoveDirectoryService moveService;

    /** ディレクトリの差分比較操作を実行するサービス。 DIコンテナによって注入される。 */
    private final DiffDirectoryService diffService;

    /**
     * 各種ディレクトリ操作サービスをDIするコンストラクタ。 Spring Bootのコンテナによって自動的にインスタンス化される。
     *
     * @param copySvc
     *                コピー操作を実行するサービス
     * @param moveSvc
     *                移動操作を実行するサービス
     * @param diffSvc
     *                差分比較操作を実行するサービス
     */
    public DirectoryService(final CopyDirectoryService copySvc, final MoveDirectoryService moveSvc,
            final DiffDirectoryService diffSvc) {
        this.copyService = copySvc;
        this.moveService = moveSvc;
        this.diffService = diffSvc;
    }

    /**
     * 指定されたソースディレクトリをターゲットディレクトリに対して処理する。 処理内容は指定された操作モード（COPY、MOVE、またはDIFF）に依存する。 モードに応じて適切なサービスに処理を委譲する。
     *
     * @param srcPath
     *                 ソースディレクトリのパス（存在するディレクトリである必要がある）
     * @param destPath
     *                 ターゲットディレクトリのパス
     * @param mode
     *                 操作モード（COPY、MOVE、またはDIFF）
     * @throws IOException
     *                     ディレクトリの読み書き中にエラーが発生した場合、 またはソースディレクトリが存在しない場合
     * @see OperationMode
     */
    public void processDirectory(final String srcPath, final String destPath, final OperationMode mode)
            throws IOException {
        final AbstractDirectoryService service = switch (mode) {
            case COPY -> this.copyService;
            case MOVE -> this.moveService;
            case DIFF -> this.diffService;
        };
        service.processDirectory(srcPath, destPath);
    }
}
