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
    /**
     * ソースディレクトリとターゲットディレクトリのパスを比較し、差分を検出します。
     *
     * @param sourcePath   ソースディレクトリのパス
     * @param targetPath   ターゲットディレクトリのパス
     * @param relativePath ソースディレクトリからの相対パス
     * @throws IOException 入出力エラーが発生した場合
     */
    @Override
    protected void processPath(Path sourcePath, Path targetPath, Path relativePath)
            throws IOException {

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

    /**
     * ターゲットディレクトリを走査して、ソースディレクトリに存在しないファイルを検出します。
     *
     * @param source      ソースディレクトリのパス
     * @param destination ターゲットディレクトリのパス
     * @throws IOException 入出力エラーが発生した場合
     */
    @Override
    protected void postProcess(Path source, Path destination) throws IOException {
        // ターゲットディレクトリを走査してソースにないファイルを検出
        if (Files.exists(destination)) {
            try (var stream = Files.walk(destination)) {
                stream.forEach(path -> processDestinationPath(source, destination, path));
            }
        }
    }

    /**
     * ターゲットディレクトリのパスを処理し、ソースディレクトリに存在しないファイルを検出します。
     *
     * @param source      ソースディレクトリのパス
     * @param destination ターゲットディレクトリのパス
     * @param path        ターゲットディレクトリ内の現在のパス
     */
    private void processDestinationPath(Path source, Path destination, Path path) {
        if (!path.equals(destination)) { // ルートディレクトリは除外
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

    /**
     * ソースディレクトリとターゲットディレクトリを比較し、差分を検出します。
     *
     * @param srcPath  ソースディレクトリのパス
     * @param destPath ターゲットディレクトリのパス
     * @throws IOException 入出力エラーが発生した場合
     */
    @Override
    public void processDirectory(String srcPath, String destPath) throws IOException {

        Path source = Path.of(srcPath);
        Path destination = Path.of(destPath);

        // ソースディレクトリの存在チェック
        if (!Files.exists(source)) {
            throw new IOException("Source directory does not exist");
        }

        // ターゲットディレクトリの存在チェック
        if (!Files.exists(destination)) {
            throw new IOException("Target directory does not exist: " + destPath);
        }

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
