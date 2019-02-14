package tmt.conf;

import com.google.gson.Gson;
import tmt.dsl.Classifier;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Conf {
    public static final ArrayList<String> bad_types = new Gson().fromJson(
            "[\"PsiJavaToken\",\"PsiDocToken\",\"PsiElement(BAD_CHARACTER)\",\"PsiModifierList\",\"PsiField\",\"PsiTypeElement\",\"PsiParameter\"]", ArrayList.class);
    public static final ArrayList<String> good_types  = new Gson().fromJson("[\"PsiKeyword\",\"PsiIdentifier\"]", ArrayList.class);

    public static Classifier templates = new Gson().fromJson(
            "{\"classes\":[{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\"," +
                    "\"ast_type\":\"PsiType:Intent\",\"comparing_method\":\"ast_type\"},{\"node\":\"\"," +
                    "\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Intent#PsiIdentifier:getAction\"," +
                    "\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Intent\"," +
                    "\"executor_command\":\"22\",\"scheme\":[{\"literal1\":\"String iAction \\u003d \",\"stab_req2\":\"PsiType:Intent\",\"literal3\":" +
                    "\".getAction();\"}],\"description\":\"Retrieve the general action to be performed, such as ACTION_VIEW\"},{\"elements\":[{\"node\":\"\"," +
                    "\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:EditText\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\"," +
                    "\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:EditText#PsiIdentifier:toString\",\"comparing_method\":\"class_method\"}]," +
                    "\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"EditText\",\"executor_command\":\"44\",\"scheme\":[{\"literal1\":" +
                    "\"String eText \\u003d \",\"stab_req2\":\"PsiType:EditText\",\"literal3\":\".getText().toString();\"}],\"description\":\"Return the text that" +
                    " TextView is displaying\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Resources\",\"comparing_method\"" +
                    ":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Resources#PsiIdentifier:getResources\"," +
                    "\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Resources\",\"executor_command\":\"23\"" +
                    ",\"scheme\":[{\"literal1\":\"Resources resources \\u003d \",\"stab_req2\":\"PsiType:Resources\",\"literal3\":\".getResources();\"}],\"description\":" +
                    "\"Returns a Resources instance for the application\\u0027s package\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":" +
                    "\"PsiType:SharedPreferences\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":" +
                    "\"PsiType:SharedPreferences#PsiIdentifier:getBoolean\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\"," +
                    "\"ast_type\":\"SharedPreferences\",\"executor_command\":\"45\",\"scheme\":[{\"literal1\":\"boolean spBoolean \\u003d \",\"stab_req2\":\"PsiType:SharedPref" +
                    "erences\",\"literal3\":\".getBoolean (String key, boolean defValue);\"}],\"description\":\"Retrieve a boolean value from the preferences\"},{\"elements\":[" +
                    "{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:TextView\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"lin" +
                    "e\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:TextView#PsiIdentifier:setVisibility\",\"comparing_method\":\"class_method\"}],\"start\":0," +
                    "\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"TextView\",\"executor_command\":\"24\",\"scheme\":[{\"stab_req1\":\"PsiType:TextView\",\"liter" +
                    "al2\":\".setVisibility (int visibility);\"}],\"description\":\"Set the visibility state of this view\"},{\"elements\":[{\"node\":\"\",\"text\":\"\"," +
                    "\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Parcel\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_" +
                    "type\":\"\",\"class_method\":\"PsiType:Parcel#PsiIdentifier:writeInt\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"typ" +
                    "e\":\"truekey\",\"ast_type\":\"Parcel\",\"executor_command\":\"46\",\"scheme\":[{\"stab_req1\":\"PsiType:Parcel\",\"literal2\":\".writeInt (int val);\"}]," +
                    "\"description\":\"Write an integer value into the parcel at the current dataPosition()\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":" +
                    "\"\",\"ast_type\":\"PsiType:SharedPreferences\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\"," +
                    "\"class_method\":\"PsiType:SharedPreferences#PsiIdentifier:getString\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"ty" +
                    "pe\":\"truekey\",\"ast_type\":\"SharedPreferences\",\"executor_command\":\"25\",\"scheme\":[{\"literal1\":\"String spString \\u003d \",\"stab_req2\":\"PsiT" +
                    "ype:SharedPreferences\",\"literal3\":\".getString (String key, boolean defValue);\"}],\"description\":\"Retrieve a String value from the preferences\"},{\"el" +
                    "ements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Context\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":" +
                    "\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Context#PsiIdentifier:getContentResolver\",\"comparing_method\":\"class_meth" +
                    "od\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Context\",\"executor_command\":\"26\",\"scheme\":[{\"literal1\":\"Conten" +
                    "tResolver cResolver \\u003d \",\"stab_req2\":\"PsiType:Context\",\"literal3\":\".getContentResolver();\"}],\"description\":\"Return a ContentResolver inst" +
                    "ance for your application\\u0027s package\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:EditText\",\"com" +
                    "paring_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:EditText#PsiIdentifier:s" +
                    "etText\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"EditText\",\"executor_command\":" +
                    "\"27\",\"scheme\":[{\"stab_req1\":\"PsiType:EditText\",\"literal2\":\".setText (CharSequence text, TextView.BufferType type);\"}],\"description\":\"Sets t" +
                    "he text to be displayed and the TextView.BufferType\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:MotionE" +
                    "vent\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:MotionEvent#" +
                    "PsiIdentifier:getX\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"MotionEvent\",\"exec" +
                    "utor_command\":\"28\",\"scheme\":[{\"literal1\":\"Float meXcoor \\u003d \",\"stab_req2\":\"PsiType:MotionEvent\",\"literal3\":\".getX();\"}],\"description\":" +
                    "\"Returns the X coordinate of this event\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:LayoutInflater\",\"co" +
                    "mparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:LayoutInflater#PsiIdentif" +
                    "ier:inflate\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"LayoutInflater\",\"executor" +
                    "_command\":\"29\",\"scheme\":[{\"literal1\":\"View liView \\u003d \",\"stab_req2\":\"PsiType:LayoutInflater\",\"literal3\":\".inflate (...);\"}],\"descrip" +
                    "tion\":\"Inflate a new view hierarchy\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:TextView\",\"comparing" +
                    "_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:TextView#PsiIdentifier:setText\"," +
                    "\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"TextView\",\"executor_command\":\"30\",\"sc" +
                    "heme\":[{\"stab_req1\":\"PsiType:TextView\",\"literal2\":\".setText (CharSequence text);\"}],\"description\":\"Set the text that is associated with this vi" +
                    "ew\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Context\",\"comparing_method\":\"ast_type\"}," +
                    "{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Context#PsiIdentifier:getSystemService\",\"comparing_" +
                    "method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Context\",\"executor_command\":\"31\",\"scheme\":[{\"lit" +
                    "eral1\":\"Object manager \\u003d \",\"stab_req2\":\"PsiType:Context\",\"literal3\":\".getSystemService (String name);\"}],\"description\":\"Return the han" +
                    "dle to a system-level service by name\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Context\",\"comparing" +
                    "_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Context#PsiIdentifier:getString\"," +
                    "\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Context\",\"executor_command\":\"10\"," +
                    "\"scheme\":[{\"literal1\":\"String text \\u003d \",\"stab_req2\":\"PsiType:Context\",\"literal3\":\".getString (int resId);\"}],\"description\":\"Returns a" +
                    " localized string from the application\\u0027s package\\u0027s default string table\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\"," +
                    "\"ast_type\":\"PsiType:MenuItem\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_meth" +
                    "od\":\"PsiType:MenuItem#PsiIdentifier:getItemId\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_" +
                    "type\":\"MenuItem\",\"executor_command\":\"11\",\"scheme\":[{\"literal1\":\"int mId \\u003d \",\"stab_req2\":\"PsiType:MenuItem\",\"literal3\":\".getItemI" +
                    "d ();\"}],\"description\":\"Return the identifier for this menu item\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":" +
                    "\"PsiType:TextView\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:" +
                    "TextView#PsiIdentifier:findViewById\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Tex" +
                    "tView\",\"executor_command\":\"33\",\"scheme\":[{\"literal1\":\"View tView \\u003d \",\"stab_req2\":\"PsiType:TextView\",\"literal3\":\".findViewById (int" +
                    " id);\"}],\"description\":\"Finds the first descendant view with the given ID, the view itself if the ID matches getId()\"},{\"elements\":[{\"node\":\"\"," +
                    "\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Intent\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":" +
                    "\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Intent#PsiIdentifier:putExtra\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":" +
                    "0,\"type\":\"truekey\",\"ast_type\":\"Intent\",\"executor_command\":\"12\",\"scheme\":[{\"stab_req1\":\"PsiType:Intent\",\"literal2\":\".putExtra (String" +
                    " name, ... value);\"}],\"description\":\"Add extended data to the intent\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_ty" +
                    "pe\":\"PsiType:PrintStream\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":" +
                    "\"PsiType:PrintStream#PsiIdentifier:println\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":" +
                    "\"PrintStream\",\"executor_command\":\"34\",\"scheme\":[{\"stab_req1\":\"PsiType:PrintStream\",\"literal2\":\".println (Object x);\"}],\"description\":\"Prin" +
                    "ts an Object and then terminate the line\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:EditText\",\"compar" +
                    "ing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:EditText#PsiIdentifier:" +
                    "getText\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"EditText\",\"executor_command\":" +
                    "\"13\",\"scheme\":[{\"literal1\":\"Editable eText \\u003d \",\"stab_req2\":\"PsiType:EditText\",\"literal3\":\".getText();\"}],\"description\":\"Return" +
                    " the text that TextView is displaying\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Bundle\",\"comparing_" +
                    "method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Bundle#PsiIdentifier:getString\"," +
                    "\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Bundle\",\"executor_command\":\"35\",\"sche" +
                    "me\":[{\"literal1\":\"String bString \\u003d \",\"stab_req2\":\"PsiType:Bundle\",\"literal3\":\".getString (String key);\"}],\"description\":\"Returns the" +
                    " value associated with the given key\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:ContentValues\",\"com" +
                    "paring_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:ContentValues#PsiIdentif" +
                    "ier:put\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"ContentValues\",\"executor_co" +
                    "mmand\":\"14\",\"scheme\":[{\"stab_req1\":\"PsiType:ContentValues\",\"literal2\":\".put (String key, value);\"}],\"description\":\"Adds a value to the " +
                    "set\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Intent\",\"comparing_method\":\"ast_type\"}," +
                    "{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Intent#PsiIdentifier:getStringExtra\",\"comparing_met" +
                    "hod\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Intent\",\"executor_command\":\"36\",\"scheme\":[{\"stab" +
                    "_req1\":\"PsiType:Intent\",\"literal2\":\".putExtra (String name, ...);\"}],\"description\":\"Retrieve extended data from the intent\"},{\"elements\":" +
                    "[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Menu\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"lin" +
                    "e\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Menu#PsiIdentifier:add\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0," +
                    "\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Menu\",\"executor_command\":\"37\",\"scheme\":[{\"stab_req1\":\"PsiType:Menu\",\"literal2\":\".add (...);" +
                    "\"}],\"description\":\"Add a new item to the menu\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:SQLite" +
                    "Database\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:SQLite" +
                    "Database#PsiIdentifier:execSQL\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"SQLite" +
                    "Database\",\"executor_command\":\"15\",\"scheme\":[{\"stab_req1\":\"PsiType:SQLiteDatabase\",\"literal2\":\".execSQL (String sql, Object[] bindArgs);\"}]," +
                    "\"description\":\"Execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"ty" +
                    "pe\":\"\",\"ast_type\":\"PsiType:Button\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"cla" +
                    "ss_method\":\"PsiType:Button#PsiIdentifier:setOnClickListener\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"tru" +
                    "ekey\",\"ast_type\":\"Button\",\"executor_command\":\"38\",\"scheme\":[{\"stab_req1\":\"PsiType:Button\",\"literal2\":\".setOnClickListener(new View.On" +
                    "ClickListener() {\\n        @Override\\n        public void onClick(View view) {\\n            doSomeWork();\\n        }\\n    });\"}],\"description\":" +
                    "\"Register a callback to be invoked when this view is context clicked\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":" +
                    "\"PsiType:Resources\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:" +
                    "Resources#PsiIdentifier:getString\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Resour" +
                    "ces\",\"executor_command\":\"16\",\"scheme\":[{\"literal1\":\"String rString \\u003d \",\"stab_req2\":\"PsiType:Resources\",\"literal3\":\".getString (int" +
                    " resId, Object... formatArgs);\"}],\"description\":\"Returns a localized string from the application\\u0027s package\\u0027s default string table\"},{\"el" +
                    "ements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Bundle\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":" +
                    "\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Bundle#PsiIdentifier:putString\",\"comparing_method\":\"class_method\"}],\"sta" +
                    "rt\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Bundle\",\"executor_command\":\"39\",\"scheme\":[{\"stab_req1\":\"PsiType:Bundle\"," +
                    "\"literal2\":\".putString (String key, String value);\"}],\"description\":\"Inserts a String value into the mapping of this Bundle, replacing any existing" +
                    " value for the given key\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Parcel\",\"comparing_method\":" +
                    "\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Parcel#PsiIdentifier:recycle\",\"comparing" +
                    "_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Parcel\",\"executor_command\":\"17\",\"scheme\":" +
                    "[{\"stab_req1\":\"PsiType:Parcel\",\"literal2\":\".recycle();\"}],\"description\":\"Put a Parcel object back into the pool\"},{\"elements\":[{\"node\":\"\"," +
                    "\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:IntentFilter\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0," +
                    "\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:IntentFilter#PsiIdentifier:addAction\",\"comparing_method\":\"class_method\"}],\"start\":0," +
                    "\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"IntentFilter\",\"executor_command\":\"18\",\"scheme\":[{\"stab_req1\":\"PsiType:IntentFilter\"," +
                    "\"literal2\":\".addAction (String action);\"}],\"description\":\"Add a new Intent action to match against\"},{\"elements\":[{\"node\":\"\",\"text\":" +
                    "\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Cursor\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\"," +
                    "\"ast_type\":\"\",\"class_method\":\"PsiType:Cursor#PsiIdentifier:moveToNext\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0," +
                    "\"type\":\"truekey\",\"ast_type\":\"Cursor\",\"executor_command\":\"19\",\"scheme\":[{\"stab_req1\":\"PsiType:Cursor\",\"literal2\":\".moveToNext ();\"}]," +
                    "\"description\":\"Move the cursor to the next row\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:PrintWri" +
                    "ter\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:PrintWriter#" +
                    "PsiIdentifier:println\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"PrintWriter\",\"ex" +
                    "ecutor_command\":\"1\",\"scheme\":[{\"stab_req1\":\"PsiType:PrintWriter\",\"literal2\":\".println (Object x);\"}],\"description\":\"Prints an Object and th" +
                    "en terminate the line\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Cursor\",\"comparing_method\":\"ast_ty" +
                    "pe\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Cursor#PsiIdentifier:getInt\",\"comparing_method\":" +
                    "\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Cursor\",\"executor_command\":\"2\",\"scheme\":[{\"literal1\":" +
                    "\"int cValue \\u003d \",\"stab_req2\":\"PsiType:Cursor\",\"literal3\":\".getInt (int columnIndex);\"}],\"description\":\"Returns the value of the requested" +
                    " column as an int\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Button\",\"comparing_method\":\"ast_t" +
                    "ype\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Button#PsiIdentifier:findViewById\",\"comparing_" +
                    "method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Button\",\"executor_command\":\"4\",\"scheme\":[{\"lit" +
                    "eral1\":\"Button mButton \\u003d (Button) \",\"stab_req2\":\"PsiType:View\",\"literal3\":\".findViewById(int id);\"}],\"description\":\"Finds the first de" +
                    "scendant view with the given ID\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Cursor\",\"comparing_met" +
                    "hod\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Cursor#PsiIdentifier:getLong\",\"co" +
                    "mparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Cursor\",\"executor_command\":\"5\",\"scheme" +
                    "\":[{\"literal1\":\"long cValue \\u003d \",\"stab_req2\":\"PsiType:Cursor\",\"literal3\":\".getLong (int columnIndex);\"}],\"description\":\"Returns the" +
                    " value of the requested column as a long\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Cursor\",\"compa" +
                    "ring_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Cursor#PsiIdentifier:getC" +
                    "ount\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Cursor\",\"executor_command\":\"6\"," +
                    "\"scheme\":[{\"literal1\":\"int cCount \\u003d \",\"stab_req2\":\"PsiType:Cursor\",\"literal3\":\".getCount ();\"}],\"description\":\"Returns the numbers " +
                    "of rows in the cursor\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Parcel\",\"comparing_method\":\"ast_" +
                    "type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Parcel#PsiIdentifier:obtain\",\"comparing_meth" +
                    "od\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Parcel\",\"executor_command\":\"7\",\"scheme\":[{\"liter" +
                    "al1\":\"Parcel parcel \\u003d \",\"stab_req2\":\"PsiType:Parcel\",\"literal3\":\".obtain();\"}],\"description\":\"Retrieve a new Parcel object from the " +
                    "pool\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Cursor\",\"comparing_method\":\"ast_type\"},{\"node\":" +
                    "\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Cursor#PsiIdentifier:getColumnIndexOrThrow\",\"comparing_method\":" +
                    "\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Cursor\",\"executor_command\":\"8\",\"scheme\":[{\"literal1\":" +
                    "\"int cIndex \\u003d \",\"stab_req2\":\"PsiType:Cursor\",\"literal3\":\".getColumnIndex (String columnName);\"}],\"description\":\"Returns the zero-based " +
                    "index for the given column name\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Cursor\",\"comparing_me" +
                    "thod\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Cursor#PsiIdentifier:getColumnI" +
                    "ndex\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Cursor\",\"executor_command\":\"9\"," +
                    "\"scheme\":[{\"literal1\":\"int cIndex \\u003d \",\"stab_req2\":\"PsiType:Cursor\",\"literal3\":\".getColumnIndex (String columnName);\"}],\"description\":" +
                    "\"Returns the value associated with the given key\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Cursor\"," +
                    "\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Cursor#PsiIdentifier:" +
                    "close\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Cursor\",\"executor_command\":\"40" +
                    "\",\"scheme\":[{\"stab_req1\":\"PsiType:Cursor\",\"literal2\":\".close ();\"}],\"description\":\"Closes the Cursor, releasing all of its resources and mak" +
                    "ing it completely invalid\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Paint\",\"comparing_method\":\"ast_" +
                    "type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Paint#PsiIdentifier:setColor\",\"comparing_m" +
                    "ethod\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Paint\",\"executor_command\":\"41\",\"scheme\":[{\"sta" +
                    "b_req1\":\"PsiType:Paint\",\"literal2\":\".setColor (int color);\"}],\"description\":\"Set the paint\\u0027s color\"},{\"elements\":[{\"node\":\"\",\"te" +
                    "xt\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiType:Cursor\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\"," +
                    "\"ast_type\":\"\",\"class_method\":\"PsiType:Cursor#PsiIdentifier:moveToFirst\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_" +
                    "num\":0,\"type\":\"truekey\",\"ast_type\":\"Cursor\",\"executor_command\":\"42\",\"scheme\":[{\"stab_req1\":\"PsiType:Cursor\",\"literal2\":\".moveToFir" +
                    "st();\"}],\"description\":\"Move the cursor to the first row\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"PsiTyp" +
                    "e:Parcel\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Parcel#P" +
                    "siIdentifier:readInt\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Parcel\",\"executo" +
                    "r_command\":\"20\",\"scheme\":[{\"literal1\":\"int pInt \\u003d \",\"stab_req2\":\"PsiType:Parcel\",\"literal3\":\".readInt();\"}],\"description\":\"Read " +
                    "an integer value from the parcel at the current dataPosition()\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"Psi" +
                    "Type:Cursor\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_method\":\"PsiType:Curso" +
                    "r#PsiIdentifier:getString\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_type\":\"Cursor\",\"exe" +
                    "cutor_command\":\"43\",\"scheme\":[{\"literal1\":\"String cValue \\u003d \",\"stab_req2\":\"PsiType:Cursor\",\"literal3\":\".getString (int columnInd" +
                    "ex);\"}],\"description\":\"Returns the value of the requested column as a string\"},{\"elements\":[{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\"," +
                    "\"ast_type\":\"PsiType:MotionEvent\",\"comparing_method\":\"ast_type\"},{\"node\":\"\",\"text\":\"\",\"line\":0,\"type\":\"\",\"ast_type\":\"\",\"class_m" +
                    "ethod\":\"PsiType:MotionEvent#PsiIdentifier:getY\",\"comparing_method\":\"class_method\"}],\"start\":0,\"end\":0,\"line_num\":0,\"type\":\"truekey\",\"ast_" +
                    "type\":\"MotionEvent\",\"executor_command\":\"21\",\"scheme\":[{\"literal1\":\"Float meXcoor \\u003d \",\"stab_req2\":\"PsiType:MotionEvent\",\"literal3\":" +
                    "\".getY();\"}],\"description\":\"Returns the Y coordinate of this event\"}],\"folder\":\"android_crossvalidation2/\",\"blocking\":false,\"vs\":[],\"domain" +
                    "\":\"MotionEvent_getY\"}", Classifier.class);;
    public static String stab = "{\"elements\":[{\"node\":\"PsiExpressionStatement\",\"text\":\"stab\",\"line\":61,\"type\":\"EXPRESSION_STATEMENT\",\"parent\":\"PsiCodeBlock\"" +
            "},{\"node\":\"PsiReferenceExpression\",\"text\":\"stab\",\"line\":61,\"type\":\"REFERENCE_EXPRESSION\",\"ast_type\":\"PsiReferenceExpression:stab\",\"parent\":\"" +
            "PsiExpressionStatement\"},{\"node\":\"PsiReferenceParameterList\",\"text\":\"\",\"line\":61,\"type\":\"REFERENCE_PARAMETER_LIST\",\"parent\":\"PsiReferenceExpres" +
            "sion\"},{\"node\":\"PsiIdentifier\",\"text\":\"stab\",\"line\":61,\"type\":\"IDENTIFIER\",\"parent\":\"PsiReferenceExpression\"},{\"node\":\"PsiErrorElement\"," +
            "\"text\":\"\",\"line\":61,\"type\":\"ERROR_ELEMENT\",\"parent\":\"PsiExpressionStatement\"}],\"start\":2198,\"end\":2226,\"line_num\":61,\"line_text\":\"                       stab\\n\"}";
}
