import java.util.*;
import java.io.*;
import java.util.zip.CheckedInputStream;

public class SuffixTree {
    public static int[] end;      // end index of strings;
    public static int[] index;    // index ---> different strings(according to start index);
    public static int[] aux;
    public static char[] textArray;
    public static int N, numOfStrings; 
    private static final int CUTOFF =  5;   // cutoff to insertion sort (any value between 0 and 12)
    class FastScanner {
        StringTokenizer tok = new StringTokenizer("");
        BufferedReader in;

        FastScanner() {
            in = new BufferedReader(new InputStreamReader(System.in));
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

    // Build a suffix tree of the string text and return a list
    // with all of the labels of its edges (the corresponding 
    // substrings of the text) in any order.
    public List<String> computeSuffixTreeEdges(String text) {
//        System.out.println("total Length: " + text.length());
        List<String> result = new ArrayList<String>();
        N = text.length();
        numOfStrings = N;
//        index = new int[N];
        end = new int[N];
        textArray = text.toCharArray();
//        System.out.println("size" + textArray.length);
        for (int i = 0; i < N; i++)
            end[i] = N - 1;
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < N; i++)
            indices.add(i);
        while (!indices.isEmpty()) {
            if (indices.size() == 1)
            {
//                System.out.println("only one!!");
                for (int i : indices) 
                {
//                    System.out.println(index[i] + " / " +  end[index[i]]);
                    result.add(text.substring(i, end[i] + 1));
                }
                break;
            }
            int cur = 0;  
            index = new int[numOfStrings];
            aux = new int[numOfStrings];
            for (int i : indices)
                index[cur++] = i;
            indices = new ArrayList<Integer>();
//            System.out.println("numofstrings : " + numOfStrings);
//            sort(0, numOfStrings - 1, 0);
            sort(0, numOfStrings - 1);
            int[] d = new int[numOfStrings];
//            for (int i = 1; i < numOfStrings; i++)
//                System.out.println("lcp: " + lcp(i));            //print out lcp(longest common pattern);
            d[0] = index[0] + lcp(1);
            d[numOfStrings - 1] = index[numOfStrings - 1] + lcp(numOfStrings - 1);
            for (int i = 1; i < numOfStrings - 1; i++)
                d[i] = index[i] + Math.max(lcp(i), lcp(i + 1));
//            for (int i = 0; i < numOfStrings; i++)
//            {
//                System.out.println("i = " + i + " : " + index[i] + " / " + d[i] + " / " + end[index[i]] + "  String : " + text.substring(index[i], end[index[i]] + 1));
//            }
            for (int i = 0; i < numOfStrings; i++)
                if (d[i] == index[i])
                {
//                    System.out.println("haha " + text.substring(index[i], end[index[i]] + 1));
                    result.add(text.substring(index[i], end[index[i]] + 1));
                    end[index[i]] = index[i] - 1;
                } else if (d[i] == end[index[i]] + 1) {
//                    indices.add(index[i]);
                } else {
//                    indices.add(index[i]);
                    result.add(text.substring(d[i], end[index[i]] + 1));
                    end[index[i]] = d[i] - 1;
                }
            if (end[index[0]] >= index[0])
            {
                indices.add(index[0]);
//                System.out.println("index[0] = " + index[0]);
            }
            for (int i = 1; i < numOfStrings; i++)
                if (end[index[i]] >= index[i] && (!isEqual(index[i], index[i - 1])))
                {
//                    System.out.println("hh");
//                    System.out.println("index" + index[i]);
                    indices.add(index[i]);
                }
//            for (int i = 0; i < numOfStrings; i++)
//                System.out.println("i = " + i + " : " + index[i] + " / " + d[i] + " / " + end[index[i]] + "  String : " + text.substring(index[i], end[index[i]] + 1));
//            for (int i : indices)
//                System.out.println("final: " + i);
            numOfStrings = indices.size();
//            for (String s : result)
//                System.out.println(s);
//            System.out.println("//");
//            for (int i = 1; i < numOfStrings; i++)
//                System.out.println("lcp: " + lcp(i));
        }
        return result;
    }
    
