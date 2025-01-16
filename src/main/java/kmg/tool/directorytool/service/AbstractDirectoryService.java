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
     * ソースとターゲットのパスを検証する。
     * ソースパスが存在し、ディレクトリであることを確認する。
     * ターゲットパスが存在する場合、ディレクトリであることを確認する。
     * ターゲットパスが存在しない場合、ディレクトリを作成する。
     *
     * @param source ソースディレクトリのパス
     * @param destination ターゲットディレクトリのパス
     * @throws IOException ソースパスが存在しない、またはディレクトリでない場合、
     *                     ターゲットパスが存在するがディレクトリでない場合、
     *                     ターゲットディレクトリの作成に失敗した場合
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
     * 個々のファイル/ディレクトリに対して具体的な操作を実行する。
     * サブクラスで実装されるべき抽象メソッド。
     *
     * @param sourcePath 処理対象のソースパス
     * @param targetPath 処理対象のターゲットパス
     * @param relativePath ソースディレクトリからの相対パス
     * @throws IOException ファイル操作中にエラーが発生した場合
     */
    protected abstract void processPath(Path sourcePath, Path targetPath, Path relativePath)
            throws IOException;

    /**
     * すべてのファイル処理が完了した後に実行する後処理。
     * サブクラスで実装されるべき抽象メソッド。
     * 主にリソースの解放や最終的な状態確認などを行う。
     *
     * @param source ソースディレクトリのパス
     * @param destination ターゲットディレクトリのパス
     * @throws IOException 後処理中にエラーが発生した場合
     */
    protected abstract void postProcess(Path source, Path destination) throws IOException;

    /**
     * 2つのファイルの内容をバイト単位で比較する。
     * ファイルサイズが異なる場合は即座にfalseを返す。
     * ファイルサイズが同じ場合、内容が完全に一致する場合にtrueを返す。
     *
     * @param file1 比較対象のファイル1
     * @param file2 比較対象のファイル2
     * @return ファイル内容が完全に一致する場合true、それ以外の場合false
     * @throws IOException ファイルの読み取り中にエラーが発生した場合
     */
    protected boolean compareFiles(Path file1, Path file2) throws IOException {
        if (Files.size(file1) != Files.size(file2)) {
            return false;
        }
        return Files.mismatch(file1, file2) == -1;
    }

    /**
     * すべての非同期タスクの完了を待機する。
     * 各タスクには30秒のタイムアウトが設定されており、
     * タイムアウトした場合やエラーが発生した場合はIOExceptionをスローする。
     *
     * @param futures 完了を待機するFutureオブジェクトのリスト
     * @throws IOException タスクの実行中にエラーが発生した場合、
     *                     またはタイムアウトした場合
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
