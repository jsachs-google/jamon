package foo.bar.html;

public class TextInputField {
  public TextInputField(String p_name) {
    this (p_name, null);
  }

  public TextInputField(String p_name, String p_value) {
    m_name = p_name;
    m_value = p_value;
  }

  public String getName() {
    return m_name;
  }

  public String getValue() {
    return m_value;
  }

  private final String m_name;
  private final String m_value;
}
