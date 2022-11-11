package webapp.runner.launch;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Ryan Brainard
 */
public class TomcatBaseDirResolutionTest {

  private static final Integer PORT = 1234;
  private static final File BASE_DIR =
      new File(System.getProperty("user.dir"), new File("target", "tomcat." + PORT).toString());

  @BeforeMethod
  @AfterTest
  public void clean() {
    if (BASE_DIR.exists()) {
      assertTrue(BASE_DIR.delete());
    }
  }

  @Test
  public void testBaseDirNotExists() throws Exception {
    Main.resolveTomcatBaseDir(PORT, null);
    assertTrue(BASE_DIR.isDirectory());
  }

  @Test
  public void testBaseDirAlreadyExists() throws Exception {
    assertTrue(BASE_DIR.mkdirs());
    Main.resolveTomcatBaseDir(PORT, null);
    assertTrue(BASE_DIR.isDirectory());
  }

  @Test
  public void testBaseDirAlreadyExistsAsFile() throws Exception {
    BASE_DIR.getParentFile().mkdirs();
    new PrintWriter(BASE_DIR).append("");
    assertTrue(BASE_DIR.isFile());

    try {
      Main.resolveTomcatBaseDir(PORT, null);
      fail();
    } catch (IOException e) {
      // expected
    }

    assertTrue(BASE_DIR.isFile());
  }

  @Test
  public void testSuggestBaseDir() throws IOException {

    String suggest = System.getProperty("user.dir") + File.separator + "temp";
    String tomcatBaseDir = Main.resolveTomcatBaseDir(PORT, suggest);

    assertTrue(tomcatBaseDir.equals(suggest));

    File dir = new File(tomcatBaseDir);
    if (dir.exists()) {
      dir.delete();
    }
  }
}
