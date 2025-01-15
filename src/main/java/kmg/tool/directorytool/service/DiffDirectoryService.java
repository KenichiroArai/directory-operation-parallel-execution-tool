package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;

/**
 * ディレクトリの差分を検出するサービスクラス。
 */
@Service
public class DiffDirectoryService extends AbstractDirectoryService {
    @Override
    protected void processPath(Path sourcePath, Path targetPath, Path relativePath) throws IOException {
        if (Files.isDirectory(sourcePath)) {
            if (!Files.exists(targetPath)) {
                System.out.println("Directory only in source: " + relativePath);
            }
        } else {
            if (!Files.exists(targetPath)) {
                System.out.println("Only in source: " + relativePath);
            } else if (!compareFiles(sourcePath, targetPath)) {
                System.out.println("Different: " + relativePath);
            }
        }
    }

    @Override
    protected void postProcess(Path source, Path destination) throws IOException {
        // ターゲットディレクトリを走査してソースにないファイルを検出
        try (var stream = Files.walk(destination)) {
            stream.forEach(path -> {
                Path relativePath = destination.relativize(path);
                Path sourcePath = source.resolve(relativePath);
                if (!Files.exists(sourcePath)) {
                    if (Files.isDirectory(path)) {
                        System.out.println("Directory only in destination: " + relativePath);
                    } else {
                        System.out.println("Only in destination: " + relativePath);
                    }
                }
            });
        }
    }
}
