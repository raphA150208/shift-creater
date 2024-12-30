
## 10回目課題-1: CREATE処理-READ処理-UPDATE処理-DELETE処理

### 目次
1. プロジェクト名
2. プロジェクトの概要
3. 環境
4. 環境構築の手順
5. 作成の流れ
6. エラー
7. 学んだこと

### 1. プロジェクト名
- staff

### 2. プロジェクトの概要
- 名前・生年月日・最寄り駅を要素に持った、staffテーブルへの登録処理の実装

### 3. 環境
- Windowsのバージョン: Windows11
- IntelliJ IDEA 17
- Spring Boot 3.2.4

### 4. 環境構築の手順
1. Spring InitializrにてSpring Bootアプリケーションのひな型を作成
    - Project: Gradle-8.7
    - Language: Java17.0.8
    - Dependencies: Spring Web, MySQL Driver, MyBatis Framework
2. ダウンロードしたファイルを解凍し、IntelliJで起動
3. SQLファイルにテーブルのデータを記載
4. `docker-compose.yml`ファイルに`container_name`を設定
5. Dockerを起動
6. CREATE処理の実装
    - `create-table-and-load-data.sql`ファイルで、`INSERT INTO`の後がテーブル名になっていなかったため、テーブルにレコードが正しく登録されていなかった。
    - Dockerを立ち上げ忘れてSpring Bootを起動して、サーバーエラーになる。
7.READ処理の実装
    - 課題9で作ったものを元にfindByIdの依存関係を再確認しながら追記していく
    - Service、Mapper、Controllerに記載
