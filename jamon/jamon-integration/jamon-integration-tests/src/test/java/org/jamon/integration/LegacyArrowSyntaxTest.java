/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.integration;

import test.jamon.LegacyArrowSyntax;

public class LegacyArrowSyntaxTest extends TestBase {
  public void testExercise() throws Exception {
    new LegacyArrowSyntax().render(getWriter());
    checkOutput("simple simple simple\n12");
  }
}
