package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
 * CopyDirectoryService service = new CopyDirectoryService();
 * service.processDirectory("/source", "/target");
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
     * 個々のファイル/ディレクトリに対してコピー操作を実行する。 <br>
     * <p>
     * ソースがディレクトリの場合、ターゲットディレクトリを作成する。 <br>
     * ソースがファイルの場合、親ディレクトリを作成し、ファイルをコピーする。<br>
     * 既存のファイルは上書きされる。
     * </p>
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
    protected void processPath(final Path sourcePath, final Path targetPath, final Path relativePath)
            throws IOException {

        if (Files.isDirectory(sourcePath)) {
            Files.createDirectories(targetPath);
            return;
        }

        // ターゲットディレクトリが存在することを保証
        Files.createDirectories(targetPath.getParent());
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * コピー操作後の後処理を実行する。 <br>
     * <p>
     * この実装では特に処理は行わないため、例外はスローされません。
     * </p>
     *
     * @param source
     *                    ソースディレクトリのパス
     * @param destination
     *                    ターゲットディレクトリのパス
     */
    @Override
    protected void postProcess(final Path source, final Path destination) throws IOException {
        // コピー操作では後処理は不要
    }
}
