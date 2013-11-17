/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
import java.io.OutputStreamWriter;
import java.util.Date;

public class ClassExampleTut5 {
  public static void main(String[] argv) throws Exception {
    new ClassExampleTemplate()
      .render(new OutputStreamWriter(System.out), new Date(), 3);
  }
}
