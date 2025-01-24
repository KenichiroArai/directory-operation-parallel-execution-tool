package kmg.tool.directorytool.domain.service;

import org.springframework.stereotype.Service;

/**
 * ディレクトリのコピー操作を実行するサービスクラス。 <br>
 * <p>
 * {@link AbstractDirectoryService}を継承し、ディレクトリとその内容の再帰的なコピー機能を提供する。
 * </p>
 * <p>
 * 主な特徴：
 * <ul>
 * <li>ディレクトリ構造の完全なコピー
 * <li>既存ファイルの自動上書き
 * <li>並列処理による高速なファイルコピー
 * <li>ディレクトリ階層の自動作成
 * </ul>
 * 使用例：
 *
 * <pre>
 * CopyDirectoryService service = new CopyDirectoryServiceImpl();
 * service.processDirectory("/source", "/target");
 * </pre>
 *
 * @author kmg
 * @version 1.0
 * @see AbstractDirectoryService
 * @see DirectoryService
 */
@Service
public interface CopyDirectoryService extends AbstractDirectoryService {
    // 処理なし
}
