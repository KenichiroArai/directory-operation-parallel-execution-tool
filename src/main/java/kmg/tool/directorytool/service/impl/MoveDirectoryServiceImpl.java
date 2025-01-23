package kmg.tool.directorytool.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kmg.tool.directorytool.service.AbstractDirectoryService;
import kmg.tool.directorytool.service.DirectoryService;
import kmg.tool.directorytool.service.MoveDirectoryService;

/**
 * ディレクトリの移動操作を実行するサービスクラス。<br>
 * <p>
 * {@link AbstractDirectoryService}を継承し、ディレクトリとその内容の再帰的な移動機能を提供する。
 * </p>
 * <p>
 * 主な特徴：
 * <ul>
 * <li>ディレクトリ構造の完全な移動
 * <li>既存ファイルの自動上書き
 * <li>並列処理による高速なファイル移動
 * <li>ディレクトリ階層の自動作成
 * <li>移動後のソースディレクトリの自動クリーンアップ
 * </ul>
 * <p>
 * このサービスはSpring Frameworkのコンポーネントとして実装され、 {@link DirectoryService}クラスによって使用される。
 * スレッドセーフな実装となっており、複数のスレッドから同時にアクセスしても安全に動作する。
 * <p>
 * 移動処理の特徴：
 * <ul>
 * <li>ファイルの移動は原子的に行われる（可能な場合）
 * <li>ソースディレクトリは移動完了後に自動的に削除される
 * <li>空のディレクトリは深い階層から順に削除される
 * </ul>
 * <p>
 * 使用例：
 *
 * <pre>
 * MoveDirectoryService service = new MoveDirectoryServiceImpl();
 * service.processDirectory("/source/dir", "/target/dir");
 * // 移動完了後、/source/dirは自動的に削除される
 * </pre>
 *
 * @author kmg
 * @version 1.0
 * @see AbstractDirectoryService
 * @see DirectoryService
 */
@Service
public class MoveDirectoryServiceImpl extends AbstractDirectoryServiceImpl implements MoveDirectoryService {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(MoveDirectoryServiceImpl.class);

    /**
     * 個々のファイル/ディレクトリに対して移動操作を実行する。<br>
     * <p>
     * ソースがディレクトリの場合、ターゲットディレクトリを作成する。 ソースがファイルの場合、親ディレクトリを作成し、ファイルを移動する。 既存のファイルは上書きされる。
     * </p>
     *
     * @param sourcePath
     *                     移動元のパス
     * @param targetPath
     *                     移動先のパス
     * @param relativePath
     *                     ソースディレクトリからの相対パス
     * @throws IOException
     *                     ディレクトリ作成またはファイル移動中にエラーが発生した場合
     */
    @Override
    protected void processPath(final Path sourcePath, final Path targetPath, final Path relativePath)
            throws IOException {

        if (Files.isDirectory(sourcePath)) {

            Files.createDirectories(targetPath);
            return;

        }

        // ファイル移動前にターゲットディレクトリが存在することを保証
        Files.createDirectories(targetPath.getParent());
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

    }

    /**
     * 移動操作後の後処理を実行する。 <br>
     * <p>
     * ソースディレクトリ内の空になったディレクトリを削除する。<br>
     * 削除は深い階層から順に行われる。
     * </p>
     * <p>
     * この処理は以下の手順で実行されます：
     * <ol>
     * <li>ソースディレクトリ内のすべてのパスを取得
     * <li>パスをその長さで降順にソート（深い階層から処理するため）
     * <li>各パスに対して削除を試行
     * </ol>
     * 削除に失敗した場合は処理を継続し、可能な限り多くのディレクトリを削除します。
     *
     * @param source
     *                    ソースディレクトリのパス
     * @param destination
     *                    ターゲットディレクトリのパス
     * @throws IOException
     *                     ディレクトリの削除中にI/Oエラーが発生した場合。例えば、削除権限がない場合など。
     */
    @Override
    protected void postProcess(final Path source, final Path destination) throws IOException {

        // 空になったディレクトリを削除
        try (Stream<Path> stream = Files.walk(source)) {

            stream.sorted((src, dest) -> dest.toString().length() - src.toString().length()).forEach(path -> {

                try {

                    this.deleteIfExists(path);

                } catch (final IOException e) {

                    MoveDirectoryServiceImpl.logger.error(String.format("パス '%s' の削除に失敗しました", path), e);

                }

            });

        }

    }

    /**
     * 指定されたパスが存在する場合に削除する。<br>
     *
     * @param path
     *             ファイルまたはディレクトリのパス
     * @throws IOException
     *                     ファイルまたはディレクトリの削除中にエラーが発生した場合
     */
    @SuppressWarnings("static-method")
    protected void deleteIfExists(final Path path) throws IOException {

        Files.deleteIfExists(path);

    }

}
