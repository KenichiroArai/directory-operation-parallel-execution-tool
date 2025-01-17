package kmg.tool.directorytool;

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

    /**
     * アプリケーションのメインメソッド。 コマンドライン引数を解析し、適切なディレクトリ操作を実行する。 引数の形式: args[0]: ソースディレクトリのパス args[1]: 対象ディレクトリのパス（COPY/MOVEの場合）
     * args[2]: 操作タイプ（COPY/MOVE/DIFF）
     *
     * @param args
     *             コマンドライン引数の配列
     */
    public static void main(final String[] args) {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(DirectoryToolApplication.class, args)) {
            System.exit(SpringApplication.exit(ctx));
        }
    }

}