### 5. 作成の流れ
1. 最終課題のCRUD機能を作るにあたって、最終的に作りたいアプリケーションの概要から考え、必要な機能を書き出し、それに沿ってCREATE処理の実装を行いました。
2. 全体的なスケジュールを把握するため、GitHubのProjectにissueとRoadMapを作成。
3. Mapper, Controller, Service, NotFoundException, Application, Rosterクラスを作成。
4. POSTMANでの動作確認   
   <br>![image](https://github.com/hiro903/Kadai10/assets/145466271/c7699188-6418-4439-a386-c21a2fa97bad)
   <br>    ![image-1](https://github.com/hiro903/Kadai10/assets/145466271/0db63fcf-47c4-472e-858e-46cc56f66221)
   <br> ![image](https://github.com/hiro903/Kadai10/assets/145466271/b496c111-3f39-4972-b03b-593db26cf244)
　　<br>![image](https://github.com/hiro903/Kadai10/assets/145466271/9f9b2c2d-24fb-4ecc-98d6-3910c7a0f4ae)
   <br> ![image](https://github.com/hiro903/Kadai10/assets/145466271/98495fff-5422-4505-83d9-a6c9fba665dd)
　　<br>![image](https://github.com/hiro903/Kadai10/assets/145466271/a53e74c6-f689-41f5-98ef-1ba2156521e7)
　　<br>![image](https://github.com/hiro903/Kadai10/assets/145466271/6e34ecd0-3b02-4161-96ba-84b0614b3489)
　　<br>![image](https://github.com/hiro903/Kadai10/assets/145466271/0c154276-83c1-4df2-8e9d-611c57c61185)
　　<br>![image](https://github.com/hiro903/Kadai10/assets/145466271/0546b34b-7d9e-4773-a843-2485126fac53)
### 6. エラー
1. リクエストを投げたら、500サーバーエラーが返ってきてしまう。コードエラーは直していたので、問題の箇所を見つけるのに時間がかかった。
   >カラムでもたせていた「date_of_birth」がRosterクラスではgetDateOfBirth()メソッドが提供されていたが、MapperクラスのINSERT文ではスネークケースで記載されていたので、リクエストを投げた時にマッピングできなかった。エンティティのフィールド名、リクエストを投げる時は、キャメルケースにすること。（※「nearest_station」はなぜかエラーにならなかった）

2. Gradleがダウンロードされていない、またはGradleが対応していないというエラーで、Spring Bootが起動できない。コードにエラーは出ていなかった。
   >Gradleファイルなどを確認して依存関係にあるか調べたが、きちんと記載されていた。ビルドの再読み込みをしたら改善した。
### 7. 学んだこと
1. **SQLのテーブル定義で各要素にnull制約をかけている場合でも、Javaのコードで対応することが推奨される**
   Javaのコードでnull値をチェックしてバリデーションを行うことは、セキュリティやシステムの信頼性を高めるために重要。そのため、SQLのテーブル定義で制約を設けていても、Javaのコードでのバリデーションを行うことが推奨される。
   <br>
    - クライアント側でのバリデーションはセキュリティ上の観点から必要。サーバーサイドのバリデーションのみでは、不正なデータがデータベースに挿入される可能性がある。そのため、クライアント側でもバリデーションを行い、不正なデータの送信を防ぐことが重要。
      <br>
    - クライアント側でのバリデーションを行うことで、フロントエンドとバックエンドの分離ができる。フロントエンドではユーザーが入力したデータのバリデーションを行い、バックエンドではそのデータを受け取ってから再度バリデーションを行う。この分離により、システム全体が柔軟性が向上し、メンテナンスや拡張が容易にできる。
      <br>
    - バックエンドでのバリデーションを行うことで、データベースに不正なデータが挿入されることを防ぐ。これにより、データベース側でのエラーが発生する可能性が低くなり、エラーハンドリングが簡素化される。

2. **Mapperクラスにおけるバリデーション**
    - 通常、SQLクエリの実行やデータのマッピングが主な役割だが、必要に応じてバリデーションロジックを追加する。

    - Insert文の中に書くビジネスロジックは簡素なものに留める。
      基本的なデータ整合性を確保するためのバリデーション（整合性を担保する基本的なチェック）は、Mapperに書くことが適切。高度なビジネスロジックや複雑なバリデーションはサービス層で行う。

3. **バリデーションを追加する場合、注意すること**
    1. **責務の明確化**:
       バリデーションはデータアクセス層で行うものではないため、Mapperクラスにバリデーションを追加する場合は、ビジネスロジックとの明確な分離を確保する必要がある。バリデーションが複雑化する場合は、サービス層に移動し、責務を明確にする。

    2.  **保守性**:
        ビジネスロジックが変更された場合、それに伴うバリデーションの変更もMapperクラスにある場合は修正が必要になる。このような場合、関連するコードを特定しやすくするために、適切なコメントやドキュメントを追加することが重要。

    3.  **拡張性**:
        ビジネスロジックが複雑化し、追加のバリデーションが必要になった場合、Mapperクラスにあるバリデーションコードが増えることになる。このような場合、コードの再利用性や拡張性を向上させるために、バリデーションロジックをサービス層に移動することが望ましい。

4. **Insert文に記載したnullチェック**
      ```java:
         @Insert({"<script>",
              "INSERT INTO staff (name, date_of_birth, nearest_station)",
              "VALUES ( ",
              "<if test='name != null'>#{name},</if>",
              "<if test='dateOfBirth != null'>#{dateOfBirth},</if>",
              "<if test='nearestStation != null'>#{nearestStation}</if>",
              "</script>"})
   ```
    - `@Insert`アノテーション:
      MyBatisによって、このメソッドがデータベースに対して`INSERT`クエリを実行することを示す。
    -   `"<script>"`タグ:
        MyBatisの動的SQLを記述するための開始タグです。このタグで囲まれた部分は、動的なSQLが記述される。
    -   `INSERT INTO staff (name, date_of_birth, nearest_station)`:
        実行されるSQLクエリの基本的な構造を定義する。`staff`テーブルに`name`、`date_of_birth`、`nearest_station`の値を挿入。
    -   `<if>`タグ:
        MyBatisの動的SQLの制御フローを提供するためのタグ。この場合、`dateOfBirth`プロパティが`null`でない場合にのみ含まれる部分を挿入する。
    -   `test='dateOfBirth != null'`:
        `dateOfBirth`プロパティが`null`でないことをチェックする条件。
    -   `#{dateOfBirth},`:
        `dateOfBirth`プロパティが`null`でない場合に、実際の値を挿入する。
    -   `#{nearest_station}`:
        `nearest_station`が`null`でない場合に、実際の値を挿入する。
    -   `"</script>"`タグ:
MyBatisの動的SQLを記述するための終了タグ。
<br> ※上記はnullでない値を持つことが保証されている場合にのみ適切。
もしデータベースのスキーマがnullを許容していないのであれば、その点を考慮する必要がある。
5. **更新処理において、テーブル定義が**<br>
- name VARCHAR(255) NOT NULL,<br>
          date_of_birth DATE NOT NULL,<br>
        nearest_station VARCHAR(255) NOT NULL,<br>の場合
    - ![image](https://github.com/hiro903/Kadai10/assets/145466271/c7c8d1a4-3965-476e-a30d-bcaff6d06bfd)
　　　　””とリクエストを投げると「何もない」にカウントされてしまう。
    - ![image](https://github.com/hiro903/Kadai10/assets/145466271/055ffca9-0f29-4d82-91e4-7c671d15d956)
   　　![image](https://github.com/hiro903/Kadai10/assets/145466271/288bf49b-df1c-4c40-927d-66d406b05e84)
   　　データ型が文字列型の場合、nullとリクエストを投げないとデータベースが空になってしまう。
    - ![image](https://github.com/hiro903/Kadai10/assets/145466271/bfcb2a98-1167-471c-a2c7-fd20121faf8c)
6.**name != null と Objects.nonNull(name) の違い**
    - name != null:
      nameがnullでない場合に条件を満たしnameがnullであるかどうかを直接確認する。
    - Objects.nonNull(name):
      ObjectクラスのnonNullメソッドを使用。このメソッドはJava 8から導入された。
      nonNullメソッドは、渡されたオブジェクトがnullでない場合にtrueを返し、それ以外の場合にfalseを返す。
      nonNullメソッドは、オブジェクトがnullでないことを明示的に示すため、可読性が向上する。Object.nonNull(name)はnonNullメソッドを使用しているため、オブジェクトがnullでないことを明示的に示す。
