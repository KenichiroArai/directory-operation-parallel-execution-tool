package kmg.tool.directorytool.domain.service;

import org.springframework.stereotype.Service;

/**
 * ディレクトリの移動操作を実行するサービスインタフェース。<br>
 * <p>
 * {@link AbstractDirectoryService}を継承し、ディレクトリとその内容の再帰的な移動機能を提供する。
 * </p>
 * <p>
 * 主な特徴：
 * <ul>
 * <li>ディレクトリ構造の完全な移動
 * <li>既存ファイルの自動上書き
 * <li>並列処理による高速なファイル移動
 * <li>ディレクトリ階層の自動作成
 * <li>移動後のソースディレクトリの自動クリーンアップ
 * </ul>
 * <p>
 * このサービスはSpring Frameworkのコンポーネントとして実装され、 {@link DirectoryService}クラスによって使用される。
 * スレッドセーフな実装となっており、複数のスレッドから同時にアクセスしても安全に動作する。
 * <p>
 * 移動処理の特徴：
 * <ul>
 * <li>ファイルの移動は原子的に行われる（可能な場合）
 * <li>ソースディレクトリは移動完了後に自動的に削除される
 * <li>空のディレクトリは深い階層から順に削除される
 * </ul>
 * <p>
 * 使用例：
 *
 * <pre>
 * MoveDirectoryService service = new MoveDirectoryServiceImpl();
 * service.processDirectory("/source/dir", "/target/dir");
 * // 移動完了後、/source/dirは自動的に削除される
 * </pre>
 *
 * @author kmg
 * @version 1.0
 * @see AbstractDirectoryService
 * @see DirectoryService
 */
@Service
public interface MoveDirectoryService extends AbstractDirectoryService {
    // 処理なし
}
