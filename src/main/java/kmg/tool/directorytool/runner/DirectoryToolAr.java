package kmg.tool.directorytool.runner;

import java.io.IOException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import kmg.tool.directorytool.model.OperationMode;
import kmg.tool.directorytool.service.DirectoryService;

/**
 * コマンドラインインターフェースを提供するクラス。 Spring Bootのコマンドラインランナーとして実装され、アプリケーションの起動時に コマンドライン引数を処理し、適切なディレクトリ操作を実行する。
 * <p>
 * 基本的な使用方法：
 *
 * <pre>
 * java -jar directory-tool.jar <src> <dest> <mode>
 * </pre>
 * <p>
 * パラメータ：
 * <ul>
 * <li><src> - 操作対象のソースディレクトリパス
 * <li><dest> - 操作対象のターゲットディレクトリパス
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
public class DirectoryToolAr implements ApplicationRunner, ExitCodeGenerator, ExitCodeExceptionMapper {

    /** ディレクトリ操作サービス。 Spring DIコンテナによって注入される {@link DirectoryService} のインスタンス。 */
    @Autowired
    private DirectoryService directoryService;

    /** 終了コード */
    private int exitCode;

    /**
     * 基本コンストラクタ。 Spring Bootのコンテナによって自動的にインスタンス化される。
     */
    public DirectoryToolAr() {
        this.exitCode = 0;      // TODO 2025/01/18 列挙型で定義する
    }

    /**
     * 終了コードを返す。 このメソッドはSpring Bootによって自動的 に呼び出される。
     */
    @Override
    public int getExitCode() {
        final int result = this.exitCode;
        return result;
    }

    /**
     * 例外を受け取り、終了コードを返す。 このメソッドはSpring Bootによって自動的に呼び出される。
     */
    @Override
    public int getExitCode(final Throwable exception) {
        final int result = 2;
        return result;
    }

    /**
     * ディレクトリ操作を実行するメイン処理。
     * <p>
     * コマンドライン引数を解析し、指定されたディレクトリ操作を実行する。
     * </p>
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
    public void run(final ApplicationArguments args) throws Exception {
        // 非オプション引数を取得
        final String[] nonOptionArgs = args.getNonOptionArgs().toArray(new String[0]);

        // 引数の数をチェック
        if (nonOptionArgs.length != 3) {
            System.out.println("Usage: java -jar directory-tool.jar <src> <dest> <mode>");
            System.out.println("Modes: COPY, MOVE, DIFF");
            return;
        }

        // 引数をパース
        final String src     = nonOptionArgs[0];
        final String dest    = nonOptionArgs[1];
        final String modeStr = nonOptionArgs[2].toUpperCase();

        try {
            // モード文字列をenumに変換
            final OperationMode mode = OperationMode.valueOf(modeStr);

            // ディレクトリ操作を実行
            this.directoryService.processDirectory(src, dest, mode);
            System.out.println("Operation completed successfully");
        } catch (final IllegalArgumentException e) {
            // 無効なモードが指定された場合
            this.exitCode = 1;
            System.out.println("Invalid mode: " + modeStr);
            System.out.println("Valid modes are: COPY, MOVE, DIFF");
            e.printStackTrace();
        } catch (final IOException e) {
            // ディレクトリ操作中にエラーが発生した場合
            this.exitCode = 1;
            e.printStackTrace();
        }
    }
}
