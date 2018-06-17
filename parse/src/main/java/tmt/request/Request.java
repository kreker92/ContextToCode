package com.detectum.request;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.Normalizer;
import java.nio.charset.Charset;
import java.nio.CharBuffer;

import com.detectum.SearchContext;
import com.detectum.config.*;
import com.detectum.config.synonym.Synonym;
import com.detectum.config.synonym.WordSynKey;
import com.detectum.data.Index;
import com.detectum.request.speller.Speller;
import com.detectum.request.stemmer.GrsInfo;
import com.detectum.request.stemmer.Stemmer;

import org.apache.commons.collections4.iterators.IteratorChain;

import de.danielnaber.jwordsplitter.GermanWordSplitter;
import de.danielnaber.jwordsplitter.AbstractWordSplitter;

import com.google.gson.Gson;

public class Request {
  private transient SearchConfiguration configuration;
  private transient SearchLanguage lang;
  private String request;
  private int generation;
  private int coverage;
  private ArrayList<Integer> word_breaks = new ArrayList<Integer>();
  private static final int generations_per_coverage = 3;
  private List<Token> tokens;
  private List<Token> space_delimited = new ArrayList<Token>();

  private List<Word> goda_words = new ArrayList<Word>();
  private List<Word> words0;    // original forms and basic alternations (layout etc)
  private List<Word> words;     // index forms for all lemmas
  private List<Word> stopwords;
  private List<Word> prepositions;
  private List<Word> phantoms;
  private List<Word> specials = new ArrayList<Word>();    // " ' $ etc
  private Map<Integer,MultiSyn> multi_syn = new HashMap<Integer,MultiSyn>();
  private Map<Integer,ArrayList<Tag>> cats_n_tags = new HashMap<Integer,ArrayList<Tag>>();

  private ArrayList<ArrayList<Token>> translit_groups = new ArrayList<ArrayList<Token>>();
  
  public HashSet<Token> composites = new HashSet<Token>();

  private List<Range> ranges;
  private List<Number> numbers;

  private transient Speller spell_process = null;
  private transient Stemmer stemmer = null;

  public static Pattern cyrillic = Pattern.compile("([\\p{IsCyrillic}]+)");
  static Pattern cyrillic_latin = Pattern.compile("([\\p{IsCyrillic}\\p{IsLatin}]+)");
  public static Pattern latin = Pattern.compile("([\\p{IsLatin}]+)");
  static Pattern number = Pattern.compile("([0-9]+([.,][0-9]+))");
  static Pattern integer = Pattern.compile("([0-9]+)");
  static Pattern non_cyrillic = Pattern.compile("([[\\p{IsAlphabetic}]&&[^\\p{IsCyrillic}]]+)");
  static Pattern iso88591 = Pattern.compile("([\u0080-\u00ff]+)");
  static Pattern unglue = Pattern.compile("([\\p{IsAlphabetic},.;'\\[\\]<>:\"{}]+|[0-9]+([.,][0-9]+)*)");
  static Pattern unglue_lat = Pattern.compile("([\\p{IsLatin}]+|[0-9]+([.,][0-9]+)*)");
  static Pattern pseudo_number = Pattern.compile("([0-9]+([.,][0-9]+)*)");
  static Pattern possible_word_parts = Pattern.compile("([\\p{IsCyrillic}\\p{IsLatin}]+|[\\p{IsLatin},.;'\\[\\]<>:\"{}]+|[[\\p{IsAlphabetic}]&&[^\\p{IsCyrillic}]&&[^\\p{IsLatin}]]+)");
  static Pattern latin_plus = Pattern.compile("([\\p{IsLatin},.;'\\[\\]<>:\"{}]+)");
  static Pattern just_plus = Pattern.compile("[,.;'\\[\\]<>:\"{}]+");
  static Pattern space_dash = Pattern.compile("([^\\p{Space}-]+)");
  static Pattern spelled_word = Pattern.compile("([\\p{IsAlphabetic} -']+)(, |$)");
  static Pattern separators = Pattern.compile("[[^\\p{IsAlphabetic}]&&[^0-9]]+");
  static Pattern glued_words_numbers = Pattern.compile("([\\p{IsAlphabetic}][0-9]|[0-9][\\p{IsAlphabetic}]|[\\p{IsCyrillic}][[\\p{IsAlphabetic}]&&[^\\p{IsCyrillic}]]|[[\\p{IsAlphabetic}]&&[^\\p{IsCyrillic}]][\\p{IsCyrillic}])"); // num-alpha and cyr-noncyr alpha breaks

  static Set<String> rangeStart;
  static {
    rangeStart = new HashSet<String>();
    rangeStart.add("от");
    rangeStart.add("с");
  }
  static Set<String> rangeEnd;
  static {
    rangeEnd = new HashSet<String>();
    rangeEnd.add("до");
  }
  static Map<Character, Character> latinCyrillicMap;
  static {
    latinCyrillicMap = new HashMap<Character, Character>();
    latinCyrillicMap.put('c' , 'с');
    latinCyrillicMap.put('e' , 'е');
    latinCyrillicMap.put('h' , 'н');
    latinCyrillicMap.put('k' , 'к');
    latinCyrillicMap.put('m' , 'м');
    latinCyrillicMap.put('o' , 'о');
    latinCyrillicMap.put('p' , 'р');
    latinCyrillicMap.put('t' , 'т');
    latinCyrillicMap.put('x' , 'х');
    latinCyrillicMap.put('y' , 'у');
    latinCyrillicMap.put('a' , 'а');
    latinCyrillicMap.put('b' , 'в');
    latinCyrillicMap.put('n' , 'п');
    latinCyrillicMap.put('u' , 'и');

  }

  static Map<Character, Character> cyrillicLatinMap;
  static {
    cyrillicLatinMap = new HashMap<Character, Character>();
    cyrillicLatinMap.put('с' , 'c');
    cyrillicLatinMap.put('е' , 'e');
    cyrillicLatinMap.put('н' , 'h');
    cyrillicLatinMap.put('к' , 'k');
    cyrillicLatinMap.put('м' , 'm');
    cyrillicLatinMap.put('о' , 'o');
    cyrillicLatinMap.put('р' , 'p');
    cyrillicLatinMap.put('т' , 't');
    cyrillicLatinMap.put('х' , 'x');
    cyrillicLatinMap.put('у' , 'y');
    cyrillicLatinMap.put('а' , 'a');
    cyrillicLatinMap.put('в' , 'b');
    cyrillicLatinMap.put('п' , 'n');
    cyrillicLatinMap.put('и' , 'u');
  }



  public Request(String request_, SearchConfiguration configuration_, SearchLanguage lang_) {
    configuration = configuration_;
    lang = lang_;
    generation = 0;
    coverage = 0;

    if (request_.length() > 300) {
      int n = request_.indexOf(' ', 300);
      if (n == -1 || n > 500){
        request = request_.substring(0, 300);
      }
      else {
        request = request_.substring(0, n);

      }
    }
    else {
      request = request_;
    }
//    if (lang.getLang().equals("de"))
//      request = request.toLowerCase().replaceAll("ß", "ss").replaceAll("ue", "ü").replaceAll("ae", "ä").replaceAll("oe", "ö");
    tokens = new ArrayList<Token>();
    words0 = new ArrayList<Word>();
    words = new ArrayList<Word>();
    stopwords = new ArrayList<Word>();
    prepositions = new ArrayList<Word>();
    phantoms = new ArrayList<Word>();

    ranges = new ArrayList<Range>();
    numbers = new ArrayList<Number>();

    if (lang.getLang().equals("ru"))
      parse_tokens_ru();
    else if(lang.getLang().equals("ru_en"))
      parse_tokens_ru_en();
    else if (lang.getLang().equals("en"))
      parse_tokens_en();
    else if (lang.getLang().equals("de"))
      parse_tokens_de();

    Matcher mt = separators.matcher(request);
    int sep_end = 0;
    while ( mt.find() ) {
      word_breaks.add(mt.start());
      sep_end = mt.end();
    }
    if ( word_breaks.isEmpty() || word_breaks.get(0) != 0 )
      word_breaks.add(0, 0);

    if ( request.length() != sep_end )
      word_breaks.add(request.length());

    mt = glued_words_numbers.matcher(request);
    int start = -1;
    while ( mt.find(start + 1) ) {
      start = mt.start();
      word_breaks.add(start+1);
    }
    Collections.sort(word_breaks);

    /*
    System.err.printf(
      "0123456789012345678901234567890123456789012345678901234567890123456789\n%s\nword_breaks: %s\n", 
      request, word_breaks);
    */

    prepare_words0();

    init_processes();
  }

