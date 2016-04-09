/*
 * Copyright 2016: smanna@cpp.edu
 * Please do not change any public method's header.
 * Feel free to include your own methods/variables as required.
 */
import java.util.HashSet;
import java.util.Vector;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineProcessor {
  private static HashSet<Pattern> patternPhoneNumber = new HashSet<Pattern>();
  private static HashSet<Pattern> patternEmail = new HashSet<Pattern>();
  private static HashSet<Pattern> patternEmailTag = new HashSet<Pattern>();
  private static Pattern patternNonHTML = Pattern.compile(">([^><]+)<|^([^><]+)<|>([^><]+)$|^([^><]+)$");
  private static Pattern patternHTML = Pattern.compile("<([^><]+)>|^([^><]+)>|<([^><]+)$");
  static {
    patternPhoneNumber.add(Pattern.compile("([2-9]\\d{2})\\s*[.-]?\\s*(\\d{3})\\s*[.-]?\\s*(\\d{4})(?:\\D|$)"));
    patternPhoneNumber.add(Pattern.compile("[(]\\s*([2-9]\\d{2})\\s*[)]\\s*[.-]?\\s*(\\d{3})\\s*[.-]?\\s*(\\d{4})(?:\\D|$)"));

    patternEmail.add(Pattern.compile("((?:[a-z0-9!#$%&'*\\+\\-\\/=?^_`{|}~.]|[ ]dot[ ])+)(?:[ ][(]?at[)]?[ ]|[ ]?[@][ ]?|&#x40;)([a-z0-9](?:[a-z0-9-]|[.]|[ ]dot[ ])*(?:[.]|[ ]dot[ ])(?:[a-z0-9-]|[.]|[ ]dot[ ])*[a-z0-9])")); // hostname based

    patternEmailTag.add(Pattern.compile("(?:mailto\\:|src=)(?:[ ]|%22)*([a-z0-9!#$%&'*\\+\\-\\/=?^_`{|}~.]+)[@]([a-z0-9][a-z0-9.-]*[.][a-z0-9.-]*[a-z0-9])(?:[ ]|%22)*")); // hostname based
  }

  public LineProcessor() {
    // Constructor
    // TODO(student): Feel free to add your initialization code, if any.
  }

  private HashSet<String> findEmails(String line) {
    // TODO(student): Write your regular expression based email
    // extractor code here. Feel free to add as many as private method
    // you need.
    HashSet<String> result = new HashSet<String>();

    // Find email in plain text
    Iterator<String> itText = extractText(line).iterator();
    while (itText.hasNext()) {
      String nonHTMLString = itText.next();
      Iterator<Pattern> itPattern = patternEmail.iterator();
      while (itPattern.hasNext()) {
        Pattern pattern = itPattern.next();

        Matcher matcher = pattern.matcher(nonHTMLString.toLowerCase());
        while (matcher.find()) {
          result.add(formatEmail(matcher.group(1), matcher.group(2)));
        }
      }
    }

    // Find email in HTML tag
    Iterator<Pattern> itPatternEmailTag = patternEmailTag.iterator();
    String htmlString = findHTMLTag(line);

    while(itPatternEmailTag.hasNext()) {
      Pattern pattern = itPatternEmailTag.next();

      Matcher matcher = pattern.matcher(htmlString.toLowerCase());
      while (matcher.find()) {
        result.add(formatEmail(matcher.group(1), matcher.group(2)));
      }
    }

    return result;  // returning empty => no result found
  }

  private HashSet<String> findPhoneNumbers(final String line) {
    // TODO(student): Write your regular expression based phone
    // number extractor code here. Feel free to add as many as private method
    // you need.
    HashSet<String> result = new HashSet<String>();

    
    String nonHTMLString = findText(line);
    Iterator<Pattern> itPattern = patternPhoneNumber.iterator();
    while (itPattern.hasNext()) {
      Pattern pattern = itPattern.next();

      Matcher matcher = pattern.matcher(nonHTMLString);
      while (matcher.find()) {
        result.add(formatPhoneNumber(matcher.group(1), matcher.group(2), matcher.group(3)));
      }
    }

    return result;  // returning empty => no result found
  }

  private String formatPhoneNumber(String part1, String part2, String part3) {
    return part1 + "-" + part2 + "-" + part3;
  }

  private String formatEmail(String localPart, String hostName) {
    return localPart.replaceAll(" dot ", ".").replaceAll(" ", "") + "@" + hostName.replaceAll(" dot ", ".").replaceAll(" ", "");
  }

  private String findText(final String line) {
    String result = "";

    Matcher nonHTMLMatcher = patternNonHTML.matcher(line);
    while (nonHTMLMatcher.find()) {
      int groupCount = nonHTMLMatcher.groupCount();
      for (int i = 1; i < groupCount+1 ; i ++) {
        if (nonHTMLMatcher.group(i) == null) {
          continue;
        }
        result += nonHTMLMatcher.group(i);
      }
    }

    return result;
  }

  private String findHTMLTag(final String line) {
    String result = "";

    Matcher htmlMatcher = patternHTML.matcher(line);
    while (htmlMatcher.find()) {
      int groupCount = htmlMatcher.groupCount();
      for (int i = 1; i < groupCount+1 ; i ++) {
        if (htmlMatcher.group(i) == null) {
          continue;
        }
        result += htmlMatcher.group(i);
      }
    }

    return result;
  }

  private HashSet<String> extractText(final String line) {
    HashSet<String> result = new HashSet<String>();

    Matcher nonHTMLMatcher = patternNonHTML.matcher(line);
    while (nonHTMLMatcher.find()) {
      int groupCount = nonHTMLMatcher.groupCount();
      for (int i = 1; i < groupCount+1 ; i ++) {
        if (nonHTMLMatcher.group(i) == null) {
          continue;
        }
        result.add(nonHTMLMatcher.group(i));
      }
    }

    return result;    
  }

  public HashSet<String> processLine(String line) {
    // You should not be modifying this method.
    HashSet<String> email = findEmails(line);
    HashSet<String> phone = findPhoneNumbers(line);
    HashSet<String> email_n_phones = new HashSet<String>();
    for (String e : email) {
      email_n_phones.add("e\t" + e);
    }
    for (String p : phone) {
      email_n_phones.add("p\t" + p);
    }
    return email_n_phones;
  }
}
