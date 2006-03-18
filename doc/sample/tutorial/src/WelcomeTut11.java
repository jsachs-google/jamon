import java.io.OutputStreamWriter;

public class WelcomeTut11 {
  public static void main(String[] argv) throws Exception {
    MyContext context = new MyContext();
    context.setSecure(true);
    new WelcomeTemplate()
      .setJamonContext(context)
      .render(new OutputStreamWriter(System.out));
  }
}
