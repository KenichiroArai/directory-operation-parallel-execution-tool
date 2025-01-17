package kmg.tool.directorytool.runner;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kmg.tool.directorytool.model.OperationMode;
import kmg.tool.directorytool.service.DirectoryService;

/**
 * コマンドラインインターフェースを提供するクラス。 アプリケーション起動時にコマンドライン引数を処理し、適切なディレクトリ操作を実行する。 Spring Bootのコマンドラインランナーとして実装され、DIコンテナによって管理される。
 * <p>
 * 引数形式: java -jar directory-tool.jar <src> <dest> <mode>
 * <p>
 * mode: COPY, MOVE, DIFF
 *
 * @author kmg
 * @version 1.0
 * @see kmg.tool.directorytool.service.DirectoryService
 * @see kmg.tool.directorytool.model.OperationMode
 */
@Component
public class CommandLineAppRunner implements CommandLineRunner {

    /**
     * ディレクトリ操作サービス。
     */
    private final DirectoryService directoryService;

    /**
     * DirectoryServiceをDIするコンストラクタ。 Spring Bootのコンテナによって自動的にインスタンス化される。
     *
     * @param directoryService
     *                         ディレクトリ操作サービス。DIコンテナにより適切な実装が注入される。
     */
    public CommandLineAppRunner(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    /**
     * コマンドライン引数を処理し、ディレクトリ操作を実行するメイン処理。 Spring Bootアプリケーション起動時に自動的に呼び出される。
     *
     * @param args
     *             コマンドライン引数。args[0]: ソースディレクトリパス、args[1]: ターゲットディレクトリパス、args[2]: 操作モード (COPY, MOVE, DIFF)
     * @throws Exception
     *                   ディレクトリ操作中にIO例外が発生した場合、または無効な引数が指定された場合
     */
    @Override
    public void run(String... args) throws Exception {
        // 引数の数をチェック
        if (args.length != 3) {
            System.out.println("Usage: java -jar directory-tool.jar <src> <dest> <mode>");
            System.out.println("Modes: COPY, MOVE, DIFF");
            return;
        }

        // 引数をパース
        String src     = args[0];
        String dest    = args[1];
        String modeStr = args[2].toUpperCase();

        try {
            // モード文字列をenumに変換
            OperationMode mode = OperationMode.valueOf(modeStr);

            // ディレクトリ操作を実行
            directoryService.processDirectory(src, dest, mode);
            System.out.println("Operation completed successfully");
        } catch (IllegalArgumentException e) {
            // 無効なモードが指定された場合
            System.out.println("Invalid mode: " + modeStr);
            System.out.println("Valid modes are: COPY, MOVE, DIFF");
        } catch (IOException e) {
            // ディレクトリ操作中にエラーが発生した場合
            System.out.println("Error: " + e.getMessage());
        }
    }
}
