/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jamon.api.Location;
import org.jamon.compiler.ParserErrorsImpl;
import org.jamon.node.AbstractNode;
import org.jamon.node.ArgNameNode;
import org.jamon.node.ArgValueNode;
import org.jamon.node.ParentArgNode;
import org.jamon.node.ParentArgWithDefaultNode;
import org.jamon.node.ParentArgsNode;
import org.junit.Test;

public class ParentArgsParserTest extends AbstractParserTest {
  private static String PARENT_ARGS_START = ">";

  private static String PARENT_ARGS_END = "</%xargs>";

  @Override
  protected AbstractNode parse(String text) throws IOException {
    final PositionalPushbackReader reader = makeReader(text);
    ParserErrorsImpl errors = new ParserErrorsImpl();
    ParentArgsNode result = new ParentArgsParser(reader, errors, START_LOC).getParentArgsNode();
    if (errors.hasErrors()) {
      throw errors;
    }
    else {
      return result;
    }
  }

  private static ParentArgsNode parentArgsNode() {
    return new ParentArgsNode(START_LOC);
  }

  private static ParentArgNode parentArgNode(int line, int column, String name) {
    Location location = location(line, column);
    return new ParentArgNode(location, new ArgNameNode(location, name));
  }

  private static ParentArgNode parentArgWithDefaultNode(
    int nameLine, int nameColumn, String name, int valueLine, int valueColumn, String value) {
    Location location = location(nameLine, nameColumn);
    return new ParentArgWithDefaultNode(
      location,
      new ArgNameNode(location, name),
      new ArgValueNode(location(valueLine, valueColumn), value));
  }

  @Test
  public void testNoXargs() throws Exception {
    assertEquals(parentArgsNode(), parse(PARENT_ARGS_START + PARENT_ARGS_END));
    assertEquals(parentArgsNode(), parse(PARENT_ARGS_START + " \t\r\n" + PARENT_ARGS_END));
  }

  @Test
  public void testSomeXargs() throws Exception {
    assertEquals(
      parentArgsNode()
        .addArg(parentArgNode(1, 2, "foo"))
        .addArg(parentArgNode(2, 1, "bar")),
      parse(PARENT_ARGS_START + "foo;\nbar ; " + PARENT_ARGS_END));
  }

  @Test
  public void testOptionalXarg() throws Exception {
    assertEquals(
      parentArgsNode().addArg(parentArgWithDefaultNode(1, 2, "x", 1, 4, "3")),
      parse(PARENT_ARGS_START + "x=3;" + PARENT_ARGS_END));
  }

  @Test
  public void testNoSemiAfterXarg() throws Exception {
    assertError(
      PARENT_ARGS_START + "x" + PARENT_ARGS_END,
      1, 3, OptionalValueTagEndDetector.NEED_SEMI_OR_ARROW);
  }

  @Test
  public void testNoSemiAfterXargWithDefault() throws Exception {
    assertError(
      PARENT_ARGS_START + "x=3" + PARENT_ARGS_END,
      1, 4, AbstractParser.eofErrorMessage(";"));
  }

  @Test
  public void testXargWithSimpleType() throws Exception {
    assertError(
      PARENT_ARGS_START + "a c;" + PARENT_ARGS_END,
      1, 4, OptionalValueTagEndDetector.NEED_SEMI_OR_ARROW);
  }

  @Test
  public void testXargWithFullyScopedType() throws Exception {
    assertError(
      PARENT_ARGS_START + "a.b c;" + PARENT_ARGS_END,
      1, 3, OptionalValueTagEndDetector.NEED_SEMI_OR_ARROW);
  }

  public static junit.framework.Test suite() {
    return new junit.framework.JUnit4TestAdapter(ParentArgsParserTest.class);
  }
}
