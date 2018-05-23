import org.junit.*;
import java.util.function.*;
import java.io.*;

/**
 * Defines JUnit test cases to check that sythesized code provides expected runtime behavior.
 */
public abstract class TestSuite
{

        @Test
        public void TestNoExceptionsGenerated() throws Exception
        {
           File temp = File.createTempFile("temp-file", ".tmp");
    	   temp.deleteOnExit();
           try(  PrintWriter out = new PrintWriter(temp.getAbsolutePath()))
           {
               out.println("file contents");
           }
           makeTestable().accept(temp.getAbsolutePath());  // just ensure no exception is generated
        }

	/**
	 * @return instances of the class to test.
	 */
	protected abstract Consumer<String> makeTestable();

        // Other common useful signatures for makeTestable that can be swapped out for above:

        // protected abstract Function<Foo,Bar> makeTestable();
        // protected abstract Consumer<Foo> makeTestable();
        // protected abstract Supplier<Foo> makeTestable();
}

