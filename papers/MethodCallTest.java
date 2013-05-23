

class A {}
class B extends A {}
class C extends B {}
class D extends C {}



public class MethodCallTest {
    

    public void foo(A a1, A a2) {
	System.out.println("A,A");
    }


    public void foo(B b, A a) {
	System.out.println("B,A");
    }

    public void foo(A a, B b) {
	System.out.println("A,B");
    }


    public void foo(B b1, B b2) {
	System.out.println("B,B");
    }


    public void foo(A a, C c) {
	System.out.println("A,C");
    }

    public void foo(D d, C c) {
	System.out.println("D,C");
    }


    public void foo(C c1, C c2) {
	System.out.println("C,C");
    }

    


    public static void main(String args[]) {
	MethodCall      m;
	MethodCallTest  test=new MethodCallTest();

	try {
	    m=new MethodCall("foo", new A(), new D());
	    m.Invoke(test);
	}
	catch(Exception e) {
	    System.err.println(e);
	}
    }
}
