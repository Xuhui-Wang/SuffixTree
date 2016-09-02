import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
class BWMatching {
    public static int[] count;
    public static int n;
    public static int[][] counts_before;
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
        int lo = 0, hi = pattern.length() - 1;
        while (top <= bottom)
        {
            if (hi >= lo)
            {
                char symbol = pattern.charAt(hi);
                int index = charToNumber(symbol);
                hi--;
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
}
public class SuffixArrayMatching {
    public static int L;
    class fastscanner {
        StringTokenizer tok = new StringTokenizer("");
        BufferedReader in;

        fastscanner() {
            in = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() throws IOException {
            while (!tok.hasMoreElements())
                tok = new StringTokenizer(in.readLine());
            return tok.nextToken();
        }

        int nextint() throws IOException {
            return Integer.parseInt(next());
        }
    }


    public int[] computeSuffixArray(String text) {
        L = text.length();
        int[] order = SortCharacters(text);
        int[] clas = ComputeCharClasses(text, order);  
        // write your code here
        int bits = 1;
        while (bits < L) {
            order = sortDoubled(order, clas, bits);
            clas = updateClasses(order, clas, bits);
            bits = 2 * bits;
        }
        return order;

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
    public int[] SortCharacters(String text) {
        int[] order = new int[L];
        int[] count = new int[5];
        for (int i = 0; i < L; i++)
            count[charToNumber(text.charAt(i))]++;
        for (int j = 1; j < 5; j++)
            count[j] += count[j - 1];
        for (int i = L - 1; i >= 0; i--)
        {
            int index = charToNumber(text.charAt(i));
            count[index]--;
            order[count[index]] = i;
        }
        return order;
    }

    public int[] ComputeCharClasses(String text, int[] order) {
        int[] clas = new int[L];
        clas[order[0]] = 0;
        for (int i = 1; i < L; i++) 
            if (text.charAt(order[i]) != text.charAt(order[i - 1]))
                clas[order[i]] = clas[order[i - 1]] + 1;
            else
                clas[order[i]] = clas[order[i - 1]];
        return clas;
    }
    
    public int[] sortDoubled(int[] order, int[] clas, int bits) {
        int[] count = new int[L];
        int[] newOrder = new int[L];
        for (int i = 0; i < L; i++) 
            count[clas[i]]++;
        for (int j = 1; j < L; j++)
            count[j] += count[j - 1];
        for (int i = L - 1; i >= 0; i--){
            int start = (order[i] - bits + L ) % L;
            int cl = clas[start];
            count[cl]--;
            newOrder[count[cl]] = start;
        }
        return newOrder;
    }

    public int[] updateClasses(int[] order, int[] clas, int bits) {
        int[] newClass = new int[L];
        newClass[order[0]] = 0;
        for (int i = 1; i < L; i++) {
            int cur = order[i];
            int prev = order[i - 1];
            int mid = (cur + bits) % L, midPrev = (prev + bits) % L;
            if (clas[cur] != clas[prev] || clas[mid] != clas[midPrev])
                newClass[cur] = newClass[prev] + 1;
            else
                newClass[cur] = newClass[prev];
        }
        return newClass;
    }

    public void findOccurrences(String pattern, String text, int[] suffixArray, boolean[] occurs) {
//        List<Integer> result = new ArrayList<Integer>();          // find the first and last suffixArray whose prefix is pattern.
        int firstContains = Integer.MAX_VALUE, lastContains = Integer.MIN_VALUE;
        int lo = 0, hi = L - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int compare = toCompare(pattern, text, suffixArray[mid]);
            if (compare > 0)
                lo = mid + 1;
            else if (compare == 0) {
                firstContains = mid;
                hi = mid - 1;
            }
             
            else
                hi = mid - 1;
        }
//        for (int i = hi; i >= lo; i--)
//            if (toCompare(pattern, text, suffixArray[i]) == 0)
//                firstContains = i;
        if (firstContains == Integer.MAX_VALUE)
            return;        
        lo = 0;
        hi = L - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int compare = toCompare(pattern, text, suffixArray[mid]);
            if (compare > 0)
                lo = mid + 1;
            else if (compare == 0) {
                lastContains = mid;
                lo = mid + 1;
            }
            else
                hi = mid - 1;
        }
//        for (int i = lo; i <= hi; i++)
//            if (toCompare(pattern, text, suffixArray[i]) == 0)
//                lastContains = i;
        if (lastContains == Integer.MIN_VALUE)
            return;
        for (int i = firstContains; i <= lastContains; i++)
            occurs[suffixArray[i]] = true;


    }

    public int toCompare(String pattern, String text, int position) {
//        L = text.length();
        if (position + pattern.length() > L)
            return 1;
        for (int i = 0; i < pattern.length(); i++)
        {
            if (pattern.charAt(i) > text.charAt(i + position))
                return 1;
            else if (pattern.charAt(i) < text.charAt(i + position))
                return -1;
        }
        return 0;
    }
    
    static public void main(String[] args) throws IOException {
        new SuffixArrayMatching().run();
    }

    public void print(boolean[] x) {
        for (int i = 0; i < x.length; ++i) {
            if (x[i]) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }

    public void run() throws IOException {
        fastscanner scanner = new fastscanner();
        String text = scanner.next() + "$";
        int[] suffixArray = computeSuffixArray(text);
        int patternCount = scanner.nextint();
        boolean[] occurs = new boolean[text.length()];
        for (int patternIndex = 0; patternIndex < patternCount; ++patternIndex) {
            String pattern = scanner.next();
            findOccurrences(pattern, text, suffixArray, occurs);
        }
        print(occurs);
    }
}
