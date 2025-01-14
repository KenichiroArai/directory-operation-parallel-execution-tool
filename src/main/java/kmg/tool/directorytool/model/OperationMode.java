package kmg.tool.directorytool.model;

/**
 * ディレクトリ操作ツールの操作モードを表す列挙型。
 * ツールがファイルに対して行う操作の種類を定義する。
 */
public enum OperationMode {
    /**
     * ファイルをコピーする操作モード。
     * 元のファイルを保持したまま、指定された場所に複製を作成する。
     */
    COPY,

    /**
     * ファイルを移動する操作モード。
     * 元のファイルを指定された場所に移動し、元の場所からは削除する。
     */
    MOVE,

    /**
     * ファイルの差分を出力する操作モード。
     * ソースディレクトリとターゲットディレクトリのファイルの差分を出力する。
     */
    DIFF
}
