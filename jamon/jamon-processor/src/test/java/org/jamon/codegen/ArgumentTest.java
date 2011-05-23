package org.jamon.codegen;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArgumentTest {
  private static final TemplateUnit TEMPLATE_UNIT =
    new TemplateUnit("org/jamon/Template", null);

  @Test
  public void testGetFullyQualifiedTypeForRequiredArg() {
    assertEquals("bar", new RequiredArgument("foo", "bar", null).getFullyQualifiedType());
  }

  @Test
  public void testGetFullyQualifiedTypeForTopLevelFragmentArg() {
    FragmentUnit fragmentUnit = new FragmentUnit(
      "frag", TEMPLATE_UNIT, new GenericParams(), null, null);
    FragmentArgument fragmentArgument = new FragmentArgument(fragmentUnit, null);
    assertEquals("org.jamon.Template.Intf.Fragment_frag", fragmentArgument.getFullyQualifiedType());
  }

  @Test
  public void testGetFullyQualifiedTypeForMethodFragmentArg() {
    FragmentUnit fragmentUnit = new FragmentUnit(
      "frag",
      new DeclaredMethodUnit("method", TEMPLATE_UNIT, null, null),
      new GenericParams(),
      null,
      null);
    FragmentArgument fragmentArgument = new FragmentArgument(fragmentUnit, null);
    assertEquals("Fragment_method__jamon__frag", fragmentArgument.getFullyQualifiedType());
  }

  @Test
  public void testGetFullyQualifiedTypeForDefFragmentArg() {
    FragmentUnit fragmentUnit = new FragmentUnit(
      "frag",
      new DefUnit("def", TEMPLATE_UNIT, null, null),
      new GenericParams(),
      null,
      null);
    FragmentArgument fragmentArgument = new FragmentArgument(fragmentUnit, null);
    assertEquals("Fragment_def__jamon__frag", fragmentArgument.getFullyQualifiedType());
  }
}
