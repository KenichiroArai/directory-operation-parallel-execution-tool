package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Service;

/**
 * ディレクトリのコピー操作を実行するサービスクラス。
 */
@Service
public class CopyDirectoryService extends AbstractDirectoryService {
    /**
     * 個々のファイル/ディレクトリに対してコピー操作を実行する。
     * ソースがディレクトリの場合、ターゲットディレクトリを作成する。
     * ソースがファイルの場合、親ディレクトリを作成し、ファイルをコピーする。
     * 既存のファイルは上書きされる。
     *
     * @param sourcePath コピー元のパス
     * @param targetPath コピー先のパス
     * @param relativePath ソースディレクトリからの相対パス
     * @throws IOException ディレクトリ作成またはファイルコピー中にエラーが発生した場合
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
     * コピー操作後の後処理を実行する。
     * この実装では特に処理は行わない。
     *
     * @param source ソースディレクトリのパス
     * @param destination ターゲットディレクトリのパス
     * @throws IOException 後処理中にエラーが発生した場合
     */
    @Override
    protected void postProcess(Path source, Path destination) throws IOException {
        // コピー操作では後処理は不要
    }
}
