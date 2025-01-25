package kmg.tool.directorytool.domain.service;

import java.io.IOException;

/**
 * ディレクトリ操作の基本機能を提供するインタフェース。 <br>
 * <p>
 * このクラスは、ファイルシステム操作の共通機能を実装し、具体的なディレクトリ操作のベースとなる機能を提供する。
 * </p>
 * <p>
 * 主な特徴：
 * <ul>
 * <li>マルチスレッドによる並列処理機能
 * <li>ディレクトリ走査の共通実装
 * <li>ファイル操作の基本的な検証機能
 * <li>ファイル比較の共通ユーティリティ
 * </ul>
 * <p>
 *
 * @author kmg
 * @version 1.0
 */
public interface AbstractDirectoryService {

    /**
     * ディレクトリの処理を実行する。
     *
     * @param srcPath
     *                 ソースディレクトリのパス
     * @param destPath
     *                 ターゲットディレクトリのパス
     * @throws IOException
     *                     ソースディレクトリが存在しない場合、ターゲットディレクトリの作成に失敗した場合、 またはファイル処理中にエラーが発生した場合。
     */
    void processDirectory(final String srcPath, final String destPath) throws IOException;

    /**
     * スレッドプールのサイズを設定します。
     *
     * @param threadPoolSize
     *                       スレッドプールのサイズ。0以下の場合はデフォルト値が使用されます。
     */
    void setThreadPoolSize(int threadPoolSize);
}
