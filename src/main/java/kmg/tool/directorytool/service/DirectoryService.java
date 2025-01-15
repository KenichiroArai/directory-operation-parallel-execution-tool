package kmg.tool.directorytool.service;

import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import kmg.tool.directorytool.model.OperationMode;

/**
 * ディレクトリ操作のファサードとして機能するサービスクラス。
 * 操作モードに応じて適切なサービスクラスに処理を委譲する。
 */
@Service
public class DirectoryService {

    private final CopyDirectoryService copyService;
    private final MoveDirectoryService moveService;
    private final DiffDirectoryService diffService;

    @Autowired
    public DirectoryService(
            CopyDirectoryService copyService,
            MoveDirectoryService moveService,
            DiffDirectoryService diffService) {
        this.copyService = copyService;
        this.moveService = moveService;
        this.diffService = diffService;
    }

    /**
     * 指定されたソースディレクトリをターゲットディレクトリに対して処理する。
     * 処理内容は指定された操作モード（COPY、MOVE、またはDIFF）に依存する。
     *
     * @param srcPath ソースディレクトリのパス
     * @param destPath ターゲットディレクトリのパス
     * @param mode 操作モード（COPY、MOVE、またはDIFF）
     * @throws IOException ディレクトリ操作中にエラーが発生した場合
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
