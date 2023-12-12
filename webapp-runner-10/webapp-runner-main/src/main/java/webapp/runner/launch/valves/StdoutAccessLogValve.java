package webapp.runner.launch.valves;

import java.io.CharArrayWriter;
import org.apache.catalina.valves.AbstractAccessLogValve;

public class StdoutAccessLogValve extends AbstractAccessLogValve {
  @Override
  public void log(CharArrayWriter message) {
    synchronized (this) {
      System.out.println(message.toCharArray());
    }
  }
}
