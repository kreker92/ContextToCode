package tmt.analyze;

import com.google.common.collect.Sets;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import java.util.List;
import java.util.Set;

public class SyntaxUtils {

    public static final Set<IElementType> meaninglessForContextTokenTypes =
            Sets.newHashSet(
                    TokenType.WHITE_SPACE,

                    JavaTokenType.C_STYLE_COMMENT,
                    JavaTokenType.END_OF_LINE_COMMENT,

                    JavaTokenType.LPARENTH,
                    JavaTokenType.RPARENTH,
                    JavaTokenType.LBRACE,
                    JavaTokenType.RBRACE,
                    JavaTokenType.LBRACKET,
                    JavaTokenType.RBRACKET,
                    JavaTokenType.SEMICOLON,
                    JavaTokenType.COMMA,
                    JavaTokenType.DOT,
                    JavaTokenType.ELLIPSIS,
                    JavaTokenType.AT,

                    JavaTokenType.EQ,
                    JavaTokenType.GT,
                    JavaTokenType.LT,
                    JavaTokenType.EXCL,
                    JavaTokenType.TILDE,
                    JavaTokenType.QUEST,
                    JavaTokenType.COLON,
                    JavaTokenType.PLUS,
                    JavaTokenType.MINUS,
                    JavaTokenType.ASTERISK,
                    JavaTokenType.DIV,
                    JavaTokenType.AND,
                    JavaTokenType.OR,
                    JavaTokenType.XOR,
                    JavaTokenType.PERC,

                    JavaTokenType.EQEQ,
                    JavaTokenType.LE,
                    JavaTokenType.GE,
                    JavaTokenType.NE,
                    JavaTokenType.ANDAND,
                    JavaTokenType.OROR,
                    JavaTokenType.PLUSPLUS,
                    JavaTokenType.MINUSMINUS,
                    JavaTokenType.LTLT,
                    JavaTokenType.GTGT,
                    JavaTokenType.GTGTGT,
                    JavaTokenType.PLUSEQ,
                    JavaTokenType.MINUSEQ,
                    JavaTokenType.ASTERISKEQ,
                    JavaTokenType.DIVEQ,
                    JavaTokenType.ANDEQ,
                    JavaTokenType.OREQ,
                    JavaTokenType.XOREQ,
                    JavaTokenType.PERCEQ,
                    JavaTokenType.LTLTEQ,
                    JavaTokenType.GTGTEQ,
                    JavaTokenType.GTGTGTEQ,

                    JavaTokenType.DOUBLE_COLON,
                    JavaTokenType.ARROW);

    public static void traversePsiElement(PsiElement element, List<PsiElement> selectedElements, int selectionStartOffset, int selectionEndOffset) {
        int elementStart = element.getTextOffset();
        int elementEnd = elementStart + element.getTextLength();
        if (selectionStartOffset <= elementStart && elementEnd <= selectionEndOffset) {
            selectedElements.add(element);
        }
        for (PsiElement childElement : element.getChildren()) {
            traversePsiElement(childElement, selectedElements, selectionStartOffset, selectionEndOffset);
        }
    }
}
