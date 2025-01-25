package kmg.tool.directorytool.infrastructure.types;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 終了コードの種類<br>
 * <p>
 * ディレクトリ操作ツールの終了コードを表す列挙型。<br>
 * </p>
 *
 * @author kmg
 * @version 1.0
 */
public enum ExitCodeTypes implements Supplier<Integer> {

    /* 定義：開始 */

    /** 指定無し */
    NONE("指定無し", -1),

    /** 正常 */
    SUCCESS("正常", 0),

    /** 引数エラー */
    ARGUMENT_ERROR("引数エラー", 1),

    /** 想定エラー */
    EXPECTED_ERROR("想定エラー", 2),

    /** 想定外エラー */
    UNEXPECTED_ERROR("想定外エラー", 3),

    /* 定義：終了 */
    ;

    /** 名称 */
    private final String name;

    /** 値 */
    private final int value;

    /** 種類のマップ */
    private static final Map<Integer, ExitCodeTypes> VALUES_MAP = new HashMap<>();

    static {

        /* 種類のマップにプット */
        for (final ExitCodeTypes type : ExitCodeTypes.values()) {

            ExitCodeTypes.VALUES_MAP.put(type.get(), type);

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
    ExitCodeTypes(final String name, final int value) {

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
    public static ExitCodeTypes getEnum(final int value) {

        ExitCodeTypes result = ExitCodeTypes.VALUES_MAP.get(value);

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
    public static ExitCodeTypes getInitValue() {

        final ExitCodeTypes result = NONE;
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
    public static ExitCodeTypes getDefault() {

        final ExitCodeTypes result = NONE;
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

        final String result = String.valueOf(this.value);
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
    public int getValue() {

        final int result = this.value;
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
    public Integer get() {

        final Integer result = this.value;
        return result;

    }
}
