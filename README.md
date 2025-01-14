# ディレクトリ操作並行実行ツール

https://github.com/KenichiroArai/directory-operation-parallel-execution-tool.git

ディレクトリ操作を並行出来るようにします。

下記のモードがあります。

* MOVE：srcからdestに移動します。
* COPY：srcからdestにコピーします。
* DIFF：srcとdestを比較します。

# 開発環境の構築手順

## パッケージ化

### テスト実施する

mvn package

### テストスキップする
mvn package -DskipTests

### 実行例

引数にsrc、dest、モードを指定する。

java -jar target\directory-tool-1.0.0.jar C:\dev\wk\src C:\dev\wk\dest DIFF
