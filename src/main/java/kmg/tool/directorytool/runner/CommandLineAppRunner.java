package kmg.tool.directorytool.runner;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kmg.tool.directorytool.model.OperationMode;
import kmg.tool.directorytool.service.DirectoryService;

/**
 * コマンドラインインターフェースを提供するクラス。 Spring Bootのコマンドラインランナーとして実装され、アプリケーションの起動時に コマンドライン引数を処理し、適切なディレクトリ操作を実行する。
 * <p>
 * 基本的な使用方法：
 * 
 * <pre>
 * java -jar directory-tool.jar &lt;src&gt; &lt;dest&gt; &lt;mode&gt;
 * </pre>
 * <p>
 * パラメータ：
 * <ul>
 * <li>&lt;src&gt; - 操作対象のソースディレクトリパス
 * <li>&lt;dest&gt; - 操作対象のターゲットディレクトリパス
 * <li>&lt;mode&gt; - 実行する操作のモード（COPY/MOVE/DIFF）
 * </ul>
 * <p>
 * 使用例：
 * 
 * <pre>
 * # ディレクトリのコピー
 * java -jar directory-tool.jar /source/dir /target/dir COPY
 *
 * # ディレクトリの移動
 * java -jar directory-tool.jar /source/dir /target/dir MOVE
 *
 * # ディレクトリの差分比較
 * java -jar directory-tool.jar /source/dir /target/dir DIFF
 * </pre>
 * <p>
 * エラーハンドリング：
 * <ul>
 * <li>引数の数が不正な場合：使用方法を表示
 * <li>無効なモードが指定された場合：有効なモードの一覧を表示
 * <li>ディレクトリ操作中のエラー：エラーメッセージを表示
 * </ul>
 * <p>
 * 出力メッセージ：
 * <ul>
 * <li>成功時：「Operation completed successfully」
 * <li>無効なモード時：「Invalid mode: [指定されたモード]」
 * <li>エラー発生時：「Error: [エラーメッセージ]」
 * </ul>
 * <p>
 * このクラスはSpring Bootのコンテナによって管理され、 {@link DirectoryService}がコンストラクタインジェクションされる。
 *
 * @author kmg
 * @version 1.0
 * @see kmg.tool.directorytool.service.DirectoryService
 * @see kmg.tool.directorytool.model.OperationMode
 */
@Component
public class CommandLineAppRunner implements CommandLineRunner {

    /**
     * ディレクトリ操作サービス。 Spring DIコンテナによって注入される {@link DirectoryService} のインスタンス。
     */
    private final DirectoryService directoryService;

    /**
     * DirectoryServiceをDIするコンストラクタ。 Spring Bootのコンテナによって自動的にインスタンス化される。 {@link DirectoryService}のインスタンスは、操作モードに応じて
     * 適切な実装クラスを選択・実行する。
     *
     * @param directoryService
     *                         ディレクトリ操作サービス。DIコンテナにより適切な実装が注入される。
     */
    public CommandLineAppRunner(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    /**
     * コマンドライン引数を処理し、ディレクトリ操作を実行するメイン処理。 Spring Bootアプリケーション起動時に自動的に呼び出される。
     * <p>
     * 処理の流れ：
     * <ol>
     * <li>引数の数を検証（3つの引数が必要）
     * <li>操作モードを文字列からenumに変換
     * <li>DirectoryServiceを使用してディレクトリ操作を実行
     * <li>結果に応じて適切なメッセージを出力
     * </ol>
     *
     * @param args
     *             コマンドライン引数。
     *             <ul>
     *             <li>args[0]: ソースディレクトリパス
     *             <li>args[1]: ターゲットディレクトリパス
     *             <li>args[2]: 操作モード (COPY, MOVE, DIFF)
     *             </ul>
     * @throws Exception
     *                   ディレクトリ操作中にIO例外が発生した場合、または無効な引数が指定された場合
     */
    @Override
    public void run(String... args) throws Exception {
        // 引数の数をチェック
        if (args.length != 3) {
            System.out.println("Usage: java -jar directory-tool.jar <src> <dest> <mode>");
            System.out.println("Modes: COPY, MOVE, DIFF");
            return;
        }

        // 引数をパース
        String src     = args[0];
        String dest    = args[1];
        String modeStr = args[2].toUpperCase();

        try {
            // モード文字列をenumに変換
            OperationMode mode = OperationMode.valueOf(modeStr);

            // ディレクトリ操作を実行
            directoryService.processDirectory(src, dest, mode);
            System.out.println("Operation completed successfully");
        } catch (IllegalArgumentException e) {
            // 無効なモードが指定された場合
            System.out.println("Invalid mode: " + modeStr);
            System.out.println("Valid modes are: COPY, MOVE, DIFF");
        } catch (IOException e) {
            // ディレクトリ操作中にエラーが発生した場合
            System.out.println("Error: " + e.getMessage());
        }
    }
}
