package sudoku;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/** Small util: restrict text fields to a single digit 1-9. */
public class Util {
    public static class IntDocument extends PlainDocument {
        private final int maxLen;
        public IntDocument(int maxLen) { this.maxLen = maxLen; }
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) return;
            StringBuilder sb = new StringBuilder();
            for (char ch : str.toCharArray()) {
                if (Character.isDigit(ch)) sb.append(ch);
            }
            String candidate = getText(0, getLength()) + sb.toString();
            if (candidate.length() > maxLen) return;
            // Only allow 1-9 (no zero)
            if (sb.length() > 0) {
                for (char ch : sb.toString().toCharArray()) {
                    if (ch == '0') return;
                }
            }
            super.insertString(offs, sb.toString(), a);
        }
    }
}