  public String request() {
    return request;
  }

  public int coverage() {
    return coverage;
  }

  public int generation() {
    return generation;
  }

  public boolean completed_coverage() {
    return generation() % generations_per_coverage == 0;
  }

  public boolean total_coverage() {
    return ( coverage * 7 >= word_breaks.size() - 1 );
  }

  public Collection<Token> tokens() {
    return tokens;
  }

  public List<Token> space_delimited() {
    return space_delimited;
  }

  public Collection<Word> words(int generation_) {
    if ( generation_ == 1 || generation_ == 0 )  // first generation of words or all of them
      return words;
    else {
      List<Word> more = new ArrayList<Word>();
      for (Word w: words)
        if ( w.generation() == generation_)
          more.add(w);
      return more;
    }
  }

  public Collection<Word> words_basic(int coverage_) {
    if ( coverage_ == 0 )
      return words0;
    else {
      Collection<Word> covered_words = new ArrayList<Word>();
      for (Word w: words0)
        if ( w.start() < word_breaks.get(Math.min(7 * coverage,word_breaks.size()-1) ) )
          covered_words.add(w);
      return covered_words;
    }
  }

  public Collection<Word> getWords() {
    return words;
  }

  public Collection<Word> stopwords() {
    return stopwords;
  }

  public Collection<Word> prepositions() {
    return prepositions;  
  }

  public Collection<Word> phantoms() {
    return phantoms;
  }

  public Collection<Range> ranges(int generation_) {
    if ( generation_ == 0 ) 
      return ranges;

    Collection<Range> rr = new ArrayList<Range>();
    for ( Range r: ranges )
      if ( r.generation() == generation_ ) rr.add(r);

    return rr;
  }

  public Collection<Number> numbers(int generation_) {
    if ( generation_ == 0 ) 
      return numbers;

    Collection<Number> nn = new ArrayList<Number>();
    for ( Number n: numbers )
      if ( n.generation() == generation_ ) nn.add(n);

    return nn;
  }

  public Map<Integer,MultiSyn> multi_syn() {return multi_syn;}

  public Map<Integer, ArrayList<Tag>> tags() {return cats_n_tags;}

  /*
    There exist two options how to calculate start and end positions of tokens in request reliably:
    1. remove 32-bit unicode
    or
    2. wrap Matcher.start() and end() with Character.codePointCount()

    http://docs.oracle.com/javase/tutorial/i18n/text/unicode.html
   */


