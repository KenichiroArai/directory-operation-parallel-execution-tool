package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import kmg.tool.directorytool.model.OperationMode;

/**
 * ディレクトリ操作の主要なビジネスロジックを提供するサービスクラス。 マルチスレッドを使用して並列処理を行い、大規模なディレクトリ操作を効率的に実行する。
 */
@Service
public class DirectoryService {

    /**
     * 並列処理で使用するスレッド数
     */
    private static final int THREAD_POOL_SIZE = 4;

    /**
     * 並列処理用のスレッドプール。
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    /**
     * 指定されたソースディレクトリをターゲットディレクトリに対して処理する。 処理内容は指定された操作モード（COPY、MOVE、またはDIFF）に依存する。
     *
     * @param srcPath ソースディレクトリのパス
     * @param destPath ターゲットディレクトリのパス
     * @param mode 操作モード（COPY、MOVE、またはDIFF）
     * @throws IOException ディレクトリ操作中にエラーが発生した場合
     */
    public void processDirectory(String srcPath, String destPath, OperationMode mode)
            throws IOException {
        // パスオブジェクトの作成
        Path source = Path.of(srcPath);
        Path destination = Path.of(destPath);

        // ソースディレクトリの存在確認
        if (!Files.exists(source)) {
            throw new IOException("Source directory does not exist");
        }

        // ソースパスがディレクトリかどうかの確認
        if (!Files.isDirectory(source)) {
            throw new IOException("Source path is not a directory");
        }

        // ターゲットパスが既に存在する場合、ディレクトリかどうかを確認
        if (Files.exists(destination) && !Files.isDirectory(destination)) {
            throw new IOException("Destination path exists but is not a directory");
        }

        // ターゲットディレクトリが存在しない場合は作成
        if (!Files.exists(destination)) {
            Files.createDirectories(destination);
        }

        List<Future<?>> futures = new ArrayList<>();

        // ソースディレクトリを再帰的に走査
        try (var stream = Files.walk(source)) {
            stream.forEach(path -> {
                Future<?> future = executorService.submit(() -> {
                    try {
                        // 相対パスを計算
                        Path relativePath = source.relativize(path);
                        Path targetPath = destination.resolve(relativePath);

                        // ディレクトリの場合の処理
                        if (Files.isDirectory(path)) {
                            if (!Files.exists(targetPath)) {
                                // ターゲットディレクトリが存在しない場合は作成
                                Files.createDirectories(targetPath);
                            }
                        } else {
                            // ファイルの場合の処理
                            if (mode == OperationMode.COPY) {
                                // コピーモードの場合、ファイルをコピー
                                Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                            } else if (mode == OperationMode.MOVE) {
                                // ムーブモードの場合、ファイルを移動
                                Files.move(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                            } else if (mode == OperationMode.DIFF) {
                                // DIFFモードの場合、ファイルの差分を比較
                                if (!Files.exists(targetPath)) {
                                    if (Files.isDirectory(path)) {
                                        System.out.println("Directory only in source: " + relativePath);
                                    } else {
                                        System.out.println("Only in source: " + relativePath);
                                    }
                                } else if (!Files.isDirectory(path) && !compareFiles(path, targetPath)) {
                                    System.out.println("Different: " + relativePath);
                                }
                            }
                        }
                    } catch (IOException e) {
                        // ファイル処理中にエラーが発生した場合
                        throw new RuntimeException("Failed to process file: " + path, e);
                    }
                });
                futures.add(future);
            });
        }

        // すべてのタスクの完了を待つ
        for (Future<?> future : futures) {
            try {
                future.get(30, TimeUnit.SECONDS);
            } catch (InterruptedException | java.util.concurrent.ExecutionException
                    | java.util.concurrent.TimeoutException e) {
                throw new IOException("Failed to process directory: " + e.getMessage(), e);
            }
        }

        // DIFFモードの場合、ターゲットディレクトリを走査してソースにないファイルを検出
        if (mode == OperationMode.DIFF) {
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

        // 移動モードの場合、空になったディレクトリを削除
        if (mode == OperationMode.MOVE) {
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

    /**
     * 2つのファイルの内容を比較する。
     *
     * @param file1 比較する最初のファイル
     * @param file2 比較する2番目のファイル
     * @return ファイルの内容が同じ場合はtrue、異なる場合はfalse
     * @throws IOException ファイルの読み取り中にエラーが発生した場合
     */
    private boolean compareFiles(Path file1, Path file2) throws IOException {
        if (Files.size(file1) != Files.size(file2)) {
            return false;
        }
        return Files.mismatch(file1, file2) == -1;
    }
}
