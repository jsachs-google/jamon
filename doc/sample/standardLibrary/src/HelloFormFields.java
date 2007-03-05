package foo.bar;

import java.util.Map;
import foo.bar.html.TextInputField;

public class HelloFormFields {

    public HelloFormFields(int p_number) {
        m_number = String.valueOf(p_number);
    }

    public HelloFormFields(Map p_data) {
        String[] numbers = (String[])p_data.get(NUMBER_KEY);
        if (numbers == null) {
            m_number = null;
        } else {
            m_number = numbers[0];
        }
    }

    public TextInputField getNumberInput() {
        return new TextInputField(NUMBER_KEY, m_number);
    }

    public boolean hasData() {
        return m_number != null;
    }

    public int parse()
        throws ValidationException {
        try {
            int number = Integer.parseInt(m_number);
            if (number < 1 || number > MAX_NUMBER) {
                ValidationErrors errors = new ValidationErrors();
                errors.outOfRange();
                throw new ValidationException(errors);
            }
            return number;
        } catch (NumberFormatException e) {
            ValidationErrors errors = new ValidationErrors();
            errors.notANumber();
            throw new ValidationException(errors);
        }
    }

    public static class ValidationErrors {

        public boolean hasErrors() {
            return m_notANumber || m_outOfRange;
        }

        public boolean isNotANumber() {
            return m_notANumber;
        }

        public boolean isOutOfRange() {
            return m_outOfRange;
        }

        void notANumber() {
            m_notANumber = true;
        }

        void outOfRange() {
            m_outOfRange = true;
        }

        private boolean m_notANumber, m_outOfRange;

    } // end of inner class ValidationErrors


    public static class ValidationException
        extends Exception {

        ValidationException(ValidationErrors p_errors) {
            m_errors = p_errors;
        }

        public ValidationErrors getErrors() {
            return m_errors;
        }

        private final ValidationErrors m_errors;
    } // end of inner class ValidationException


    private final String m_number;

    private static final String NUMBER_KEY = "num";
    private static final int MAX_NUMBER = 20;

}