  public void parse(boolean extend) throws Exception {
    if ( extend ) {
      coverage++;
      generation = (coverage-1)*generations_per_coverage + 1;
    }
    else {
      generation++;
      if ( generation % generations_per_coverage == 1)
        throw new Exception(String.format("Generations overflow coverage %d generation %d", coverage, generation));
    }

    Collection<Word> to_process = null;
    if ( generation % generations_per_coverage == 1 ) {
       to_process = prepare_literal();
    }
    else if ( generation % generations_per_coverage == 2 ) {
       to_process = prepare_layout();
    }
    else if ( generation % generations_per_coverage == 0 ) {
       to_process = prepare_spelled();
    }

    process_new_forms(to_process);
    prepare_numbers();
    detect_type_word();
  }
  private void parse_tokens_de() {
    Matcher mt = space_dash.matcher(request);
    List<String> parts = new ArrayList<String>();
    AbstractWordSplitter splitter = null;

    try {
      splitter = new GermanWordSplitter(true);
    }
    catch (Exception e){
      System.err.println("Splitter err");
    }


    while ( mt.find() )
      space_delimited.add( new Token(mt.group(1), mt.start(1), mt.end(1), Token.TYPE_SPACE_DELIMITED|Token.TYPE_DEBUG) );


    tokens.addAll(space_delimited);

    List<Token> words_numbers = new ArrayList<Token>();

    for (Token t: space_delimited ) {
      mt = unglue_lat.matcher(t.token().toLowerCase());
      while( mt.find() )
        words_numbers.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_DEBUG) );
    }

    tokens.addAll(words_numbers);

    for (Token t: words_numbers ) {
      mt = pseudo_number.matcher(t.token());
      if ( !mt.matches() ) {
        tokens.add( new Token(t.token(), t.start(), t.end(), Token.TYPE_LATIN) );
        if (splitter != null)
          parts = splitter.splitWord(t.token());
          if (parts.size() > 1){
            int start = t.start();
            int end = t.end();
            composites.add(t);
            for (String par: parts) {
              end = start + par.length();
              Token token = new Token(par, start, end, Token.TYPE_LATIN);
              token.setSplited();
              tokens.add(token);
              space_delimited.add( new Token(par, start, end, Token.TYPE_SPACE_DELIMITED|Token.TYPE_DEBUG) );
              word_breaks.add(end);
              start = end+1;
            }
          }
      }
      else  {
        // numbers delimited with . and ,
        mt = pseudo_number.matcher(t.token()); // neccesary to reinit
        while( mt.find() ) {
          if ( integer.matcher(mt.group(1)).matches() ) {
            tokens.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_NUMBER) );
            tokens.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
          }
          else if ( number.matcher(mt.group(1)).matches() ) {
            tokens.add( new Token(mt.group(1).replace(',', '.'), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_NUMBER) );
            tokens.add( new Token(mt.group(1).replace(',', '.'), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
            tokens.add( new Token(mt.group(1).replace('.', ','), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
            Matcher nw = integer.matcher(t.token());
            while ( nw.find() ) {
              tokens.add( new Token(nw.group(1), t.start() + mt.start(1) + nw.start(), t.start() + mt.start(1) + nw.end(), Token.TYPE_OTHER) );
            }
          }
          else {
            Matcher nw = integer.matcher(t.token());
            while ( nw.find() ) {
              tokens.add( new Token(nw.group(1), t.start() + mt.start(1) + nw.start(), t.start() + mt.start(1) + nw.end(), Token.TYPE_OTHER) );
            }
          }
        }
      }
    }
  }

  private void parse_tokens_en() {
    Matcher mt = space_dash.matcher(request);
    while ( mt.find() ) 
      space_delimited.add( new Token(mt.group(1), mt.start(1), mt.end(1), Token.TYPE_SPACE_DELIMITED|Token.TYPE_DEBUG) );


    tokens.addAll(space_delimited);

    List<Token> words_numbers = new ArrayList<Token>();

    for (Token t: space_delimited ) {
      mt = unglue_lat.matcher(t.token().toLowerCase());
      while( mt.find() )
        words_numbers.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_DEBUG) );
    }

    tokens.addAll(words_numbers);

    for (Token t: words_numbers ) {

      mt = pseudo_number.matcher(t.token());
      if ( !mt.matches() ) {
        tokens.add( new Token(t.token(), t.start(), t.end(), Token.TYPE_LATIN) );
      }
      else  { 
        // numbers delimited with . and ,
        mt = pseudo_number.matcher(t.token()); // neccesary to reinit
        while( mt.find() ) {
          if ( integer.matcher(mt.group(1)).matches() ) {
            tokens.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_NUMBER) );
            tokens.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
          }
          else if ( number.matcher(mt.group(1)).matches() ) {
            tokens.add( new Token(mt.group(1).replace(',', '.'), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_NUMBER) );
            tokens.add( new Token(mt.group(1).replace(',', '.'), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
            tokens.add( new Token(mt.group(1).replace('.', ','), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
            Matcher nw = integer.matcher(t.token());
            while ( nw.find() ) {
              tokens.add( new Token(nw.group(1), t.start() + mt.start(1) + nw.start(), t.start() + mt.start(1) + nw.end(), Token.TYPE_OTHER) );
            }
          }
          else {
            Matcher nw = integer.matcher(t.token());
            while ( nw.find() ) {
              tokens.add( new Token(nw.group(1), t.start() + mt.start(1) + nw.start(), t.start() + mt.start(1) + nw.end(), Token.TYPE_OTHER) );
            }
          }
        } 
      }
    }
  }

  private void parse_tokens_ru() {
    Matcher mt = space_dash.matcher(request);
    while ( mt.find() ) 
      space_delimited.add( new Token(mt.group(1), mt.start(1), mt.end(1), Token.TYPE_SPACE_DELIMITED|Token.TYPE_DEBUG) );


    tokens.addAll(space_delimited);

    // trashy block
    // test case:
    // curl "localhost:8080/sacha/request?q=`echo 'подгузники'|iconv -t utf8 -f iso-8859-1`"
    for ( Token t: space_delimited ) {
      try {
        String decoded = new String( Charset.forName("ISO-8859-1").newEncoder().encode(CharBuffer.wrap(t.token().toCharArray())).array());
        if ( cyrillic.matcher(decoded).matches() )
          tokens.add(new Token(decoded, t.start(), t.end(), Token.TYPE_ISO88591|Token.TYPE_CYRILLIC)); // other scriptings
      }
      catch(Exception e) {
      }
      try {
        String decoded = new String( Charset.forName("CP1252").newEncoder().encode(CharBuffer.wrap(t.token().toCharArray())).array());
        if ( cyrillic.matcher(decoded).matches() )
          tokens.add(new Token(decoded, t.start(), t.end(), Token.TYPE_ISO88591|Token.TYPE_CYRILLIC)); // other scriptings
      }
      catch(Exception e) {
      }
      try {
        // curl "localhost:8080/sacha/request?q=`echo 'подгузники'|iconv -t utf8 -f cp1251`"
        String decoded = new String( Charset.forName("CP1251").newEncoder().encode(CharBuffer.wrap(t.token().toCharArray())).array());
        if ( cyrillic.matcher(decoded).matches() )
          tokens.add(new Token(decoded, t.start(), t.end(), Token.TYPE_CP1251|Token.TYPE_CYRILLIC)); // other scriptings
      }
      catch(Exception e) {
      }
    }

    List<Token> words_numbers = new ArrayList<Token>();

    for (Token t: space_delimited ) {
      mt = unglue.matcher(t.token().toLowerCase());
      while( mt.find() )
        words_numbers.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_DEBUG) );
    }

    tokens.addAll(words_numbers);

    for (Token t: words_numbers ) {

      mt = pseudo_number.matcher(t.token());
      if ( !mt.matches() ) {
        // word parts

        ArrayList<Token> cyr_group = new ArrayList<Token>();        
        ArrayList<Token> lat_group = new ArrayList<Token>();

        Matcher wmt = possible_word_parts.matcher(t.token());

        List<Token> for_glue_cyr = new ArrayList<Token>();
        List<Token> for_glue_lat = new ArrayList<Token>();

        int expect = 0;
        while( wmt.find() ) {
          if ( cyrillic.matcher(wmt.group(1)).matches() ) { // cyrillic
            Token cyr_tk = new Token(wmt.group(1), t.start() + wmt.start(1), t.start() + wmt.end(1), Token.TYPE_CYRILLIC);
            tokens.add( cyr_tk );
            for_glue_cyr.add( cyr_tk );

            // latin strings in jcuken mapped to qwerty
            String qwerty = map(wmt.group(1), jcuken2qwerty);
            if ( qwerty == null ) continue; // odd cyrillic characters
            Matcher latin_mt =  non_cyrillic.matcher(qwerty);
            while ( latin_mt.find() ) {
              Token cyr_lt_tk = new Token(latin_mt.group(1), t.start() + wmt.start(1) + latin_mt.start(1), t.start() + wmt.start(1) + latin_mt.end(1), Token.TYPE_CYRILLIC);

              if ( !for_glue_lat.isEmpty() && cyr_lt_tk.start() != expect ) {
                Token tkn = flushGlued(for_glue_lat, Token.TYPE_LATIN);
                if ( tkn != null )
                  to_group(tkn, lat_group);
              }

              for_glue_lat.add( cyr_lt_tk );
              expect = cyr_lt_tk.end();
            }
          }
          else if ( latin_plus.matcher(wmt.group(1)).matches() ) { //latin + translatable to jcuken
            if ( wmt.group(1).charAt(0) == '"') {
              Token inch = new Token("\"", t.start() + wmt.start(1), t.start() + wmt.start(1) + 1, Token.TYPE_SPECIAL);
              tokens.add(inch);
            }

            String tr = map(wmt.group(1), qwerty2jcuken);
            if ( tr != null) {
              int type = Token.TYPE_LAYOUT|Token.TYPE_DEBUG;
              if ( just_plus.matcher(wmt.group(1)).matches() ) type |= Token.TYPE_PHANTOM;
              Token lt_tk_cyr = new Token(tr, t.start() + wmt.start(1), t.start() + wmt.end(1), type);
              for_glue_cyr.add( lt_tk_cyr );
            }
            Matcher latin_mt =  non_cyrillic.matcher(wmt.group(1));
            while ( latin_mt.find()) {
              // latin substring
              Token lt_tk = new Token(latin_mt.group(1), t.start() + wmt.start(1) + latin_mt.start(1), t.start() + wmt.start(1) + latin_mt.end(1), Token.TYPE_LATIN);
              tokens.add( lt_tk );

              if ( !for_glue_lat.isEmpty() && lt_tk.start() != expect ) {
                Token tkn = flushGlued(for_glue_lat, Token.TYPE_LATIN);
                if ( tkn != null )
                  to_group(tkn, lat_group);
              }

              for_glue_lat.add( lt_tk );
              expect = lt_tk.end();
            }
          }
          else { // other
            tokens.add( new Token(wmt.group(1), t.start() + wmt.start(1), t.start() + wmt.end(1), Token.TYPE_OTHER) );
          }
        }
        if ( !for_glue_cyr.isEmpty() ) {
           int type = Token.TYPE_CYRILLIC;
          if ( for_glue_cyr.size() == 1 && (for_glue_cyr.get(0).type() & Token.TYPE_PHANTOM) != 0 )
            type |= Token.TYPE_PHANTOM;
          Token tkn = flushGlued(for_glue_cyr, type);
          if ( tkn != null )
            to_group(tkn, cyr_group);
        }
        if ( !for_glue_lat.isEmpty() ) {
          Token tkn = flushGlued(for_glue_lat, Token.TYPE_LATIN);
          if ( tkn != null )
            to_group(tkn, lat_group);
        }

        if(!cyr_group.isEmpty())
          translit_groups.add(cyr_group);

        if(!lat_group.isEmpty())
          translit_groups.add(lat_group);
      }
      else  { 
        // numbers delimited with . and ,
        mt = pseudo_number.matcher(t.token()); // neccesary to reinit
        while( mt.find() ) {
          if ( integer.matcher(mt.group(1)).matches() ) {
            tokens.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_NUMBER) );
            tokens.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
          }
          else if ( number.matcher(mt.group(1)).matches() ) {
            tokens.add( new Token(mt.group(1).replace(',', '.'), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_NUMBER) );
            tokens.add( new Token(mt.group(1).replace(',', '.'), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
            tokens.add( new Token(mt.group(1).replace('.', ','), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
            Matcher nw = integer.matcher(t.token());
            while ( nw.find() ) {
              tokens.add( new Token(nw.group(1), t.start() + mt.start(1) + nw.start(), t.start() + mt.start(1) + nw.end(), Token.TYPE_OTHER) );
            }
          }
          else {
            Matcher nw = integer.matcher(t.token());
            while ( nw.find() ) {
              tokens.add( new Token(nw.group(1), t.start() + mt.start(1) + nw.start(), t.start() + mt.start(1) + nw.end(), Token.TYPE_OTHER) );
            }
          }
        } 
      }
    }
  }

  private void parse_tokens_ru_en() {
    Matcher mt = space_dash.matcher(request);
    while ( mt.find() )
      space_delimited.add( new Token(mt.group(1), mt.start(1), mt.end(1), Token.TYPE_SPACE_DELIMITED|Token.TYPE_DEBUG) );


    tokens.addAll(space_delimited);

    // trashy block
    // test case:
    // curl "localhost:8080/sacha/request?q=`echo 'подгузники'|iconv -t utf8 -f iso-8859-1`"
    //cyrillic_latin
    for ( Token t: space_delimited ) {
      try {
        String decoded = new String( Charset.forName("ISO-8859-1").newEncoder().encode(CharBuffer.wrap(t.token().toCharArray())).array());
        if ( cyrillic.matcher(decoded).matches() )
          tokens.add(new Token(decoded, t.start(), t.end(), Token.TYPE_ISO88591|Token.TYPE_CYRILLIC)); // other scriptings
      }
      catch(Exception e) {
      }
      try {
        String decoded = new String( Charset.forName("CP1252").newEncoder().encode(CharBuffer.wrap(t.token().toCharArray())).array());
        if ( cyrillic.matcher(decoded).matches() )
          tokens.add(new Token(decoded, t.start(), t.end(), Token.TYPE_ISO88591|Token.TYPE_CYRILLIC)); // other scriptings
      }
      catch(Exception e) {
      }
      try {
        // curl "localhost:8080/sacha/request?q=`echo 'подгузники'|iconv -t utf8 -f cp1251`"
        String decoded = new String( Charset.forName("CP1251").newEncoder().encode(CharBuffer.wrap(t.token().toCharArray())).array());
        if ( cyrillic.matcher(decoded).matches() )
          tokens.add(new Token(decoded, t.start(), t.end(), Token.TYPE_CP1251|Token.TYPE_CYRILLIC)); // other scriptings
      }
      catch(Exception e) {
      }
    }

    List<Token> words_numbers = new ArrayList<Token>();

    for (Token t: space_delimited ) {
      mt = unglue.matcher(t.token().toLowerCase());
      while( mt.find() )
        words_numbers.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_DEBUG) );
    }

    tokens.addAll(words_numbers);



    for (Token t: words_numbers ) {

      mt = pseudo_number.matcher(t.token());
      if ( !mt.matches() ) {
        // word parts

        ArrayList<Token> cyr_group = new ArrayList<Token>();
        ArrayList<Token> lat_group = new ArrayList<Token>();

        Matcher wmt = possible_word_parts.matcher(t.token());

        List<Token> for_glue_cyr = new ArrayList<Token>();
        List<Token> for_glue_lat = new ArrayList<Token>();

        int expect = 0;
        while( wmt.find() ) {
          String parse_word = wmt.group(1).toLowerCase();
          String ru_cand_word = "";
          String en_cand_word = "";
          Matcher ru_cand_match = cyrillic.matcher(parse_word);
          Matcher en_cand_match = latin.matcher(parse_word);
          while (ru_cand_match.find()){
            ru_cand_word = ru_cand_word + ru_cand_match.group();
          }
          while (en_cand_match.find()){
            en_cand_word = en_cand_word + en_cand_match.group();
          }

          if (ru_cand_word.length() > 0 && en_cand_word.length() > 0) {
            String parse_can_word = "";
            if (en_cand_word.length() >= ru_cand_word.length()) {
              for (Character iw : parse_word.toCharArray()){
                Character cand_iw = cyrillicLatinMap.get(iw);
                if (cand_iw != null)
                  parse_can_word += cand_iw.toString();
                else
                  parse_can_word += iw.toString();
              }
            }
            else {
              for (Character iw : parse_word.toCharArray()){
                Character cand_iw = latinCyrillicMap.get(iw);
                if (cand_iw != null)
                  parse_can_word += cand_iw.toString();
                else
                  parse_can_word += iw.toString();
              }
            }
            parse_word = parse_can_word;
          }

          if ( cyrillic.matcher(parse_word).matches() ) { // cyrillic
            Token cyr_tk = new Token(parse_word, t.start() + wmt.start(1), t.start() + wmt.end(1), Token.TYPE_CYRILLIC);
            tokens.add( cyr_tk );
            for_glue_cyr.add( cyr_tk );

            // latin strings in jcuken mapped to qwerty
            String qwerty = map(parse_word, jcuken2qwerty);
            if ( qwerty == null ) continue; // odd cyrillic characters
            Matcher latin_mt =  non_cyrillic.matcher(qwerty);
            while ( latin_mt.find() ) {
              Token cyr_lt_tk = new Token(latin_mt.group(1), t.start() + wmt.start(1) + latin_mt.start(1), t.start() + wmt.start(1) + latin_mt.end(1), Token.TYPE_CYRILLIC);

              if ( !for_glue_lat.isEmpty() && cyr_lt_tk.start() != expect ) {
                Token tkn = flushGlued(for_glue_lat, Token.TYPE_LATIN);
                if ( tkn != null )
                  to_group(tkn, lat_group);
              }

              for_glue_lat.add( cyr_lt_tk );
              expect = cyr_lt_tk.end();
            }
          }
          else if ( latin_plus.matcher(parse_word).matches() ) { //latin + translatable to jcuken
            if ( parse_word.charAt(0) == '"') {
              Token inch = new Token("\"", t.start() + wmt.start(1), t.start() + wmt.start(1) + 1, Token.TYPE_SPECIAL);
              tokens.add(inch);
            }

            String tr = map(parse_word, qwerty2jcuken);
            if ( tr != null) {
              int type = Token.TYPE_LAYOUT|Token.TYPE_DEBUG;
              if ( just_plus.matcher(parse_word).matches() ) type |= Token.TYPE_PHANTOM;
              Token lt_tk_cyr = new Token(tr, t.start() + wmt.start(1), t.start() + wmt.end(1), type);
              for_glue_cyr.add( lt_tk_cyr );
            }
            Matcher latin_mt =  non_cyrillic.matcher(parse_word);
            while ( latin_mt.find()) {
              // latin substring
              Token lt_tk = new Token(latin_mt.group(1), t.start() + wmt.start(1) + latin_mt.start(1), t.start() + wmt.start(1) + latin_mt.end(1), Token.TYPE_LATIN);
              tokens.add( lt_tk );

              if ( !for_glue_lat.isEmpty() && lt_tk.start() != expect ) {
                Token tkn = flushGlued(for_glue_lat, Token.TYPE_LATIN);
                if ( tkn != null )
                  to_group(tkn, lat_group);
              }

              for_glue_lat.add( lt_tk );
              expect = lt_tk.end();
            }
          }
          else { // other
            tokens.add( new Token(parse_word, t.start() + wmt.start(1), t.start() + wmt.end(1), Token.TYPE_OTHER) );
          }
        }
        if ( !for_glue_cyr.isEmpty() ) {
           int type = Token.TYPE_CYRILLIC;
          if ( for_glue_cyr.size() == 1 && (for_glue_cyr.get(0).type() & Token.TYPE_PHANTOM) != 0 )
            type |= Token.TYPE_PHANTOM;
          Token tkn = flushGlued(for_glue_cyr, type);
          if ( tkn != null )
            to_group(tkn, cyr_group);
        }
        if ( !for_glue_lat.isEmpty() ) {
          Token tkn = flushGlued(for_glue_lat, Token.TYPE_LATIN);
          if ( tkn != null )
            to_group(tkn, lat_group);
        }

        if(!cyr_group.isEmpty())
          translit_groups.add(cyr_group);

        if(!lat_group.isEmpty())
          translit_groups.add(lat_group);
      }
      else  {
        // numbers delimited with . and ,
        mt = pseudo_number.matcher(t.token()); // neccesary to reinit
        while( mt.find() ) {
          if ( integer.matcher(mt.group(1)).matches() ) {
            tokens.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_NUMBER) );
            tokens.add( new Token(mt.group(1), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
          }
          else if ( number.matcher(mt.group(1)).matches() ) {
            tokens.add( new Token(mt.group(1).replace(',', '.'), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_NUMBER) );
            tokens.add( new Token(mt.group(1).replace(',', '.'), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
            tokens.add( new Token(mt.group(1).replace('.', ','), t.start() + mt.start(1), t.start() + mt.end(1), Token.TYPE_OTHER) );
            Matcher nw = integer.matcher(t.token());
            while ( nw.find() ) {
              tokens.add( new Token(nw.group(1), t.start() + mt.start(1) + nw.start(), t.start() + mt.start(1) + nw.end(), Token.TYPE_OTHER) );
            }
          }
          else {
            Matcher nw = integer.matcher(t.token());
            while ( nw.find() ) {
              tokens.add( new Token(nw.group(1), t.start() + mt.start(1) + nw.start(), t.start() + mt.start(1) + nw.end(), Token.TYPE_OTHER) );
            }
          }
        }
      }
    }
  }

  private void to_group(Token token, ArrayList<Token> group) {
    group.add(token);
    tokens.add(token);
  }

  /*
    words starting after position
   */
  private List<Word> words_after(int after) {
    IteratorChain<Word> ic = new IteratorChain<Word>();
    ic.addIterator(specials.iterator());
    ic.addIterator(words.iterator()); // TODO remove multisyn words 

    ArrayList<Word> result = new ArrayList();
    int min_end = request.length();

    while ( ic.hasNext() ) {
      Word w = ic.next();

      if ( w.tokens().get(0).start() >= after ) {
        result.add(w);
        min_end = Math.min(min_end, w.tokens().get(w.tokens().size()-1).end());
      }
    }

    Iterator<Word> it = result.iterator();
    while ( it.hasNext() ) {
      Word w = it.next();
      if ( w.tokens().get(0).start() >= min_end ) 
        it.remove();
    }

    return result;
  }

  public List<Word> words_before(int before) {
    Iterator<Word> ic = words.iterator(); // TODO remove multisyn words 

    ArrayList<Word> result = new ArrayList();
    int max_start = 0;

    while ( ic.hasNext() ) {
      Word w = ic.next();

      if ( w.tokens().get(w.tokens().size()-1).end() <= before ) {
        result.add(w);
        max_start = Math.max(max_start, w.tokens().get(0).start());
      }
    }

    Iterator<Word> it = result.iterator();
    while ( it.hasNext() ) {
      Word w = it.next();
      if ( w.tokens().get(0).end() <= max_start ) 
        it.remove();
    }

    return result;
  }

  private List<Word> words_between(int after, int before){
    Iterator<Word> ic = words.iterator(); // TODO remove multisyn words 

    ArrayList<Word> result = new ArrayList();

    while ( ic.hasNext() ) {
      Word w = ic.next();

      if ( w.tokens().get(0).start() >= after && w.tokens().get(w.tokens().size()-1).end() <= before ) {
        result.add(w);
      }
    }

    return result;
  }

  private void prepare_numbers() {
    /*
      Some black magic for "от года" "до года"
     */
    for ( Word w: words0 ) {
      if ( w.word().toLowerCase().equals("от") || w.word().toLowerCase().equals("до") ) {
        //System.err.printf("x %s\n", w.word());
        for ( Word u: words_after(w.tokens().get(0).end()) ) {
          if ( u.tokens().get(0).token().equalsIgnoreCase("года") && 
               (w.generation() == generation() || (u.generation() == generation() ) ) ) { // at least one word is of current generation
            //System.err.printf("y %s\n", u.word());
            goda_words.add(new Word("1", "1", Arrays.asList(new Token("1", w.tokens().get(0).end(), u.tokens().get(0).start(), Token.TYPE_NUMBER)), Word.TYPE_NUMBER, generation(), new GrsInfo(null, null, lang.getLang())));
            break;
          }
        }
      }
    }

    /*
      intervals
     */
    IteratorChain<Word> ict = new IteratorChain<Word>();
    ict.addIterator(words.iterator());
    ict.addIterator(goda_words.iterator());

    while ( ict.hasNext() ) {
      Word sw = ict.next();
      if ( (sw.type() & Word.TYPE_NUMBER) == 0 ) continue;

      List<Word> range_words = new ArrayList<Word>();

      //System.err.printf("first %s\n", t);
      //System.err.printf(" after %s\n", words_after(t.end()));

      int second_start = sw.tokens().get(0).end();
      Word second = null;

      // possibly skip TO word
      Word to_word = null;
      for (Word w: words_after(sw.tokens().get(0).end()) ) {
        if ( rangeEnd.contains(w.word().toLowerCase()) ) {
          to_word = w;
          second_start = w.tokens().get(0).end();
          break;
        }
      }

      // find range end
      for (Word w: words_after(second_start) ) {
        if ( (w.type() & Word.TYPE_NUMBER) != 0 ) {
          second = w;
          break;
        }
      }

      //System.err.printf("second %s\n", second);

      boolean from = false;
      boolean to = false;

      Word from_word = null;
      for (Word w: words_before(sw.tokens().get(0).start())) {
        if ( rangeStart.contains(w.word().toLowerCase()) ) {
          from = true;
          from_word = w;
          break;
        }
      }

      if ( second == null ) { // next number-word not found, single-ended interval
        boolean is_second = false;
        int first_end = sw.tokens().get(0).start();

        //System.err.printf(" before %d %s\n", t.start(), words_before(t.start()));

        if ( !from )
          for (Word w: words_before(sw.tokens().get(0).start())) {
            if ( rangeEnd.contains(w.word().toLowerCase()) ) {
              first_end = w.tokens().get(0).start();
              to = true;
              to_word = w; // possibly rewrite to_tokens
              break;
            }
          }

        for ( Word w: words_before(first_end) ) {
          if ( (w.type() & Word.TYPE_NUMBER) != 0 ) {
            is_second = true;
            break;
          }
        }
        if ( is_second ) continue; // range found from first number will be used
      }

      /*
      unit
       */
      ArrayList<Word> units = new ArrayList<Word>();
      //System.err.printf(" unit candidates: %s\n", words_after( second != null ? second.end() : t.end() ));
      for (Word u: words_after( second != null ? second.tokens().get(0).end() : sw.tokens.get(0).end() ) ) {
        if ( configuration.getUnits().containsKey( u.word().toLowerCase() ) ) {
          units.add(u);
        }
      }
      if ( units.isEmpty() ) continue; // no unit 

      Double start = null;
      Double end = null;

      if ( second != null || from )
        start = Double.parseDouble(sw.word());
      if ( second != null )
        end = Double.parseDouble(second.word());
      else if ( to )
        end = Double.parseDouble(sw.word());

      range_words.addAll(units);

      List<Token> range_tokens = new ArrayList<Token>();
      boolean new_words = false;

      if ( second == null && !from && !to ) { // no second number, no to/from 
        range_words.add(sw);
        for ( Word w: range_words) {
          range_tokens.addAll(w.tokens());
          if ( w.generation()  == generation() ) new_words = true;
        }

        if ( new_words ) {
          Collections.sort(range_tokens);
          for ( Word unit: units ) {
            Number n = new Number(Double.parseDouble(sw.word()), range_tokens, unit.word().toLowerCase(), unit.orig_form(), generation());
            numbers.add(n);
          }
        }
      }
      else if ( start != null && end != null && start >= end ) { // not a full interval
        if ( to ) { // second number is preceeded with "to"
          range_words.add(to_word);
          range_words.add(second);
          for ( Word w: range_words) {
            range_tokens.addAll(w.tokens());
            if ( w.generation()  == generation() ) new_words = true;
          }
          if ( new_words ) {
            Collections.sort(range_tokens);
            for ( Word unit: units ) {
              Range r = new Range(null, end, range_tokens, unit.word().toLowerCase(), unit.orig_form(), generation());
              ranges.add(r);
            }
          }

        }
        else {
          range_words.add(second);
          for ( Word w: range_words) {
            range_tokens.addAll(w.tokens());
            if ( w.generation()  == generation() ) new_words = true;
          }
          if ( new_words ) {
            Collections.sort(range_tokens);
            for ( Word unit: units ) {
              Number n = new Number(end, range_tokens, unit.word().toLowerCase(), unit.orig_form(), generation());
              numbers.add(n);
            }
          }
        }
      }
      else {
        if ( second != null ) {
          range_words.add(second); // any case
          if ( from_word == null && to_word == null ) { // no interval words around
            boolean new_words_ = false;
            ArrayList<Token> range_tokens_ = new ArrayList<Token>();
            for ( Word w: range_words ) {
              range_tokens_.addAll(w.tokens());
              if ( w.generation()  == generation() ) new_words_ = true;
            }
            if ( new_words ) {
              Collections.sort(range_tokens);
              for ( Word unit: units ) {
                Number n = new Number(end, range_tokens_, unit.word().toLowerCase(), unit.orig_form(), generation());
               numbers.add(n);
              }
            }
          }
        }

        if ( from_word != null ) range_words.add(from_word);
        range_words.add(sw);
        if ( to_word != null ) range_words.add(to_word);

        for ( Word w: range_words ) {
          range_tokens.addAll(w.tokens());
          if ( w.generation()  == generation() ) new_words = true;
        }
        if ( new_words ) {
          Collections.sort(range_tokens);
          for ( Word unit: units ) {
            Range r = new Range(start, end, range_tokens, unit.word().toLowerCase(), unit.orig_form(), generation());
            ranges.add(r);
          }
        }
      }
    }
  }

  private void prepare_words0() {
    Set<String> stopwordsConf = configuration.getStopwords();
    for (Token t: tokens) {
      if ( (t.type() & Token.TYPE_DEBUG) == Token.TYPE_DEBUG ) continue;
      List<Token> tt = new ArrayList();
      tt.add(t);
      if ( (t.type() & Token.TYPE_SPECIAL) == 0 ) {
        int type = Word.TYPE_NONE;
        if ( stopwordsConf.contains(t.token())) type |= Word.TYPE_STOPWORD;
        if ( (t.type() & Token.TYPE_PHANTOM) != 0) type |= Word.TYPE_PHANTOM;
        if ( (t.type() & Token.TYPE_NUMBER) != 0) type |= Word.TYPE_NUMBER;
        String word_lang = null;
        if (cyrillic.matcher(t.token()).matches())
          word_lang = "ru";
        else if (latin.matcher(t.token()).matches())
          word_lang = "en";
        words0.add(new Word(t.token(), t.token(), tt, type, generation(), new GrsInfo(null, word_lang, lang.getLang())));
      }
      else
        specials.add(new Word(t.token(), t.token(), tt, Word.TYPE_SPECIAL, generation(), new GrsInfo(null, null, lang.getLang())));
    }

    for ( Word w: words0 ) {
      if ( w.isStopword() )
        stopwords.add(w);

      if ( w.isPhantom() )
        phantoms.add(w);

      if ( w.isPreposition() )
        prepositions.add(w);
    }
    
    Collections.sort(prepositions);
    
    // dirty hack to avoid random matches of stopwords fixed by layout to something like one-char part of a model
    Iterator<Word> it = words0.iterator();
    while ( it.hasNext() ) {
      Word w = it.next();
      if ( !w.not_layout() )
        for ( Word sw: stopwords ) {
          if ( w.coincide(sw) ) {
            it.remove();
            break;
          }
        }
    }
  }

  private void process_new_forms(Collection<Word> to_lemmatize) throws Exception {
    Collection<Word> lemmas = lemmatize(false, to_lemmatize);
    Collection<Word> syns = syn(lemmas);
    Collection<Word> old_nsyn_lemmas = new ArrayList<Word>();
    for ( Word w: words )
      if ( (w.type() & (Word.TYPE_SYN1 | Word.TYPE_SYN3)) == 0 )
        old_nsyn_lemmas.add(w);
    Collection<Word> multi_syns = fill_multi_syn(lemmas, old_nsyn_lemmas);

    fill_tags(lemmas, syns);

    index_lemmas(lemmas, syns, multi_syns);
  }

  private Collection<Word> prepare_literal() throws Exception {
    // phase1: no layout fixes, no spell
    Collection<Word> to_process = new ArrayList<Word>();
    for ( Word w: words0 )
      if ( w.not_layout() && 
           w.start() < word_breaks.get(Math.min(7 * coverage,word_breaks.size()-1)) && 
           w.start() >=  word_breaks.get(7 * (coverage - 1)) )
        to_process.add(w);

    return to_process;
  }

  private Collection<Word> prepare_layout() throws Exception {
    // phase2: layout fixes
    Collection<Word> to_process = new ArrayList<Word>();
    for ( Word w: words0 )
      if ( !w.not_layout() && 
           w.start() < word_breaks.get(Math.min(7 * coverage,word_breaks.size()-1)) && 
           w.start() >=  word_breaks.get(7 * (coverage - 1)) )
        to_process.add(w);
    remove_conflicts_with_indexed(to_process);
    return to_process;
  }

  private Collection<Word> prepare_spelled() throws Exception {
    // phase3: spelling
    if (configuration.doSpell()) {
      Collection<Word> to_spell = new ArrayList<Word>();
      for ( Word w: words0 )
        if ( (w.start() < word_breaks.get(Math.min(7 * coverage,word_breaks.size()-1))) && 
             (w.start() >=  word_breaks.get(7 * (coverage - 1))) &&
             (w.word().length() > 2) ) {
          //&& (cyrillic.matcher( w.word() ).matches() ||  latin.matcher( w.word() ).matches())
          if (lang.getLang().equals("ru") && cyrillic.matcher( w.word() ).matches())
            to_spell.add(w);
          else if (lang.getLang().equals("ru_en") && (cyrillic.matcher( w.word() ).matches() ||  latin.matcher( w.word() ).matches()) )
            to_spell.add(w);
          else if ((lang.getLang().equals("en") || lang.getLang().equals("de")) && latin.matcher( w.word() ).matches())
            to_spell.add(w);
        }
      remove_conflicts_with_indexed(to_spell);
      if ( !to_spell.isEmpty() ) {
        long start = System.currentTimeMillis();
        Collection<Word> spelled;
        synchronized(spell_process) {
          spelled = spell(to_spell);
        }
        return spelled;
      }
    }
    return new ArrayList<Word>();
  }

  // remove words which intersect with indexed
  private void remove_conflicts_with_indexed(Collection<Word> to_lemmatize) {
    Iterator<Word> it = to_lemmatize.iterator();
    while( it.hasNext() ) {
      Word w0 = it.next();
      for ( Word w: words )
        if ( ( (w.type() & (Word.TYPE_SYN1|Word.TYPE_SYN2|Word.TYPE_SYN3)) == 0) &&
             w.getPostingListLength() > 10 &&
             w.word().length() > 3 && // do not drop a word because of some tiny laout thing
             w.intersects(w0) ) {
          it.remove();
          break;
        }
    }
  }

  private void init_processes() {
    spell_process = lang.getSpeller();
    stemmer = lang.getStemmer();
  }

  public  void clear_processes() {
    spell_process = null;
    stemmer = null;
  }

  Collection<Word> spell(Collection<Word> to_spell) throws Exception {
    Collection<Word> spelled = new ArrayList<Word>();
    for (Word w: to_spell) {
      if (w.word().length() > 3) {
        String word_in = w.word().toLowerCase();
        String res = spell_process.spell(w);

        int j = res.indexOf(':');

        if (j != -1 && res.length() > j + 2) {
          Matcher spell_token_mt = spelled_word.matcher(res.substring(j + 2));

          while (spell_token_mt.find()) {
            String s = spell_token_mt.group(1).toLowerCase();
            if (s.indexOf(' ') != -1 || s.indexOf('-') != -1) continue;
            Token t = w.tokens().get(0);
            ArrayList<Token> stk = new ArrayList<Token>();
            stk.add(new Token(s, t.start(), t.end(), t.type()));
            int lev_dist = distance(word_in, s);
            if (lev_dist <= 4) {
              String word_lang = null;
              if (cyrillic.matcher(s).matches())
                word_lang = "ru";
              else if (latin.matcher(s).matches())
                word_lang = "en";
              Word word = new Word(s,
                s,
                stk,
                Word.TYPE_SPELL,
                generation(),
                new GrsInfo(null, word_lang, lang.getLang())
                );
              spelled.add(word);
            }
          }
        }
      }
    }
    return spelled;
  }

  // lemmatize all tokens, spelled
  List<Word> lemmatize(boolean known_only, Collection<Word>... lists) throws Exception {
    List<Word> to_lemmer = new ArrayList<Word>();
    List<Word> lemmas = new ArrayList<Word>();
    for ( Collection<Word> cw: lists)
      for ( Word w: cw) {
        if ( (w.type() & Word.TYPE_KNOWN) != 0) continue;
        if ( w.isStopword() ) {
          lemmas.add(w);
          continue;
        }

      /*  List<Word> resolved = _lemmatize(w);
        if ( resolved != null) {
          w.set_type(Word.TYPE_KNOWN);
          System.err.println(resolved+"%");
          lemmas.addAll(resolved);
        }*/
        else {
          if ( (lang.getLang().equals("ru") && !cyrillic.matcher(w.word()).matches() ) ||
            ((lang.getLang().equals("en") || lang.getLang().equals("de")) && !latin.matcher( w.word() ).matches() )  ||
            (lang.getLang().equals("ru_en") && !cyrillic.matcher( w.word() ).matches() && !latin.matcher( w.word() ).matches()) ) { // добавляем все как есть остальные в стеммер отправляются

            w.set_type(Word.TYPE_KNOWN);
            lemmas.add(new Word(w.word(), w.orig_form(), w.tokens(), w.type()|Word.TYPE_AS_IS, generation(), new GrsInfo(null, null, lang.getLang())));
          }
          else {// cyrillic -> mystem
            if ( w.word().indexOf(' ') == -1 && w.word().indexOf('-') == -1 ){
              to_lemmer.add(w);
            }
          }
        }
      }
    
    if ( !known_only && !to_lemmer.isEmpty() )
      synchronized(stemmer) {
        lemmas.addAll(stemmer.getLemmas(to_lemmer, generation()));
      }
//    System.err.println(lemmas);
    return lemmas;
  }

  List<Word> _lemmatize(Word word) throws Exception {
    Collection<WordInfo> wis = configuration.getWords().get(word.word().toLowerCase());
    if ( wis != null ) {
      List<Word> lemmas = new ArrayList<Word>();
      for ( WordInfo wi: wis ) {
        lemmas.add(new Word( wi.lemma, word.orig_form(), word.tokens(), word.type(), generation(), new GrsInfo(wi.grs, wi.word_lang, lang.getLang())));
      }
      return lemmas;
    }
    return null;
  }

  List<Word> syn(Collection<Word> lemmas) throws Exception {
    Map<WordSynKey, Synonym[]> synonymsConf = configuration.getSynonyms();
    ArrayList<Word> syn = new ArrayList<Word>();
    for (Word w: lemmas) {
      WordSynKey wordSynKey = new WordSynKey(w.word(), w.grs(),  null); // вслучаии леммы
      ArrayList<Synonym> synonyms = new ArrayList<Synonym>();
      Synonym[] synonyms_lemma = synonymsConf.get(wordSynKey);
      WordSynKey wordSynKeyNoLemma = new WordSynKey(w.word(), w.grs(), true);
      Synonym[] synonyms_no_lemma = synonymsConf.get(wordSynKeyNoLemma);

      if (synonyms_lemma != null) {
        synonyms.addAll(Arrays.asList(synonyms_lemma));
      }
      if (synonyms_no_lemma != null) {
        synonyms.addAll(Arrays.asList(synonyms_no_lemma));
      }

      for (Synonym s : synonyms) {
        if (s.lemma.length() > 2 || s.weight > 0.5) {
          if (w.word().length() > 2 || s.weight > 0.8) {
            int type = w.type() | Word.TYPE_SYN1;
            if (integer.matcher(s.lemma).matches() || number.matcher(s.lemma).matches())
              type |= Word.TYPE_NUMBER;
            else
              type &= ~Word.TYPE_NUMBER;
            String[] grs = null;
            if (s.grs != null)
              grs = s.grs;
            String word_lang = null; //TODO сделать чтобы были в конфигах
            if (cyrillic.matcher(s.lemma).matches())
              word_lang = "ru";
            else if (latin.matcher(s.lemma).matches())
              word_lang = "en";
            Word syn_word = new Word(s.lemma, w.orig_form(), w.tokens(), type, generation(), new GrsInfo(grs, word_lang, lang.getLang()));
            if (s.no_lemma != null)
              syn_word.setNoLemma(s.no_lemma);
            syn.add(syn_word);
          }
        }
      }
    }
    return syn;
  }

  private Boolean checkMultiSyn(Map<String, Word> in_words, ConfMultiSyn ws){

    if (ws.words.size() != in_words.size()){
      return false;
    }

    for(int i=0; i<ws.words.size()-1; i++){
      Word after = in_words.get(ws.words.get(i));
      Word before = in_words.get(ws.words.get(i+1));
      if ( before.tokens().get(before.tokens().size()-1).end() < after.tokens().get(0).start()) // провекра строго следования
        return  false;
      List<Word> wc = words_between(after.tokens().get(after.tokens().size()-1).end(), before.tokens().get(0).start());
      if (!wc.isEmpty()){
        for (Word word_cand : wc){
          if (!word_cand.isStopword())
            return false;
        }
      }
    }

    return true;
  }


  List<Word> fill_multi_syn(Collection<Word>... lists) throws Exception {
    List<ConfMultiSyn> confMultiSyn = configuration.getConfigMultiSyn();
    ArrayList<Word> multiSyn = new ArrayList<Word>();
    int index_ws = 0;
    for (ConfMultiSyn ws: confMultiSyn) {
      Map<String, Word> in_words = new HashMap<String, Word>();
      for (Collection<Word> lemmas : lists) {
        for (Word w : lemmas) {
          if ( (w.type() & (Word.TYPE_SYN1|Word.TYPE_SYN2|Word.TYPE_SYN3)) == 0 && ws.words.contains(w.word().toLowerCase()) ) {
            in_words.put(w.word().toLowerCase(), w);
          }
        }
      }
      if ( checkMultiSyn(in_words, ws) ) {
        boolean new_words = false;
        for ( Word w: in_words.values() )
          if ( generation() == w.generation() ) {
            new_words = true;
            break;
          }
        if ( !new_words ) continue;

        List<Token> syn_tokens = new ArrayList<Token>();
        for (Word w : in_words.values())
          syn_tokens.addAll(w.tokens());
        ArrayList<Word> syn_words = new ArrayList<Word>();
        for (String w : ws.syns) {
          String word_lang = null; //TODO сделать чтобы были в конфигах
          if (cyrillic.matcher(w).matches())
            word_lang = "ru";
          else if (latin.matcher(w).matches())
            word_lang = "en";
          Word nw = new Word(w, w, syn_tokens, Word.TYPE_SYN3, generation(), new GrsInfo(null, word_lang, lang.getLang()));
          nw.setMultiSynIndex(index_ws, (double)(ws.words.size())/ws.syns.length);
          syn_words.add(nw);
        }
        Collections.sort(syn_tokens); // Сортировка токинов для мультисинонима, что бы правильно определять его начало и конец
        MultiSyn ms = new MultiSyn(confMultiSyn.get(index_ws), null, syn_words);
        multi_syn.put(index_ws, ms);
        multiSyn.addAll(syn_words);
      }
      index_ws++;
    }
    return multiSyn;

  }

  void fill_tags(Collection<Word>... lists) throws Exception {
    for ( ConfTag source_tag : configuration.getTags() ) {
      boolean is_found = true;
      boolean new_words = false;

      ArrayList<Word> tag_words = new ArrayList<Word>();

      for (String part : source_tag.lemmas) {
        boolean found_part = false;
        for ( Collection<Word> col_w: lists ) {
          for ( Word w: col_w ) {
            if (w.word().toLowerCase().equals(part.toLowerCase())) {
              found_part = true;
              tag_words.add(w);
              if ( generation() == w.generation() ) 
                new_words = true;
              break;
            }
          }
          if (found_part) break;
        }
        if ( !found_part ) {
          is_found = false;
          break;
        }
      }

      if ( is_found && new_words ) {
        for ( Word w : tag_words)
          w.tokens().get(0).set_indexed();

        Tag found_tag = new Tag(source_tag, tag_words);

        for ( String category : found_tag.getCats() ) {
          Integer ourCat = configuration.getCategoriesMap().getOurIdFromShop(category);
          ArrayList<Tag> val = cats_n_tags.get(ourCat);
          if (val == null) {
            val = new ArrayList<Tag>();
            cats_n_tags.put(ourCat, val);
          }

          val.add(found_tag);
        }
      }
    }
  }

  private abstract class Type {
    protected int score;
    
    protected Boolean with_adjective = false;
    
    protected ArrayList<Word> words = new ArrayList<Word>();

    public abstract void add(Word w);

    public abstract int compareTo(Type other)  throws Exception;
    
    public int getScore() {
     return score; 
    }	
  }
  
  private class TypeRu extends Type {	
    public TypeRu() {
      score = 10;
    }

    public void add(Word w) {
      if ( w.getNounType() != null &&  w.getNounType() < score )
      {
        if (score != 8)
          score = w.getNounType();
        else
          with_adjective = true;
      }

      words.add(w);
    }
    
    public int compareTo(TypeRu other)  throws Exception {
        if ( with_adjective.compareTo(other.with_adjective) < 0 || score < other.score || ( (score == other.score) && (words.get(0).start() <= other.words.get(0).start()) ) )
          return 1;
        else
          return -1;
    }
    
    public int compareTo(Type other) throws Exception {
      if (other instanceof TypeRu)
        return compareTo((TypeRu)other);
      else 
        throw new Exception("unable to compare");
    }
	
    public String toString() {
      return String.format("score: %d words: %s", score, words);
    }
  }
  
  private class TypeEn extends Type {
    public TypeEn() {
      score = 0;
    }
    
    public void add(Word w) {
      if ( w.getNounType() != null )
        score = 1;

      words.add(w);
    }
    
    public int compareTo(TypeEn other)  throws Exception {
      if (score == 1 && (words.get(0).start() > other.words.get(0).start()) )
        return 1;
      else
        return -1;        
    }
	
    public int compareTo(Type other)  throws Exception     {
      if (other instanceof TypeEn)
        return compareTo((TypeEn)other);
      else 
        throw new Exception("unable to compare");
    }

    public String toString() {
      return String.format("score: %d words: %s", score, words);
    }
  }


  public Collection<Word> words_before_prepositions() {
    ArrayList<Word> pretype_words = new ArrayList<Word>(); 

    if (!prepositions.isEmpty()) {
      Word preposit = prepositions.iterator().next();

      for ( Word w : words ) 
        if (w.withGrammar() && preposit.start() > w.end())
          pretype_words.add(w);
    }
    else {
      for ( Word w : words ) 
        if (w.withGrammar())
          pretype_words.add(w);
    }

    return pretype_words;
  }
  
  public void detect_type_word() throws Exception {
    ArrayList<Type> type_words = new ArrayList<Type>();
    for (Word w: words_before_prepositions()) {
      Type temp = null;

      for (Type typ : type_words)
        /*
         * мы тут конечно добавляем в список кучу лишнего, но есть шанс, 
         * что мы добавим все слова и формы, по которым этот тип может быть найден 
         */
        if ( w.coincide(typ.words.get(0)) ) {
          temp = typ;
          break;
        }

      if ( temp == null ) {
        if (configuration.getLang().equals("en"))
          temp = new TypeEn();
        else
          temp = new TypeRu();
        
        type_words.add(temp);
      }

      temp.add(w);
    }
    
    Type winner = null;

    for (Type type: type_words) {
      if (winner == null || type.compareTo(winner) == 1)
        winner = type;
    }

    if (winner != null) {
      for (Word w : winner.words)
        if ( w.getNounType() != null ) {
          w.setType(generation);
        }
    }
  }
  
  

  Set<String> added = new HashSet<String>();
  void index_lemmas(Collection<Word>...lists) throws Exception {
    for ( Collection<Word> cw: lists )
      for ( Word b: cw )
        if ( added.add(b.word().toLowerCase()) || b.isMultiSyn() ) {
          words.add(b);
          if (b.word().contains("ß") || b.word().contains("ue") || b.word().contains("ae") || b.word().contains("oe"))
            words.add(new Word(b.word().replaceAll("ß", "ss").replaceAll("ue", "ü").replaceAll("ae", "ä").replaceAll("oe", "ö"),
              b.orig_form(), b.tokens(), b.type(), b.generation(), b.grs_info_raw()));
        }
  }

  private static Map<Character,Character> qwerty2jcuken;
  private static Map<Character,Character> jcuken2qwerty;
  static {
    char[][] qwerty_jcuken_map = 
    {{'Q','Й'},
        {'W','Ц'},
        {'E','У'},
        {'R','К'},
        {'T','Е'},
        {'Y','Н'},
        {'U','Г'},
        {'I','Ш'},
        {'O','Щ'},
        {'P','З'},
        {'{','Х'},
        {'}','Ъ'},
        {'A','Ф'},
        {'S','Ы'},
        {'D','В'},
        {'F','А'},
        {'G','П'},
        {'H','Р'},
        {'J','О'},
        {'K','Л'},
        {'L','Д'},
        {':','Ж'},
        {'"','Э'},
        {'Z','Я'},
        {'X','Ч'},
        {'C','С'},
        {'V','М'},
        {'B','И'},
        {'N','Т'},
        {'M','Ь'},
        {'<','Б'},
        {'>','Ю'},
        {'q','й'},
        {'w','ц'},
        {'e','у'},
        {'r','к'},
        {'t','е'},
        {'y','н'},
        {'u','г'},
        {'i','ш'},
        {'o','щ'},
        {'p','з'},
        {'[','х'},
        {']','ъ'},
        {'a','ф'},
        {'s','ы'},
        {'d','в'},
        {'f','а'},
        {'g','п'},
        {'h','р'},
        {'j','о'},
        {'k','л'},
        {'l','д'},
        {';','ж'},
        {'\'','э'},
        {'z','я'},
        {'x','ч'},
        {'c','с'},
        {'v','м'},
        {'b','и'},
        {'n','т'},
        {'m','ь'},
        {',','б'},
        {'.','ю'}};
    char[][] jcuken_qwerty_map = 
    {{'Й','Q'},
        {'Ц','W'},
        {'У','E'},
        {'К','R'},
        {'Е','T'},
        {'Н','Y'},
        {'Г','U'},
        {'Ш','I'},
        {'Щ','O'},
        {'З','P'},
        {'Х','{'},
        {'Ъ','}'},
        {'Ф','A'},
        {'Ы','S'},
        {'В','D'},
        {'А','F'},
        {'П','G'},
        {'Р','H'},
        {'О','J'},
        {'Л','K'},
        {'Д','L'},
        {'Ж',':'},
        {'Э','"'},
        {'Я','Z'},
        {'Ч','X'},
        {'С','C'},
        {'М','V'},
        {'И','B'},
        {'Т','N'},
        {'Ь','M'},
        {'Б','<'},
        {'Ю','>'},
        {'й','q'},
        {'ц','w'},
        {'у','e'},
        {'к','r'},
        {'е','t'},
        {'н','y'},
        {'г','u'},
        {'ш','i'},
        {'щ','o'},
        {'з','p'},
        {'х','['},
        {'ъ',']'},
        {'ф','a'},
        {'ы','s'},
        {'в','d'},
        {'а','f'},
        {'п','g'},
        {'р','h'},
        {'о','j'},
        {'л','k'},
        {'д','l'},
        {'ж',';'},
        {'э','\''},
        {'я','z'},
        {'ч','x'},
        {'с','c'},
        {'м','v'},
        {'и','b'},
        {'т','n'},
        {'ь','m'},
        {'б',','},
        {'ю','.'}};

    qwerty2jcuken = new HashMap<Character,Character>();
    jcuken2qwerty = new HashMap<Character,Character>();

    for (int i = 0; i < qwerty_jcuken_map.length; i++ )
      qwerty2jcuken.put(qwerty_jcuken_map[i][0], qwerty_jcuken_map[i][1]);
    for (int i = 0; i < jcuken_qwerty_map.length; i++ )
      jcuken2qwerty.put(jcuken_qwerty_map[i][0], jcuken_qwerty_map[i][1]);
  }

  private static String deAccent(String s) {
    String nfdNormalizedString = Normalizer.normalize(s, Normalizer.Form.NFD); 
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(nfdNormalizedString).replaceAll("");
  }

  private static String map(String s, Map<Character,Character> map) {
    StringBuilder sb = new StringBuilder();
    //s = deAccent(s);
    for (int i = 0; i < s.length(); i++) {
      Character c = map.get(s.charAt(i));
      if ( c != null )
        sb.append(c);
      else
        return null;
    }

    return sb.toString().toLowerCase();
  }

  private Token flushGlued(List<Token> for_glue, int type) {
    Token tkn = null;
    if ( for_glue.size() > 1 || ( for_glue.get(0).type() & type) != type ) {
      String t = "";
      int start = for_glue.get(0).start();
      int end = for_glue.get(for_glue.size() - 1).end();
      for ( Token part: for_glue )
        t += part.token();
      if ( configuration.doLayout() ) {
        tkn = new Token(t, start, end, type|Token.TYPE_LAYOUT);
      }
    }
    for_glue.clear();
    return tkn; 
  }

  //проверяем группы слов, которые мы получили транслитом, и если какая то часть группы не была найдена, то мы считаем всю группу ненайденной
  public void validate() {
    for (ArrayList<Token> group : translit_groups) {
      boolean found = true;

      for (Token check : group) {
        if ( check.is_indexed() )
          check.validate();
        else {
          found = false;
          break;
        }
      }
      if (!found) {
        for (Token check : group)
          check.invalidate();
      }
    }
  }

  private int distance(String a, String b) {
    a = a.toLowerCase();
    b = b.toLowerCase();
    // i == 0
    int [] costs = new int [b.length() + 1];
    for (int j = 0; j < costs.length; j++)
      costs[j] = j;
    for (int i = 1; i <= a.length(); i++) {
      // j == 0; nw = lev(i - 1, j)
      costs[0] = i;
      int nw = i - 1;
      for (int j = 1; j <= b.length(); j++) {
        int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
        nw = costs[j];
        costs[j] = cj;
      }
    }
    return costs[b.length()];
  }
}




