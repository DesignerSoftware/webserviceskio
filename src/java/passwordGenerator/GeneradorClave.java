package passwordGenerator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thali
 */
public class GeneradorClave {
           /** Minimum password length = 6 */
       public static final int MIN_PASSWORD_LENGTH = 8;
       /** Maximum password length = 8 */
       public static final int MAX_PASSWORD_LENGTH = 10;

       /** Uppercase characters A-Z */
       public static final char[] UPPERS = new char[26];
       /** Lowercase characters a-z */
       public static final char[] LOWERS = new char[26];
       /**
        * Printable non-alphanumeric characters, excluding space.
        */
       public static final char[] SPECIALS = new char[32];
       public static final char[] DIGITS = new char[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

       static {
           // Static initializer block for populating arrays
           int U = 'A';
           int l = 'a';
           int d = '0';
           for (int i = 0; i < 26; i++) {
               UPPERS[i] = (char) (U + i);
               LOWERS[i] = (char) (l + i);
               if (i < 10) {
                   DIGITS[i] = (char) (d + i);
               }
           }
           int p = 0;
           for (int s = 33; s < 127; s++) {
               char specialChar = (char) 32;

               if (s >= 'a' && s <= 'z')
                   s = 'z' + 1; // jump over 'a' to 'z'
               else if (s >= 'A' && s <= 'Z')
                   s = 'Z' + 1; // jump over 'A' to 'Z'
               else if (s >= '0' && s <= '9')
                   s = '9' + 1; // jump over '0' to '9'

               specialChar = (char) s;
               SPECIALS[p] = specialChar;
               p++;
           }
       }

       public static String generatePassword() {
           List<char[]> activeSets = new ArrayList<char[]>(4);
           List<char[]> inactiveSets = new ArrayList<char[]>(4);

           activeSets.add(UPPERS);
           activeSets.add(LOWERS);
           activeSets.add(SPECIALS);
           activeSets.add(DIGITS);

           SecureRandom random = new SecureRandom();

           int passwordLength = 5 + random.nextInt(3);
//           StringBuffer password = new StringBuffer(passwordLength + 1);
           StringBuilder password = new StringBuilder(passwordLength + 1);

           for (int p = 0; p <= passwordLength; p++) {
               char[] randomSet = null;
               if (activeSets.size() > 1) {
                   int rSet = random.nextInt(activeSets.size());
                   randomSet = activeSets.get(rSet);
                   inactiveSets.add(randomSet);
                   activeSets.remove(rSet);
               } else {
                   randomSet = activeSets.get(0);
                   inactiveSets.add(randomSet);
                   activeSets.clear();
                   activeSets.addAll(inactiveSets);
                   inactiveSets.clear();
               }
               int rChar = random.nextInt(randomSet.length);
               char randomChar = randomSet[rChar];
               password.append(randomChar);
           }

           return password.toString();
       }
}
