package kmg.tool.directorytool.presentation.runner;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import kmg.tool.directorytool.domain.service.DirectoryService;
import kmg.tool.directorytool.infrastructure.types.ExitCodeTypes;
import kmg.tool.directorytool.infrastructure.types.OperationModeTypes;

/**
 * コマンドラインインターフェースを提供するクラス。 Spring Bootのコマンドラインランナーとして実装され、アプリケーションの起動時に コマンドライン引数を処理し、適切なディレクトリ操作を実行する。
 * <p>
 * 基本的な使用方法：
 *
 * <pre>
 * java -jar directory-tool.jar <mode> <src> <dest>
 * </pre>
 * <p>
 * パラメータ：
 * <ul>
 * <li><mode> - 操作モード (COPY, MOVE, DIFF)
 * <li><src> - 操作対象のソースディレクトリパス
 * <li><dest> - 操作対象のターゲットディレクトリパス
 * </ul>
 * <p>
 * 使用例：
 *
 * <pre>
 * # ディレクトリのコピー
 * java -jar directory-tool.jar COPY /source/dir /target/dir
 *
 * # ディレクトリの移動
 * java -jar directory-tool.jar MOVE /source/dir /target/dir
 *
 * # ディレクトリの差分比較
 * java -jar directory-tool.jar DIFF /source/dir /target/dir
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
 */
@Component
public class DirectoryToolAr implements ApplicationRunner, ExitCodeGenerator, ExitCodeExceptionMapper {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(DirectoryToolAr.class);

    /** ディレクトリ操作サービス */
    @Autowired
    private DirectoryService directoryService;

    /** 終了コードの種類 */
    private ExitCodeTypes exitCode;

    /**
     * デフォルトコンストラクタ。
     */
    public DirectoryToolAr() {

        this.exitCode = ExitCodeTypes.SUCCESS;

    }

    /**
     * 終了コードを返す。
     */
    @Override
    public int getExitCode() {

        final int result = this.exitCode.getValue();
        return result;

    }

    /**
     * 例外を受け取り、終了コードを返す。
     */
    @Override
    public int getExitCode(final Throwable exception) {

        final int result = ExitCodeTypes.UNEXPECTED_ERROR.getValue();
        DirectoryToolAr.logger.error("例外を受け取りました。", exception);
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
     *             <li>args[0]: 操作モード (COPY, MOVE, DIFF)
     *             <li>args[1]: ソースディレクトリパス
     *             <li>args[2]: ターゲットディレクトリパス
     *             </ul>
     */
    @Override
    public void run(final ApplicationArguments args) {

        /* 引数の変換 */

        // スレッドプールサイズのオプションを取得
        int threadPoolSize = 0;

        if (args.containsOption("thread-pool-size")) {

            try {

                threadPoolSize = Integer.parseInt(args.getOptionValues("thread-pool-size").get(0));

            } catch (final NumberFormatException e) {

                DirectoryToolAr.logger.error("スレッドプールサイズは数値で指定してください。", e);
                this.exitCode = ExitCodeTypes.ARGUMENT_ERROR;
                return;

            }

        }

        // 非オプション引数を取得
        final String[] nonOptionArgs = args.getNonOptionArgs().toArray(String[]::new);

        // 引数の数をチェック
        if (nonOptionArgs.length != 3) {

            DirectoryToolAr.logger.error("使用方法: [--thread-pool-size=<size>] <mode> <src> <dest>");
            DirectoryToolAr.logger.error("モデルの種類: COPY, MOVE, DIFF");
            DirectoryToolAr.logger.error("オプション:");
            DirectoryToolAr.logger.error("  --thread-pool-size=<size>  並列処理で使用するスレッド数（デフォルト: 利用可能なCPUの論理コア数）");

            this.exitCode = ExitCodeTypes.ARGUMENT_ERROR;
            return;

        }

        // 引数をパース
        // モード
        final String       modeStr = nonOptionArgs[0];
        OperationModeTypes operationModeTypes;

        try {

            // モード文字列をenumに変換
            operationModeTypes = OperationModeTypes.valueOf(modeStr.toUpperCase());

        } catch (final IllegalArgumentException e) {

            // 無効なモードが指定された場合
            this.exitCode = ExitCodeTypes.ARGUMENT_ERROR;

            final String[] logMsgs = {
                    String.format("無効なモードが選択されています。: [%s]", modeStr), "有効なモードの種類: COPY, MOVE, DIFF",
            };
            final String   logMsg  = String.join(System.lineSeparator(), logMsgs);
            DirectoryToolAr.logger.error(logMsg, e);
            return;

        }
        // ソースディレクトリパス
        final String src = nonOptionArgs[1];
        // ターゲットディレクトリパス
        final String dest = nonOptionArgs[2];

        /* ディレクトリ操作を実行 */
        try {

            // スレッドプールサイズを設定（設定されている場合のみ）
            this.directoryService.setThreadPoolSize(threadPoolSize);

            this.directoryService.processDirectory(src, dest, operationModeTypes);
            DirectoryToolAr.logger.info("ディレクトリ操作の処理が終了しました。");

        } catch (final IOException e) {

            // ディレクトリ操作中にエラーが発生した場合
            this.exitCode = ExitCodeTypes.EXPECTED_ERROR;
            DirectoryToolAr.logger.error("ディレクトリ操作エラー", e);

        }

    }
}
