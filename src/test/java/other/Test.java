package other;

/**
 * @author cyt
 * @date 2023/11/28
 */

class A {
    int num;

    public void setA(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}

class B {
    A a;

    public B(A a) {
        this.a = a;
    }

    public A getA() {
        return a;
    }
}

public class Test {
    public static void main(String[] args) {
        A a = new A();
        a.setA(1);
        B b1 = new B(a);
        B b2 = new B(a);
        B b3 = new B(a);
        b1.getA().setA(22);
        System.out.println(b1.getA().getNum());
        System.out.println(b2.getA().getNum());
        System.out.println(b3.getA().getNum());
    }
}
