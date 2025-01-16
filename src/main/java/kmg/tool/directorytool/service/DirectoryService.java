package kmg.tool.directorytool.service;

import java.io.IOException;
import org.springframework.stereotype.Service;
import kmg.tool.directorytool.model.OperationMode;

/**
 * ディレクトリ操作のファサードとして機能するサービスクラス。
 * 操作モードに応じて適切なサービスクラスに処理を委譲する。
 * Spring Frameworkのサービスレイヤーとして実装され、DIコンテナによって管理される。
 *
 * <p>このクラスは以下の操作をサポートする：
 * <ul>
 *   <li>ディレクトリのコピー
 *   <li>ディレクトリの移動
 *   <li>ディレクトリの差分比較
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

    private final CopyDirectoryService copyService;
    private final MoveDirectoryService moveService;
    private final DiffDirectoryService diffService;

    /**
     * 各種ディレクトリ操作サービスをDIするコンストラクタ。
     * Spring Bootのコンテナによって自動的にインスタンス化される。
     *
     * @param copyService コピー操作を実行するサービス
     * @param moveService 移動操作を実行するサービス
     * @param diffService 差分比較操作を実行するサービス
     */
    public DirectoryService(CopyDirectoryService copyService, MoveDirectoryService moveService,
            DiffDirectoryService diffService) {
        this.copyService = copyService;
        this.moveService = moveService;
        this.diffService = diffService;
    }

    /**
     * 指定されたソースディレクトリをターゲットディレクトリに対して処理する。
     * 処理内容は指定された操作モード（COPY、MOVE、またはDIFF）に依存する。
     * モードに応じて適切なサービスに処理を委譲する。
     *
     * @param srcPath ソースディレクトリのパス（存在するディレクトリである必要がある）
     * @param destPath ターゲットディレクトリのパス
     * @param mode 操作モード（COPY、MOVE、またはDIFF）
     * @throws IOException ディレクトリの読み書き中にエラーが発生した場合、
     *                     またはソースディレクトリが存在しない場合
     * @see OperationMode
     */
    public void processDirectory(String srcPath, String destPath, OperationMode mode)
            throws IOException {
        AbstractDirectoryService service = switch (mode) {
            case COPY -> copyService;
            case MOVE -> moveService;
            case DIFF -> diffService;
        };
        service.processDirectory(srcPath, destPath);
    }
}
