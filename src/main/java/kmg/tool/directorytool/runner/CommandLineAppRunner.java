package kmg.tool.directorytool.runner;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kmg.tool.directorytool.model.OperationMode;
import kmg.tool.directorytool.service.DirectoryService;

/**
 * コマンドラインインターフェースを提供するクラス。
 * アプリケーション起動時にコマンドライン引数を処理し、適切なディレクトリ操作を実行する。
 */
@Component
public class CommandLineAppRunner implements CommandLineRunner {

    private final DirectoryService directoryService;

    /**
     * DirectoryServiceをDIするコンストラクタ。
     *
     * @param directoryService ディレクトリ操作サービス
     */
    public CommandLineAppRunner(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    /**
     * コマンドライン引数を処理し、ディレクトリ操作を実行するメイン処理。
     *
     * @param args コマンドライン引数
     * @throws Exception 処理中にエラーが発生した場合
     */
    @Override
    public void run(String... args) throws Exception {
        // 引数の数をチェック
        if (args.length != 3) {
            System.out.println("Usage: java -jar directory-tool.jar <src> <dest> <mode>");
            System.out.println("Modes: COPY, MOVE");
            return;
        }

        // 引数をパース
        String src = args[0];
        String dest = args[1];
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
            System.out.println("Valid modes are: COPY, MOVE");
        } catch (IOException e) {
            // ディレクトリ操作中にエラーが発生した場合
            System.out.println("Error: " + e.getMessage());
        }
    }
}
