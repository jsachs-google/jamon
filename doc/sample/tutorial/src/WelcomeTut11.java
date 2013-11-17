/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
