/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package i18n;

import java.util.Locale;

import org.jamon.AbstractTemplateProxy;
import org.jamon.AbstractTemplateReplacer;
import org.jamon.AbstractTemplateProxy.ReplacementConstructor;
import org.jamon.annotations.Replaces;

public class LocalizingTemplateReplacer extends AbstractTemplateReplacer {
  @Override
  protected ReplacementConstructor findReplacement(
    Class<? extends AbstractTemplateProxy> proxyClass, Object jamonContext) {
    if (!(jamonContext instanceof Locale)) {
      return null;
    }
    else {
      String lang = ((Locale) jamonContext).getLanguage();
      System.err.println("locale is " + jamonContext + ", lang is " + lang);
      try {
        return Class.forName(proxyClass.getName() + "_" + lang).getAnnotation(Replaces.class)
            .replacementConstructor().newInstance();
      }
      catch (Exception e) {
        return null;
      }
    }
  }
}
