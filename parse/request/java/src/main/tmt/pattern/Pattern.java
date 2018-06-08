package tmt.pattern;

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

import com.google.gson.Gson;

public class Pattern {
  
  public Pattern(String request_, SearchConfiguration configuration_, SearchLanguage lang_) {
  }
}