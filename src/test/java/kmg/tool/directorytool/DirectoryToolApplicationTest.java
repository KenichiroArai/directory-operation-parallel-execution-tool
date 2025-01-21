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
@SuppressWarnings("static-method")
@SpringBootTest
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

            DirectoryToolApplication.main(new String[] {});

        }, "メインメソッドの実行中に例外が発生しました");

    }

    /**
     * コマンドライン引数を指定した場合のメインメソッド実行テスト
     */
    @Test
    public void testMainWithArguments() {

        Assertions.assertDoesNotThrow(() -> {

            DirectoryToolApplication.main(new String[] {
                    "--spring.main.banner-mode=off"
            });

        }, "引数付きメインメソッドの実行中に例外が発生しました");

    }

    /**
     * アプリケーションの正常終了を確認するテスト
     */
    @Test
    public void testApplicationShutdown() {

        try (final ConfigurableApplicationContext context = SpringApplication.run(DirectoryToolApplication.class)) {

            Assertions.assertDoesNotThrow(() -> {

                context.close();

            }, "アプリケーションの終了処理中に例外が発生しました");
            Assertions.assertFalse(context.isActive(), "アプリケーションコンテキストが正しく終了していません");

        }

    }
}