    public static boolean isEqual(int i , int j) {
        if (end[i] - i != end[j] - j)
            return false;
        for (int k = i, k1 = j; k <= end[i]; k++, k1++)
            if (textArray[k] != textArray[k1])
                return false;
        return true;
    }
    /**
     * Returns the length of the longest common prefix of the <em>i</em>th
     * smallest suffix and the <em>i</em>-1st smallest suffix.
     * @param i an integer between 1 and <em>n</em>-1
     * @return the length of the longest common prefix of the <em>i</em>th
     * smallest suffix and the <em>i</em>-1st smallest suffix.
     * @throws java.lang.IndexOutOfBoundsException unless 1 &le; <em>i</em> &lt; <em>n</em>
     */
    public int lcp(int i) {
        if (i < 1 || i >= numOfStrings) throw new IndexOutOfBoundsException();
        return lcp(index[i], index[i-1]);
    }

    // longest common prefix of text[i..end[i]] and text[j..end[j]];
    private int lcp(int i, int j) {
        int length = 0;
        int endi = end[i];
        int endj = end[j];
        while (i <= endi && j <= endj) {
//            System.out.println("compare: " + textArray[i] + " and " + textArray[j]);
            if (textArray[i] != textArray[j]) return length;
            i++;
            j++;
            length++;
        }
        return length;
    }
    // 3-way string quicksort lo..hi starting at dth character
    private void sort(int lo, int hi) { 

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(lo, hi);
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(lo, mid);
        sort(mid + 1, hi);
        if (!less(index[mid + 1], index[mid]))
        {
//            System.out.println("lo = " + lo + " mid = " + mid + " hi = " + hi);
            return;
        }
        merge(lo, mid, hi);
    }
    
    private void merge(int lo, int mid, int hi)
    {
//        System.out.println("before ");
//        for (int i = lo; i <= hi; i++)
//            System.out.println("index : " + index[i]);
        for (int i = lo; i <= hi; i++)
            aux[i] = index[i];
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++)
        {
            if (i > mid)
                index[k] = aux[j++];
            else if (j > hi)
                index[k] = aux[i++];
            else if (less(aux[j], aux[i]))
                index[k] = aux[j++];
            else
                index[k] = aux[i++];
        }
//        System.out.println("after ");
//        for (i = lo; i <= hi; i++)
//            System.out.println("index : " + index[i]);
    }
    
    
    // sort from a[lo] to a[hi], starting at the dth character
    private void insertion(int lo, int hi) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(index[j], index[j-1]); j--)
            {
//                System.out.println(" exchange " + j  + " which is " + index[j] + " and " + (j - 1) + " which is " + index[j - 1]);
                exch(j, j-1);
            }
    }

    // is text[i+d..n) < text[j+d..n) ?
    private boolean less(int i, int j) {
        if (i == j) return false;
        int endi = end[i];
        int endj = end[j];
        while (i <= endi && j <= endj) {
            if (textArray[i] < textArray[j]) {
//                System.out.println("haha    " + textArray[i] + " / " + textArray[j]);
                return true;
            }
            if (textArray[i] > textArray[j]) return false;
            i++;
            j++;
        }
        return i > j;
    }
    

    // exchange index[i] and index[j]
    private void exch(int i, int j) {
        int swap = index[i];
        index[i] = index[j];
        index[j] = swap;
    }

    static public void main(String[] args) throws IOException {
        new SuffixTree().run();
    }

    public void print(List<String> x) {
//        System.out.println("size : " + x.size());
        for (String a : x) {
            System.out.println(a);
        }
    }

    public void run() throws IOException {
        FastScanner scanner = new FastScanner();
        String text = scanner.next();
        List<String> edges = computeSuffixTreeEdges(text);
        print(edges);
    }
}
