import java.util.*;
import java.io.*;
import java.util.zip.CheckedInputStream;

public class SuffixTree {
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
        List<String> result = new ArrayList<String>();
        List<Map<Character, Integer>> trie = new ArrayList<Map<Character, Integer>>();
        Map<Character, Integer> firstNode = new HashMap<Character, Integer>();  // this is the first node added to trie;
        trie.add(firstNode);
        int N = text.length();
        char[] a = new char[N];
        for (int i = 0; i < N; i++)
            a[i] = text.charAt(i);
        for (int i = 0; i < text.length(); i++)
        {
            int cur = 0;
            loop : for (int j = i; j < text.length(); j++)
            {
                Map<Character, Integer> node = trie.get(cur);
                char currentSymbol = a[j];          // the character at position i;
                for (Map.Entry<Character, Integer> entry : node.entrySet()) 
                    if (currentSymbol == entry.getKey())
                    {
                        cur = entry.getValue();
                        continue loop;
                    }
                node.put(currentSymbol, trie.size());
                cur = trie.size();
                trie.add(new HashMap<Character, Integer>());
            }
        }

        StackOfInts stack = new StackOfInts(text.length() * (text.length() + 1) / 2);
        stack.push(0);
        while (!stack.isEmpty()) {
            int cur = stack.pop();
            Map<Character, Integer> node = trie.get(cur);
            for (Map.Entry<Character, Integer> entry : node.entrySet()) {
                char[] string = new char[text.length()];
                int string_size = 0;
                int counter = 0;
                string[counter++] = entry.getKey();
                string_size++;
                int iterator = entry.getValue();
                while (trie.get(iterator).size() == 1)
                {
                    Map<Character, Integer> node1 = trie.get(iterator);
                    for (Map.Entry<Character, Integer> e1 : node1.entrySet()) {
                        string[counter++] += e1.getKey();
                        string_size++;
                        iterator = e1.getValue();
                    }
                }
                result.add(new String(string, 0, string_size));
                stack.push(iterator);
            }
        }
//        System.out.println(trie.size());
        return result;
    }
    private static class StackOfInts
    {
        private int[] a;
        private int N;
        public StackOfInts(int cap) 
        {
            a = new int[cap];
            N = 0;
        }
        public boolean isEmpty() { return N == 0;}
        public int size() { return N; }
        public void push(int item) { a[N++] = item; }
        public int pop() { return a[--N]; }
        public int top() { 
                return a[N - 1];      //No consideration of null exception(have ruled it out in "reach" function;
        }
    }    

    static public void main(String[] args) throws IOException {
        new SuffixTree().run();
//        char[] a = new char[6];
//        a[0] = 'c';
//        a[1] = 'b';
//        String s = new String(a);
//        System.out.println(" s= " + s + " length ; " + s.length());
    }

    public void print(List<String> x) {
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
