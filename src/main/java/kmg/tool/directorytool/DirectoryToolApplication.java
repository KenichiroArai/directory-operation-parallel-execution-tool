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

    public static void main(String[] args) {
        // Springアプリケーションを起動
        SpringApplication.run(DirectoryToolApplication.class, args);

        // テストモード以外の場合のみ、アプリケーションを終了
        if (!isTestMode) {
            System.exit(0);
        }
    }
}
