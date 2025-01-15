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
        if (!sourcePath.equals(Path.of(sourcePath.getRoot().toString()))) {  // ルートディレクトリは処理しない
            if (Files.isDirectory(sourcePath)) {
                if (!Files.exists(targetPath)) {
                    System.out.println("Directory only in source: " + relativePath);
                } else if (!Files.isDirectory(targetPath)) {
                    System.out.println("Different: " + relativePath + " (directory vs file)");
                }
            } else {
                if (!Files.exists(targetPath)) {
                    System.out.println("Only in source: " + relativePath);
                } else if (!Files.isRegularFile(targetPath)) {
                    System.out.println("Different: " + relativePath + " (file vs directory)");
                } else if (!compareFiles(sourcePath, targetPath)) {
                    System.out.println("Different: " + relativePath);
                }
            }
        }
    }

    @Override
    protected void postProcess(Path source, Path destination) throws IOException {
        // ターゲットディレクトリを走査してソースにないファイルを検出
        if (Files.exists(destination)) {
            try (var stream = Files.walk(destination)) {
                stream.forEach(path -> processDestinationPath(source, destination, path));
            }
        }
    }

    private void processDestinationPath(Path source, Path destination, Path path) {
        if (!path.equals(destination)) {  // ルートディレクトリは除外
            Path relativePath = destination.relativize(path);
            Path sourcePath = source.resolve(relativePath);
            if (!Files.exists(sourcePath)) {
                if (Files.isDirectory(path)) {
                    System.out.println("Directory only in destination: " + relativePath);
                } else {
                    System.out.println("Only in destination: " + relativePath);
                }
            }
        }
    }

    @Override
    public void processDirectory(String srcPath, String destPath) throws IOException {
        Path source = Path.of(srcPath);
        Path destination = Path.of(destPath);

        validatePaths(source, destination);

        // ソースディレクトリの処理
        try (var stream = Files.walk(source)) {
            stream.forEach(path -> {
                try {
                    Path relativePath = source.relativize(path);
                    Path targetPath = destination.resolve(relativePath);
                    processPath(path, targetPath, relativePath);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to process file: " + path, e);
                }
            });
        }

        // ターゲットディレクトリの処理
        postProcess(source, destination);
    }
}
