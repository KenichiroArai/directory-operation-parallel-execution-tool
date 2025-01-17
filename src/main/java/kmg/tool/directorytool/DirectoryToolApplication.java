package kmg.tool.directorytool;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * ディレクトリ操作ツールのメインアプリケーションクラス。 Spring Bootアプリケーションとして起動し、コマンドラインから実行される。 ディレクトリのコピー、移動、差分比較の操作を提供する。
 *
 * @author kmg
 * @version 1.0
 */
@SpringBootApplication
public class DirectoryToolApplication {

    /** テストモードを制御するフラグ。 trueの場合、アプリケーションはテストモードで動作し、System.exitが呼び出されない。 */
    private static boolean isTestMode = false;

    /**
     * テストモードの状態を取得する。
     *
     * @return テストモードの場合true、それ以外の場合false
     */
    public static boolean isTestMode() {
        return DirectoryToolApplication.isTestMode;
    }

    /**
     * テストモードを設定する。
     *
     * @param testMode
     *                 テストモードフラグ
     */
    public static void setTestMode(final boolean testMode) {
        DirectoryToolApplication.isTestMode = testMode;
    }

    /**
     * アプリケーションの終了状態を管理するフラグ。 アプリケーションが終了処理を実行した場合にtrueとなる。
     */
    private static boolean hasExited = false;

    /**
     * アプリケーションの終了状態を取得する。
     *
     * @return アプリケーションが終了している場合true、それ以外の場合false
     */
    public static boolean hasExited() {
        return DirectoryToolApplication.hasExited;
    }

    /**
     * アプリケーションの終了状態をリセットする。 テストケース実行時に使用される。
     */
    public static void resetExitStatus() {
        DirectoryToolApplication.hasExited = false;
    }

    /**
     * アプリケーションのメインメソッド。 コマンドライン引数を解析し、適切なディレクトリ操作を実行する。 引数の形式: args[0]: ソースディレクトリのパス args[1]: 対象ディレクトリのパス（COPY/MOVEの場合）
     * args[2]: 操作タイプ（COPY/MOVE/DIFF）
     *
     * @param args
     *             コマンドライン引数の配列
     */
    public static void main(final String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(DirectoryToolApplication.class,
                DirectoryToolApplication.getParams(args))) {
            // テストモード以外の場合のみ、アプリケーションを終了
            if (!DirectoryToolApplication.isTestMode) {
                DirectoryToolApplication.hasExited = true;
                // 実際の本番環境でのみSystem.exitを呼び出す
                if (!Boolean.getBoolean("skipExit")) {
                    System.exit(0);
                }
            }
        }
    }

    /**
     * コマンドライン引数を解析し、Springアプリケーションの起動パラメータを生成する。
     *
     * @param args
     *             コマンドライン引数の配列
     * @return Springアプリケーションの起動パラメータ
     */
    private static String[] getParams(final String[] args) {
        if (args.length < 3) {
            DirectoryToolApplication.exitWithError();
            System.err.println("引数が不足しています。");
            return new String[0];
        }

        String sourcePath      = null;
        String destinationPath = null;
        String operationType   = null;

        if (args.length == 4) {
            if (!"--spring.output.ansi.enabled=always".equals(args[0])) {
                System.err.println("最初の引数が「--spring.output.ansi.enabled=always」以外が設定されています。");
                return new String[0];
            }
            sourcePath = args[1];
            destinationPath = args[2];
            operationType = args[3];
        } else {
            sourcePath = args[0];
            destinationPath = args[1];
            operationType = args[2];
        }

        final Path source = Paths.get(sourcePath);
        if (!Files.exists(source)) {
            DirectoryToolApplication.exitWithError();
            System.err.println("ソースディレクトリが存在しません。");
            return new String[0];
        }

        if (!Arrays.asList("COPY", "MOVE", "DIFF").contains(operationType)) {
            DirectoryToolApplication.exitWithError();
            System.err.println("無効なモードです。");
            return new String[0];
        }

        return new String[] {
                sourcePath, destinationPath, operationType
        };
    }

    /**
     * エラー終了時の処理を行う。 テストモードやskipExit設定に応じて、適切な終了処理を実行する。
     *
     * @return エラー処理が成功した場合true、それ以外の場合false
     */
    static boolean exitWithError() {
        DirectoryToolApplication.hasExited = true;
        if (DirectoryToolApplication.isTestMode || Boolean.getBoolean("skipExit")) {
            return true;
        }
        return false;
    }
}
