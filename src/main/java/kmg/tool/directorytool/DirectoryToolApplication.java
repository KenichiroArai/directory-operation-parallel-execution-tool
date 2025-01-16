package kmg.tool.directorytool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * ディレクトリ操作ツールのメインアプリケーションクラス。 Spring Bootアプリケーションとして起動し、コマンドラインから実行される。
 */
@SpringBootApplication
public class DirectoryToolApplication {
    /**
     * アプリケーションのエントリーポイント。
     *
     * @param args コマンドライン引数
     */
    private static boolean isTestMode = false;

    public static boolean isTestMode() {
        return isTestMode;
    }

    public static void setTestMode(boolean testMode) {
        DirectoryToolApplication.isTestMode = testMode;
    }

    private static boolean hasExited = false;

    public static boolean hasExited() {
        return hasExited;
    }

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
