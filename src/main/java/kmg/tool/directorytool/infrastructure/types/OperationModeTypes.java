package kmg.tool.directorytool.infrastructure.types;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 操作モードの種類<br>
 * <p>
 * ディレクトリ操作ツールの操作モードを表す列挙型。<br>
 * ツールがファイルに対して行う操作の種類を定義する。<br>
 * このenumは、コマンドライン引数として受け取った操作タイプをアプリケーション内部で扱うための型安全な表現を提供する。
 * </p>
 *
 * @author kmg
 * @version 1.0
 */
public enum OperationModeTypes implements Supplier<String> {

    /* 定義：開始 */

    /** 指定無し */
    NONE("指定無し", null),

    /** コピー。ファイルをコピーする操作モード。元のファイルを保持したまま、指定された場所に複製を作成する。 */
    COPY("コピー", "COPY"),

    /** 移動。ファイルを移動する操作モード。元のファイルを指定された場所に移動し、元の場所からは削除する。 */
    MOVE("移動", "MOVE"),

    /** 差分。ファイルの差分を出力する操作モード。ソースディレクトリとターゲットディレクトリのファイルの差分を出力する。 */
    DIFF("差分", "DIFF"),

    /* 定義：終了 */
    ;

    /** 名称 */
    private final String name;

    /** 値 */
    private final String value;

    /** 種類のマップ */
    private static final Map<String, OperationModeTypes> VALUES_MAP = new HashMap<>();

    static {

        /* 種類のマップにプット */
        for (final OperationModeTypes type : OperationModeTypes.values()) {

            OperationModeTypes.VALUES_MAP.put(type.get(), type);

        }

    }

    /**
     * コンストラクタ<br>
     *
     * @author KenichiroArai
     * @sine 1.0.0
     * @version 1.0.0
     * @param name
     *              名称
     * @param value
     *              値
     */
    OperationModeTypes(final String name, final String value) {

        this.name = name;
        this.value = value;

    }

    /**
     * 値に該当する種類を返す<br>
     * <p>
     * 但し、値が存在しない場合は、指定無し（NONE）を返す。
     * </p>
     *
     * @author KenichiroArai
     * @sine 1.0.0
     * @version 1.0.0
     * @param value
     *              値
     * @return 種類。指定無し（NONE）：値が存在しない場合。
     */
    public static OperationModeTypes getEnum(final String value) {

        OperationModeTypes result = OperationModeTypes.VALUES_MAP.get(value);

        if (result == null) {

            result = NONE;

        }
        return result;

    }

    /**
     * 初期値の種類を返す<br>
     *
     * @author KenichiroArai
     * @sine 1.0.0
     * @version 1.0.0
     * @return 初期値
     */
    public static OperationModeTypes getInitValue() {

        final OperationModeTypes result = NONE;
        return result;

    }

    /**
     * デフォルトの種類を返す<br>
     *
     * @author KenichiroArai
     * @sine 1.0.0
     * @version 1.0.0
     * @return デフォルト値
     */
    public static OperationModeTypes getDefault() {

        final OperationModeTypes result = NONE;
        return result;

    }

    /**
     * 値を返す<br>
     *
     * @author KenichiroArai
     * @sine 1.0.0
     * @version 1.0.0
     * @return 値
     */
    @Override
    public String toString() {

        final String result = this.value;
        return result;

    }

    /**
     * 名称を返す<br>
     *
     * @author KenichiroArai
     * @sine 1.0.0
     * @version 1.0.0
     * @return 名称
     */
    public String getName() {

        final String result = this.name;
        return result;

    }

    /**
     * 値を返す<br>
     *
     * @author KenichiroArai
     * @sine 1.0.0
     * @version 1.0.0
     * @return 値
     */
    public String getValue() {

        final String result = this.value;
        return result;

    }

    /**
     * 種類の値<br>
     *
     * @author KenichiroArai
     * @sine 1.0.0
     * @version 1.0.0
     * @return 種類の値
     */
    @Override
    public String get() {

        final String result = this.value;
        return result;

    }
}
