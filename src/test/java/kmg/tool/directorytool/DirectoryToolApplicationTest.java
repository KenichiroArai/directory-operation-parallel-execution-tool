package kmg.tool.directorytool;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.context.SpringBootTest;
import java.nio.file.Path;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class DirectoryToolApplicationTest {

    @Test
    void contextLoads() {
        // Spring Contextが正常にロードされることを確認
    }

    @Test
    void mainMethodExecutesSuccessfullyInTestMode(@TempDir Path tempDir) {
        // テストモードを設定
        DirectoryToolApplication.setTestMode(true);

        // mainメソッドを実行（一時ディレクトリを使用）
        DirectoryToolApplication.main(new String[] {"src", tempDir.resolve("dest").toString(), "COPY"});
    }

    @Test
    void setTestModeTogglesBehavior() {
        // テストモードの設定を確認
        DirectoryToolApplication.setTestMode(true);
        assertTrue(DirectoryToolApplication.isTestMode());

        DirectoryToolApplication.setTestMode(false);
        assertFalse(DirectoryToolApplication.isTestMode());
    }
}
