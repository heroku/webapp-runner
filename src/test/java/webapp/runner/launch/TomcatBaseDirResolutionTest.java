package webapp.runner.launch;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

import static org.testng.Assert.*;

/**
 * @author Ryan Brainard
 */
public class TomcatBaseDirResolutionTest {

    private static final Integer PORT = 1234;
    private static final File BASE_DIR = new File(System.getProperty("user.dir"), "/target/tomcat." + PORT);

    @BeforeMethod
    @AfterTest
    public void clean() throws IOException {
        if (BASE_DIR.exists()) {
            if(BASE_DIR.isDirectory()) {
              FileUtils.deleteDirectory(BASE_DIR);
            } else {
              BASE_DIR.delete();
            }
        }
    }

    @Test
    public void testBaseDirNotExists() throws Exception {
        Main.resolveTomcatBaseDir(PORT);
        assertTrue(BASE_DIR.isDirectory());
    }

    @Test
    public void testBaseDirAlreadyExists() throws Exception {
        assertTrue(BASE_DIR.mkdirs());
        Main.resolveTomcatBaseDir(PORT);
        assertTrue(BASE_DIR.isDirectory());
    }

    @Test
    public void testBaseDirAlreadyExistsAsFile() throws Exception {
        BASE_DIR.getParentFile().mkdirs();
        PrintWriter printWriter = new PrintWriter(BASE_DIR);
        printWriter.append("");
        printWriter.close();
        assertTrue(BASE_DIR.isFile());

        try {
            Main.resolveTomcatBaseDir(PORT);
            fail();
        } catch (IOException e) {
            // expected
        }

        assertTrue(BASE_DIR.isFile());
    }

}
