/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
import java.io.OutputStreamWriter;
import java.util.Date;
import java.math.BigDecimal;

public class AccountSummaryTut3 {
  public static void main(String[] argv) throws Exception {
    new AccountSummaryTemplate()
      .setTitle("Most Recent Account Balances")
      .render(new OutputStreamWriter(System.out),
              new Date(), "John Public", new BigDecimal(9.99));
  }
}
