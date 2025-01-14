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
    public static void main(String[] args) {
        // Springアプリケーションを起動
        SpringApplication.run(DirectoryToolApplication.class, args);

        // コマンドラインアプリケーションのため、正常終了を示すステータスコードで終了
        System.exit(0);
    }
}
