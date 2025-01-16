package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * ディレクトリ操作の基本機能を提供する抽象クラス。
 */
public abstract class AbstractDirectoryService {
    /**
     * 並列処理で使用するスレッド数
     */
    protected static final int THREAD_POOL_SIZE = 4;

    /**
     * 並列処理用のスレッドプール。
     */
    protected final ExecutorService executorService =
            Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    /**
     * ディレクトリの処理を実行する。
     *
     * @param srcPath ソースディレクトリのパス
     * @param destPath ターゲットディレクトリのパス
     * @throws IOException ディレクトリ操作中にエラーが発生した場合
     */
    public void processDirectory(String srcPath, String destPath) throws IOException {
        Path source = Path.of(srcPath);
        Path destination = Path.of(destPath);

        validatePaths(source, destination);

        List<Future<?>> futures = new ArrayList<>();

        try (var stream = Files.walk(source)) {
            stream.forEach(path -> {
                Future<?> future = executorService.submit(() -> {
                    try {
                        Path relativePath = source.relativize(path);
                        Path targetPath = destination.resolve(relativePath);
                        processPath(path, targetPath, relativePath);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to process file: " + path, e);
                    }
                });
                futures.add(future);
            });
        }

        waitForCompletion(futures);
        postProcess(source, destination);
    }

    /**
     * パスの検証を行う
     */
    protected void validatePaths(Path source, Path destination) throws IOException {
        if (!Files.exists(source)) {
            throw new IOException("Source directory does not exist");
        }

        if (!Files.isDirectory(source)) {
            throw new IOException("Source path is not a directory");
        }

        if (Files.exists(destination) && !Files.isDirectory(destination)) {
            throw new IOException("Destination path exists but is not a directory");
        }

        if (!Files.exists(destination)) {
            Files.createDirectories(destination);
        }
    }

    /**
     * 個別のパスに対する処理を実行する
     */
    protected abstract void processPath(Path sourcePath, Path targetPath, Path relativePath)
            throws IOException;

    /**
     * すべてのファイル処理後の後処理を実行する
     */
    protected abstract void postProcess(Path source, Path destination) throws IOException;

    /**
     * 2つのファイルの内容を比較する
     */
    protected boolean compareFiles(Path file1, Path file2) throws IOException {
        if (Files.size(file1) != Files.size(file2)) {
            return false;
        }
        return Files.mismatch(file1, file2) == -1;
    }

    /**
     * すべてのタスクの完了を待つ
     */
    protected void waitForCompletion(List<Future<?>> futures) throws IOException {
        for (Future<?> future : futures) {
            try {
                future.get(30, TimeUnit.SECONDS);
            } catch (InterruptedException | java.util.concurrent.ExecutionException
                    | java.util.concurrent.TimeoutException e) {
                throw new IOException("Failed to process directory: " + e.getMessage(), e);
            }
        }
    }
}
