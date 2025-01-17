package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Service;

/**
 * ディレクトリのコピー操作を実行するサービスクラス。 {@link AbstractDirectoryService}を継承し、ディレクトリとその内容の再帰的なコピー機能を提供する。
 * <p>
 * 主な特徴：
 * <ul>
 * <li>ディレクトリ構造の完全なコピー
 * <li>既存ファイルの自動上書き
 * <li>並列処理による高速なファイルコピー
 * <li>ディレクトリ階層の自動作成
 * </ul>
 * <p>
 * このサービスはSpring Frameworkのコンポーネントとして実装され、 {@link DirectoryService}クラスによって使用される。
 * スレッドセーフな実装となっており、複数のスレッドから同時にアクセスしても安全に動作する。
 * <p>
 * 使用例：
 * 
 * <pre>
 * CopyDirectoryService service = new CopyDirectoryService();
 * service.processDirectory("/source/dir", "/target/dir");
 * </pre>
 *
 * @author kmg
 * @version 1.0
 * @see AbstractDirectoryService
 * @see DirectoryService
 */
@Service
public class CopyDirectoryService extends AbstractDirectoryService {
    /**
     * 個々のファイル/ディレクトリに対してコピー操作を実行する。 ソースがディレクトリの場合、ターゲットディレクトリを作成する。 ソースがファイルの場合、親ディレクトリを作成し、ファイルをコピーする。 既存のファイルは上書きされる。
     *
     * @param sourcePath
     *                     コピー元のパス
     * @param targetPath
     *                     コピー先のパス
     * @param relativePath
     *                     ソースディレクトリからの相対パス
     * @throws IOException
     *                     ディレクトリ作成またはファイルコピー中にエラーが発生した場合
     */
    @Override
    protected void processPath(Path sourcePath, Path targetPath, Path relativePath) throws IOException {
        if (Files.isDirectory(sourcePath)) {
            Files.createDirectories(targetPath);
        } else {
            // ターゲットディレクトリが存在することを保証
            Files.createDirectories(targetPath.getParent());
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * コピー操作後の後処理を実行する。 この実装では特に処理は行わないため、例外はスローされません。
     *
     * @param source
     *                    ソースディレクトリのパス
     * @param destination
     *                    ターゲットディレクトリのパス
     */
    @Override
    protected void postProcess(Path source, Path destination) throws IOException {
        // コピー操作では後処理は不要
    }
}
