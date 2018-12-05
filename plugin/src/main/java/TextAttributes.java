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
        CRITICAL = createTextAttributesKey("SONARLINT_CRITICAL", Severity.CRITICAL.defaultTextAttributes());
        MAJOR = createTextAttributesKey("SONARLINT_MAJOR", Severity.MAJOR.defaultTextAttributes());
        MINOR = createTextAttributesKey("SONARLINT_MINOR", Severity.MINOR.defaultTextAttributes());
        INFO = createTextAttributesKey("SONARLINT_INFO", Severity.INFO.defaultTextAttributes());
        BLOCKER = createTextAttributesKey("SONARLINT_BLOCKER", Severity.BLOCKER.defaultTextAttributes());
        SELECTED = createTextAttributesKey("SONARLINT_SELECTED");
    }
}
