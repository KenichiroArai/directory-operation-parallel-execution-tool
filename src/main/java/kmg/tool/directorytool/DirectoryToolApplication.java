package kmg.tool.directorytool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * ディレクトリ操作ツールのメインアプリケーションクラス。
 * Spring Bootアプリケーションとして起動し、コマンドラインから実行される。
 * ディレクトリのコピー、移動、差分比較の操作を提供する。
 *
 * @author kmg
 * @version 1.0
 */
@SpringBootApplication
public class DirectoryToolApplication {
    /**
     * アプリケーションのエントリーポイント。
     *
     * @param args コマンドライン引数
     */
    /** テストモードフラグ */
    private static boolean isTestMode = false;

    /**
     * テストモードの状態を取得する。
     *
     * @return テストモードの場合true、それ以外の場合false
     */
    public static boolean isTestMode() {
        return isTestMode;
    }

    /**
     * テストモードを設定する。
     *
     * @param testMode テストモードフラグ
     */
    public static void setTestMode(boolean testMode) {
        DirectoryToolApplication.isTestMode = testMode;
    }

    /** アプリケーション終了フラグ */
    private static boolean hasExited = false;

    /**
     * アプリケーションの終了状態を取得する。
     *
     * @return アプリケーションが終了している場合true、それ以外の場合false
     */
    public static boolean hasExited() {
        return hasExited;
    }

    /**
     * アプリケーションの終了状態をリセットする。
     * テストケース実行時に使用される。
     */
    public static void resetExitStatus() {
        hasExited = false;
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            if (!exitWithError()) {
                System.err.println("無効な引数です。");
            }
            return;
        }

        String sourcePath = args[0];
        String operationType = args[2];

        Path source = Paths.get(sourcePath);
        if (!Files.exists(source)) {
            if (!exitWithError()) {
                System.err.println("ソースディレクトリが存在しません。");
            }
            return;
        }

        if (!Arrays.asList("COPY", "MOVE", "DIFF").contains(operationType)) {
            if (!exitWithError()) {
                System.err.println("モードが不正です。");
            }
            return;
        }

        // Springアプリケーションを起動
        SpringApplication.run(DirectoryToolApplication.class, args);

        // テストモード以外の場合のみ、アプリケーションを終了
        if (!isTestMode) {
            hasExited = true;
            // 実際の本番環境でのみSystem.exitを呼び出す
            if (!Boolean.getBoolean("skipExit")) {
                System.exit(0);
            }
        }
    }

    /**
     * エラー終了時の処理を行う。
     * テストモードやskipExit設定に応じて、適切な終了処理を実行する。
     *
     * @return エラー処理が成功した場合true、それ以外の場合false
     */
    private static boolean exitWithError() {
        boolean result = false;

        hasExited = true;
        if (!isTestMode && !Boolean.getBoolean("skipExit")) {
            return result;
        }

        result = true;
        return result;
    }
}
