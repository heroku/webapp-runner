package webapp.runner.launch.valves;

import org.apache.catalina.valves.AbstractAccessLogValve;

import java.io.CharArrayWriter;

public class StdoutAccessLogValve extends AbstractAccessLogValve {
  @Override
  public void log(CharArrayWriter message) {
    synchronized (this) {
      System.out.println(message.toCharArray());
    }
  }
}
