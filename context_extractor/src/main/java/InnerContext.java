import java.util.ArrayList;

public class InnerContext {
  public ArrayList<ElementInfo> elements = new ArrayList<>();
  public int start;
  public int end;
  public int line_num;
  public String line_text;
  public String clean_line_text = "";
}