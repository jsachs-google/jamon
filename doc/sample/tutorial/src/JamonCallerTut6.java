/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
import java.io.OutputStreamWriter;

public class JamonCallerTut6 {
  public static void main(String[] argv) throws Exception {
    String[] names = new String[] {
      "John Public", "Mary Private", "Lee Protected"};
    String[] phoneNumbers = new String[] {
      "5550324", "4135559232", "4135551212" };
    new JamonCallerTemplate()
      .render(new OutputStreamWriter(System.out), names, phoneNumbers);
  }
}
