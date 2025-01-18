package kmg.tool.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

/**
 * ディレクトリの差分を検出するサービスクラス。 {@link AbstractDirectoryService}を継承し、2つのディレクトリ間の差分を検出・報告する機能を提供する。
 * <p>
 * 主な特徴：
 * <ul>
 * <li>ディレクトリ構造の完全な比較
 * <li>ファイル内容の詳細な比較
 * <li>並列処理による高速な差分検出
 * <li>多様な差分タイプの検出と報告
 * </ul>
 * <p>
 * 検出される差分の種類：
 * <ul>
 * <li>ソースディレクトリのみに存在するファイル
 * <li>ターゲットディレクトリのみに存在するファイル
 * <li>ファイル内容の違い
 * <li>ファイルタイプの違い（ファイルvsディレクトリ）
 * <li>ディレクトリ構造の違い
 * </ul>
 * <p>
 * 出力形式：
 *
 * <pre>
 * ソースのみに存在: path/to/file                        - ソースディレクトリのみに存在するファイル
 * ターゲットのみに存在: path/to/file                    - ターゲットディレクトリのみに存在するファイル
 * 差異あり: path/to/file                               - 内容が異なるファイル
 * 差異あり: path/to/file (ファイル vs ディレクトリ)      - タイプが異なるパス
 * ソースディレクトリのみに存在するディレクトリ: path/to/dir  - ソースのみに存在するディレクトリ
 * ターゲットディレクトリのみに存在するディレクトリ: path/to/dir - ターゲットのみに存在するディレクトリ
 * </pre>
 * <p>
 * このサービスはSpring Frameworkのコンポーネントとして実装され、 {@link DirectoryService}クラスによって使用される。
 * スレッドセーフな実装となっており、複数のスレッドから同時にアクセスしても安全に動作する。
 * <p>
 * 使用例：
 *
 * <pre>
 * DiffDirectoryService service = new DiffDirectoryService();
 * service.processDirectory("/source/dir", "/target/dir");
 * // 差分が標準出力に表示される
 * </pre>
 *
 * @author kmg
 * @version 1.0
 * @see AbstractDirectoryService
 * @see DirectoryService
 */
@Service
public class DiffDirectoryService extends AbstractDirectoryService {
    /**
     * ソースディレクトリとターゲットディレクトリのパスを比較し、差分を検出します。
     *
     * @param sourcePath
     *                     ソースディレクトリのパス
     * @param targetPath
     *                     ターゲットディレクトリのパス
     * @param relativePath
     *                     ソースディレクトリからの相対パス
     * @throws IOException
     *                     ファイルまたはディレクトリの存在確認中にI/Oエラーが発生した場合。
     */
    @Override
    protected void processPath(final Path sourcePath, final Path targetPath, final Path relativePath)
            throws IOException {

        if (Files.isDirectory(sourcePath)) {
            if (!Files.exists(targetPath)) {
                System.out.println("ソースディレクトリのみに存在するディレクトリ: " + relativePath);
            } else if (!Files.isDirectory(targetPath)) {
                System.out.println("差異あり: " + relativePath + " (ディレクトリ vs ファイル)");
            }
        } else if (!Files.exists(targetPath)) {
            System.out.println("ソースのみに存在: " + relativePath);
        } else if (!Files.isRegularFile(targetPath)) {
            System.out.println("差異あり: " + relativePath + " (ファイル vs ディレクトリ)");
        } else if (!AbstractDirectoryService.compareFiles(sourcePath, targetPath)) {
            System.out.println("差異あり: " + relativePath);
        }
    }

    /**
     * ターゲットディレクトリを走査して、ソースディレクトリに存在しないファイルを検出します。
     *
     * @param source
     *                    ソースディレクトリのパス
     * @param destination
     *                    ターゲットディレクトリのパス
     * @throws IOException
     *                     ディレクトリの走査中にI/Oエラーが発生した場合。
     */
    @Override
    protected void postProcess(final Path source, final Path destination) throws IOException {
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
     * @param source
     *                    ソースディレクトリのパス
     * @param destination
     *                    ターゲットディレクトリのパス
     * @param path
     *                    ターゲットディレクトリ内の現在のパス
     */
    private static void processDestinationPath(final Path source, final Path destination, final Path path) {
        if (!path.equals(destination)) { // ルートディレクトリは除外
            final Path relativePath = destination.relativize(path);
            final Path sourcePath   = source.resolve(relativePath);
            if (!Files.exists(sourcePath)) {
                if (Files.isDirectory(path)) {
                    System.out.println("ターゲットディレクトリのみに存在するディレクトリ: " + relativePath);
                } else {
                    System.out.println("ターゲットのみに存在: " + relativePath);
                }
            }
        }
    }

    /**
     * ソースディレクトリとターゲットディレクトリを比較し、差分を検出します。 このメソッドは親クラスの実装をオーバーライドし、両方のディレクトリが存在することを 確認してから処理を開始します。
     *
     * @param srcPath
     *                 ソースディレクトリのパス
     * @param destPath
     *                 ターゲットディレクトリのパス
     * @throws IOException
     *                     ソースディレクトリまたはターゲットディレクトリが存在しない場合に発生します。
     */
    @Override
    public void processDirectory(final String srcPath, final String destPath) throws IOException {

        final Path source      = Path.of(srcPath);
        final Path destination = Path.of(destPath);

        // ソースディレクトリの存在チェック
        if (!Files.exists(source)) {
            throw new IOException("ソースディレクトリが存在しません");
        }

        // ターゲットディレクトリの存在チェック
        if (!Files.exists(destination)) {
            throw new IOException("ターゲットディレクトリが存在しません: " + destPath);
        }

        AbstractDirectoryService.validatePaths(source, destination);

        // ソースディレクトリの処理
        try (var stream = Files.walk(source)) {
            stream.forEach(path -> {
                try {
                    final Path relativePath = source.relativize(path);
                    final Path targetPath   = destination.resolve(relativePath);
                    this.processPath(path, targetPath, relativePath);
                } catch (final IOException e) {
                    throw new RuntimeException("ファイルの処理に失敗しました: " + path, e);
                }
            });
        }

        // ターゲットディレクトリの処理
        this.postProcess(source, destination);
    }
}
