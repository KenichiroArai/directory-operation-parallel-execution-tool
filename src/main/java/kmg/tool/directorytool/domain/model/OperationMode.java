package kmg.tool.directorytool.domain.model;

/**
 * ディレクトリ操作ツールの操作モードを表す列挙型。<br>
 * <p>
 * ツールがファイルに対して行う操作の種類を定義する。<br>
 * このenumは、コマンドライン引数として受け取った操作タイプをアプリケーション内部で扱うための型安全な表現を提供する。
 * </p>
 *
 * @author kmg
 * @version 1.0
 * @see kmg.tool.directorytool.DirectoryToolApplication
 */
public enum OperationMode {

    /** ファイルをコピーする操作モード。元のファイルを保持したまま、指定された場所に複製を作成する。 */
    COPY,

    /** ファイルを移動する操作モード。元のファイルを指定された場所に移動し、元の場所からは削除する。 */
    MOVE,

    /** ファイルの差分を出力する操作モード。ソースディレクトリとターゲットディレクトリのファイルの差分を出力する。 */
    DIFF
}
