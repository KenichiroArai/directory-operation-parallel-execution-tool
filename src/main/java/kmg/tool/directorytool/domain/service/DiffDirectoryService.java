package kmg.tool.directorytool.domain.service;

import org.springframework.stereotype.Service;

/**
 * ディレクトリの差分を検出するサービスインタフェース。 <br>
 * <p>
 * {@link AbstractDirectoryService}を継承し、2つのディレクトリ間の差分を検出・報告する機能を提供する。
 * </p>
 * <p>
 * 主な特徴：
 * <ul>
 * <li>ディレクトリ構造の完全な比較
 * <li>ファイル内容の詳細な比較
 * <li>並列処理による高速な差分検出
 * <li>多様な差分タイプの検出と報告
 * </ul>
 * <p>
 * 検出される差分の種類：
 * <ul>
 * <li>ソースディレクトリのみに存在するファイル
 * <li>ターゲットディレクトリのみに存在するファイル
 * <li>ファイル内容の違い
 * <li>ファイルタイプの違い（ファイルvsディレクトリ）
 * <li>ディレクトリ構造の違い
 * </ul>
 * <p>
 * 出力形式：
 *
 * <pre>
 * ソースのみに存在: path/to/file                        - ソースディレクトリのみに存在するファイル
 * ターゲットのみに存在: path/to/file                    - ターゲットディレクトリのみに存在するファイル
 * 差異あり: path/to/file                               - 内容が異なるファイル
 * 差異あり: path/to/file (ファイル vs ディレクトリ)      - タイプが異なるパス
 * ソースディレクトリのみに存在するディレクトリ: path/to/dir  - ソースのみに存在するディレクトリ
 * ターゲットディレクトリのみに存在するディレクトリ: path/to/dir - ターゲットのみに存在するディレクトリ
 * </pre>
 * <p>
 * このサービスはSpring Frameworkのコンポーネントとして実装され、 {@link DirectoryService}クラスによって使用される。
 * スレッドセーフな実装となっており、複数のスレッドから同時にアクセスしても安全に動作する。
 * <p>
 * 使用例：
 *
 * <pre>
 * DiffDirectoryService service = new DiffDirectoryServiceImpl();
 * service.processDirectory("/source/dir", "/target/dir");
 * // 差分が標準出力に表示される
 * </pre>
 *
 * @author kmg
 * @version 1.0
 * @see AbstractDirectoryService
 * @see DirectoryService
 */
@Service
public interface DiffDirectoryService extends AbstractDirectoryService {
    // 処理なし
}
