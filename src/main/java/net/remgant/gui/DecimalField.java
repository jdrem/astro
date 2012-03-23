package net.remgant.gui;

/**
 * @author jdr
 * @version $Id$
 * @since 3/22/12 12:02 PM
 */
public class DecimalField extends javax.swing.JTextField {
    public DecimalField() {
        super();
    }

    public DecimalField(int cols) {
        super(cols);
    }

    public DecimalField(String text, int cols) {
        super(text, cols);
    }

    protected javax.swing.text.Document createDefaultModel() {
        return new NumberDocument();
    }

    static class NumberDocument extends javax.swing.text.PlainDocument {
        public void insertString(int offs, String str,
                                 javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            if (str == null) {
                return;
            }
            char[] number = str.toCharArray();
            char[] outstr = new char[number.length];
            int j = 0;
            for (int i = 0; i < number.length; i++) {
                if (Character.isDigit(number[i]) || number[i] == '.')
                    outstr[j++] = number[i];
            }
            if (j > 0)
                super.insertString(offs, new String(outstr, 0, j), a);
        }
    }
}
