/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
import java.io.OutputStreamWriter;

public class SubtemplateTut7 {
  public static void main(String[] argv) throws Exception {
    // data to pass to the template
    String[] accountNames = new String[] {
      "John Doe", "Mary Jane", "Bonnie Blue", "Johnny Reb" };
    String accountInfoUrl = "http://www.bank.com/accountInfo";
    // call the template ...
    new SubtemplateTemplate()
      .setCgiParamName("username")
      .render(new OutputStreamWriter(System.out),
              accountNames, accountInfoUrl);
  }
}
