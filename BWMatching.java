import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class BWMatching {
    public static int[] count;
    public static int n;
    public static int[][] counts_before;
    class FastScanner {
        StringTokenizer tok = new StringTokenizer("");
        BufferedReader in;

        FastScanner() throws FileNotFoundException {
            in = new BufferedReader(new InputStreamReader(System.in));//new FileInputStream("millions.txt")
        }

        String next() throws IOException {
            while (!tok.hasMoreElements())
                tok = new StringTokenizer(in.readLine());
            return tok.nextToken();
        }

        int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    }

    private static int charToNumber(char c) {
        switch (c) {
            case 'A':
                return 1;
            case 'C':
                return 2;
            case 'G':
                return 3;
            case 'T':
                return 4;
            default:
                return 0;         // 0 corresponds to '$';
        }
    }
    // Preprocess the Burrows-Wheeler Transform bwt of some text
    // and compute as a result:
    //   * starts - for each character C in bwt, starts[C] is the first position
    //       of this character in the sorted array of
    //       all characters of the text.
    //   * occ_count_before - for each character C in bwt and each position P in bwt,
    //       occ_count_before[C][P] is the number of occurrences of character C in bwt
    //       from position 0 to position P inclusive.
    private void PreprocessBWT(String bwt) {
        n = bwt.length();
        count = new int[5];
//        char[] c = {'$', 'A', 'C', 'G', 'T'};
        counts_before = new int[5][n + 1];
        for (int i = 1; i <= n; i++)
        {
            int index = charToNumber(bwt.charAt(i - 1));
            for (int j = 0; j < 5; j++)
                counts_before[j][i] = counts_before[j][i - 1];
            counts_before[index][i]++;
            count[index]++;
        }
        for (int i = 1; i < 5; i++)
            count[i] += count[i - 1];
        for (int i = 4; i > 0; i--)
            count[i] = count[i - 1];
        count[0] = 0;
        
    }

    // Compute the number of occurrences of string pattern in the text
    // given only Burrows-Wheeler Transform bwt of the text and additional
    // information we get from the preprocessing stage - starts and occ_counts_before.
    int CountOccurrences(String pattern, String bwt) {
        int top = 0, bottom = n - 1;
        String pattern1 = pattern;
        while (top <= bottom)
        {
            if (pattern1.length() > 0)
            {
                char symbol = pattern1.charAt(pattern1.length() - 1);
                int index = charToNumber(symbol);
                pattern1 = pattern1.substring(0, pattern1.length() - 1);
                if (counts_before[index][bottom + 1] > counts_before[index][top])
                {
                    int newtop = count[index] + counts_before[index][top];
                    int newbottom = count[index] + counts_before[index][bottom + 1] - 1;
                    top = newtop;
                    bottom = newbottom;
                } else
                    return 0;
            }
            else 
                return (bottom - top + 1);
        }
        return 0;
    }

    static public void main(String[] args) throws IOException {
        new BWMatching().run();
    }

    public void print(int[] x) {
        for (int a : x) {
            System.out.print(a + " ");
        }
        System.out.println();
    }

    public void run() throws IOException {
        FastScanner scanner = new FastScanner();
        String bwt = scanner.next();
        // Start of each character in the sorted list of characters of bwt,
        // see the description in the comment about function PreprocessBWT
//        Map<Character, Integer> starts = new HashMap<Character, Integer>();
        // Occurrence counts for each character and each position in bwt,
        // see the description in the comment about function PreprocessBWT
//        Map<Character, int[]> occ_counts_before = new HashMap<Character, int[]>();
        // Preprocess the BWT once to get starts and occ_count_before.
        // For each pattern, we will then use these precomputed values and
        // spend only O(|pattern|) to find all occurrences of the pattern
        // in the text instead of O(|pattern| + |text|).
        PreprocessBWT(bwt);
        int patternCount = scanner.nextInt();
        String[] patterns = new String[patternCount];
        int[] result = new int[patternCount];
        for (int i = 0; i < patternCount; ++i) {
            patterns[i] = scanner.next();
            result[i] = CountOccurrences(patterns[i], bwt);
        }
        print(result);
    }
}
