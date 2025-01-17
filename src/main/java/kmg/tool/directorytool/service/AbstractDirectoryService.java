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
 * ディレクトリ操作の基本機能を提供する抽象クラス。 このクラスは、ファイルシステム操作の共通機能を実装し、具体的なディレクトリ操作（コピー、移動、差分比較）の ベースとなる機能を提供する。
 * <p>
 * 主な特徴：
 * <ul>
 * <li>マルチスレッドによる並列処理機能
 * <li>ディレクトリ走査の共通実装
 * <li>ファイル操作の基本的な検証機能
 * <li>ファイル比較の共通ユーティリティ
 * </ul>
 * <p>
 * このクラスを継承するサブクラスは、{@link #processPath(Path, Path, Path)}メソッドと {@link #postProcess(Path, Path)}メソッドを実装することで、
 * 具体的なディレクトリ操作を定義する。
 *
 * @author kmg
 * @version 1.0
 * @see CopyDirectoryService
 * @see MoveDirectoryService
 * @see DiffDirectoryService
 */
public abstract class AbstractDirectoryService {
    /** 並列処理で使用するスレッド数。システムで利用可能なCPUの論理コア数に基づいて設定されます。 */
    protected static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /** 並列処理用のスレッドプール。タスクの実行を管理し、スレッドの再利用を可能にします。 */
    protected final ExecutorService executorService = Executors
            .newFixedThreadPool(AbstractDirectoryService.THREAD_POOL_SIZE);

    /**
     * ディレクトリの処理を実行する。
     *
     * @param srcPath
     *                 ソースディレクトリのパス
     * @param destPath
     *                 ターゲットディレクトリのパス
     * @throws IOException
     *                     ソースディレクトリが存在しない場合、ターゲットディレクトリの作成に失敗した場合、 またはファイル処理中にエラーが発生した場合。
     */
    public void processDirectory(final String srcPath, final String destPath) throws IOException {
        final Path source      = Path.of(srcPath);
        final Path destination = Path.of(destPath);

        AbstractDirectoryService.validatePaths(source, destination);

        final List<Future<?>> futures = new ArrayList<>();

        try (var stream = Files.walk(source)) {
            stream.forEach(path -> {
                final Future<?> future = this.executorService.submit(() -> {
                    try {
                        final Path relativePath = source.relativize(path);
                        final Path targetPath   = destination.resolve(relativePath);
                        this.processPath(path, targetPath, relativePath);
                    } catch (final IOException e) {
                        throw new RuntimeException("Failed to process file: " + path, e);
                    }
                });
                futures.add(future);
            });
        }

        AbstractDirectoryService.waitForCompletion(futures);
        this.postProcess(source, destination);
    }

    /**
     * ソースとターゲットのパスを検証する。 ソースパスが存在し、ディレクトリであることを確認する。 ターゲットパスが存在する場合、ディレクトリであることを確認する。 ターゲットパスが存在しない場合、ディレクトリを作成する。
     *
     * @param source
     *                    ソースディレクトリのパス
     * @param destination
     *                    ターゲットディレクトリのパス
     * @throws IOException
     *                     ソースパスが存在しない、またはディレクトリでない場合、 ターゲットパスが存在するがディレクトリでない場合、 ターゲットディレクトリの作成に失敗した場合
     */
    protected static void validatePaths(final Path source, final Path destination) throws IOException {
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
     * 個々のファイル/ディレクトリに対して具体的な操作を実行する。 サブクラスで実装されるべき抽象メソッド。
     *
     * @param sourcePath
     *                     処理対象のソースパス
     * @param targetPath
     *                     処理対象のターゲットパス
     * @param relativePath
     *                     ソースディレクトリからの相対パス
     * @throws IOException
     *                     ファイル操作中にエラーが発生した場合
     */
    protected abstract void processPath(Path sourcePath, Path targetPath, Path relativePath) throws IOException;

    /**
     * すべてのファイル処理が完了した後に実行する後処理。 サブクラスで実装されるべき抽象メソッド。 主にリソースの解放や最終的な状態確認などを行う。
     *
     * @param source
     *                    ソースディレクトリのパス
     * @param destination
     *                    ターゲットディレクトリのパス
     * @throws IOException
     *                     後処理中にファイル操作エラーが発生した場合。例えば、一時ファイルの削除に失敗した場合など。
     */
    protected abstract void postProcess(Path source, Path destination) throws IOException;

    /**
     * 2つのファイルの内容をバイト単位で比較する。 ファイルサイズが異なる場合は即座にfalseを返す。 ファイルサイズが同じ場合、内容が完全に一致する場合にtrueを返す。
     *
     * @param file1
     *              比較対象のファイル1
     * @param file2
     *              比較対象のファイル2
     * @return ファイル内容が完全に一致する場合true、それ以外の場合false
     * @throws IOException
     *                     ファイルの読み取り中にエラーが発生した場合
     */
    protected static boolean compareFiles(final Path file1, final Path file2) throws IOException {
        if (Files.size(file1) != Files.size(file2)) {
            return false;
        }
        return Files.mismatch(file1, file2) == -1;
    }

    /**
     * すべての非同期タスクの完了を待機する。 各タスクには30秒のタイムアウトが設定されており、 タイムアウトした場合やタスク内で例外が発生した場合はIOExceptionをスローする。
     *
     * @param futures
     *                完了を待機するFutureオブジェクトのリスト
     * @throws IOException
     *                     タスクの実行中にInterruptedException, ExecutionException, TimeoutExceptionが発生した場合。
     */
    protected static void waitForCompletion(final List<Future<?>> futures) throws IOException {
        for (final Future<?> future : futures) {
            try {
                future.get(30, TimeUnit.SECONDS);
            } catch (InterruptedException | java.util.concurrent.ExecutionException
                    | java.util.concurrent.TimeoutException e) {
                throw new IOException("Failed to process directory: " + e.getMessage(), e);
            }
        }
    }
}
