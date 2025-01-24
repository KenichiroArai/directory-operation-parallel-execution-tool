package kmg.tool.directorytool.domain.service;

import java.io.IOException;

import kmg.tool.directorytool.infrastructure.types.OperationModeTypes;

/**
 * ディレクトリ操作のファサードとして機能するサービスインタフェース。 <br>
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
 */
public interface DirectoryService {

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
    void processDirectory(final String srcPath, final String destPath, final OperationModeTypes operationModeTypes)
            throws IOException;
}
