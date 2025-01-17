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
    /**
     * 個々のファイル/ディレクトリに対して移動操作を実行する。 ソースがディレクトリの場合、ターゲットディレクトリを作成する。 ソースがファイルの場合、親ディレクトリを作成し、ファイルを移動する。 既存のファイルは上書きされる。
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
    protected void processPath(Path sourcePath, Path targetPath, Path relativePath) throws IOException {
        if (Files.isDirectory(sourcePath)) {
            Files.createDirectories(targetPath);
        } else {
            // ファイル移動前にターゲットディレクトリが存在することを保証
            Files.createDirectories(targetPath.getParent());
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 移動操作後の後処理を実行する。 ソースディレクトリ内の空になったディレクトリを削除する。 削除は深い階層から順に行われる。
     *
     * @param source
     *                    ソースディレクトリのパス
     * @param destination
     *                    ターゲットディレクトリのパス
     * @throws IOException
     *                     ディレクトリの削除中にI/Oエラーが発生した場合。例えば、削除権限がない場合など。
     */
    @Override
    protected void postProcess(Path source, Path destination) throws IOException {
        // 空になったディレクトリを削除
        try (var stream = Files.walk(source)) {
            stream.sorted((a, b) -> b.toString().length() - a.toString().length()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    // 削除に失敗した場合は無視
                }
            });
        }
    }
}
