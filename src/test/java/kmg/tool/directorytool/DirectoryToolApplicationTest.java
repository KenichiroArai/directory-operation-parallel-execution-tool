package kmg.tool.directorytool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * DirectoryToolApplicationのテストクラス。<br>
 * <p>
 * アプリケーションの主要な機能とエラーハンドリングをテストします。
 * </p>
 */
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
@SuppressWarnings("static-method")
public class DirectoryToolApplicationTest {

    /**
     * アプリケーションコンテキストが正常にロードされることを確認するテスト
     *
     * @param context
     *                自動注入されるアプリケーションコンテキスト
     */
    @Test
    public void contextLoads(final ApplicationContext context) {

        // コンテキストが正常にロードされたことを確認
        Assertions.assertNotNull(context, "アプリケーションコンテキストが正常にロードされていません");

    }

    /**
     * メインメソッドが例外をスローせずに正常に実行されることを確認するテスト
     */
    @Test
    public void testMain() {

        Assertions.assertDoesNotThrow(() -> {

            final String[] args = {
                    "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
            };

            DirectoryToolApplication.main(args);

        }, "メインメソッドの実行中に例外が発生しました");

    }

    /**
     * コマンドライン引数を指定した場合のメインメソッド実行テスト
     */
    @Test
    public void testMainWithArguments() {

        Assertions.assertDoesNotThrow(() -> {

            DirectoryToolApplication.main(new String[] {
                    "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
                    "--spring.main.banner-mode=off",
            });

        }, "引数付きメインメソッドの実行中に例外が発生しました");

    }

    /**
     * アプリケーションの正常終了を確認するテスト
     */
    @Test
    public void testApplicationShutdown() {

        final String[] args = {
                "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
        };

        try (final ConfigurableApplicationContext context = SpringApplication.run(DirectoryToolApplication.class,
                args)) {

            Assertions.assertDoesNotThrow(() -> {

                context.close();

            }, "アプリケーションの終了処理中に例外が発生しました");
            Assertions.assertFalse(context.isActive(), "アプリケーションコンテキストが正しく終了していません");

        }

    }
}
