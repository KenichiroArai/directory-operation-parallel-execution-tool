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

    @Override
    protected void postProcess(Path source, Path destination) throws IOException {
        // コピー操作では後処理は不要
    }
}
