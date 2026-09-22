long fib(int n) {
    long a = 0, b = 1;
    for (int i = 0; i < n; i++) {
        long next = a + b;
        a = b;
        b = next;
    }
    return a;
}

void main() {
    IO.println(fib(10));
}
