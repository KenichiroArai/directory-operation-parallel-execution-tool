package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Service;

/**
 * ディレクトリの移動操作を実行するサービスクラス。
 */
@Service
public class MoveDirectoryService extends AbstractDirectoryService {
    @Override
    protected void processPath(Path sourcePath, Path targetPath, Path relativePath) throws IOException {
        if (Files.isDirectory(sourcePath)) {
            Files.createDirectories(targetPath);
        } else {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    protected void postProcess(Path source, Path destination) throws IOException {
        // 空になったディレクトリを削除
        try (var stream = Files.walk(source)) {
            stream.sorted((a, b) -> b.toString().length() - a.toString().length())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // 削除に失敗した場合は無視
                        }
                    });
        }
    }
}
