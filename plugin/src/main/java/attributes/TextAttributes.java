package attributes;

import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class TextAttributes {
    public static final TextAttributesKey CRITICAL;
    public static final TextAttributesKey MAJOR;
    public static final TextAttributesKey MINOR;
    public static final TextAttributesKey BLOCKER;
    public static final TextAttributesKey INFO;
    public static final TextAttributesKey SELECTED;

    static {
        /*
         * Defaults should be consistent with SonarLintSeverity
         */
        CRITICAL = TextAttributesKey.createTextAttributesKey("SONARLINT_CRITICAL", Severity.CRITICAL.defaultTextAttributes());
        MAJOR = TextAttributesKey.createTextAttributesKey("SONARLINT_MAJOR", Severity.MAJOR.defaultTextAttributes());
        MINOR = TextAttributesKey.createTextAttributesKey("SONARLINT_MINOR", Severity.MINOR.defaultTextAttributes());
        INFO = TextAttributesKey.createTextAttributesKey("SONARLINT_INFO", Severity.INFO.defaultTextAttributes());
        BLOCKER = TextAttributesKey.createTextAttributesKey("SONARLINT_BLOCKER", Severity.BLOCKER.defaultTextAttributes());
        SELECTED = createTextAttributesKey("SONARLINT_SELECTED");
    }
}
