package kmg.tool.directorytool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * ディレクトリ操作ツールのメインアプリケーションクラス。
 *
 * @author kmg
 * @version 1.0
 */
@SpringBootApplication
public class DirectoryToolApplication {

    /**
     * アプリケーションのメインメソッド。
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
