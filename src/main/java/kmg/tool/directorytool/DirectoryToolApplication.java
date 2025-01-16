package kmg.tool.directorytool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ディレクトリ操作ツールのメインアプリケーションクラス。
 * Spring Bootアプリケーションとして起動し、コマンドラインから実行される。
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
}
