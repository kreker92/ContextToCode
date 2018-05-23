import java.util.function.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

/**
 * Create here a class eligable to be returned from TestSuite::makeTestable()
 */
public class PassProgram implements Consumer<String>
{
    public void accept(String filePath)
    {
         try
         {
             List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
         }
         catch(Exception e)
         {
              throw new RuntimeException(e);
         }
    }
}


