
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class MethodCall implements Serializable {
    protected String method_name=null;
    protected Vector args=new Vector();


    public MethodCall() {
	
    }
    
    public MethodCall(String name) {
	method_name=name;
    }

    public MethodCall(String name, Object arg1) {
	this(name);
	AddArg(arg1);
    }

    public MethodCall(String name, Object arg1, Object arg2) {
	this(name, arg1);
	AddArg(arg2);
    }

    public MethodCall(String name, Object arg1, Object arg2, Object arg3) {
	this(name, arg1, arg2);
	AddArg(arg3);
    }

    public MethodCall(String name, Vector args) {
	method_name=name;
	if(args != null)
	    for(int i=0; i < args.size(); i++)
		AddArg(args.elementAt(i));
    }


    public String GetName()          {return method_name;}
    public void   SetName(String n)  {method_name=n;}
    public Vector GetArgs()          {return args;}



    public void AddArg(Object arg) {
	args.addElement(arg);
    }

    public void AddArg(byte b) {
	Byte obj=new Byte(b);
	args.addElement(obj);
    }

    public void AddArg(char c) {
	Character obj=new Character(c);
	args.addElement(obj);
    }

    public void AddArg(boolean b) {
	Boolean obj=new Boolean(b);
	args.addElement(obj);
    }

    public void AddArg(int i) {
	Integer obj=new Integer(i);
	args.addElement(obj);
    }

    public void AddArg(long l) {
	Long obj=new Long(l);
	args.addElement(obj);
    }



    int ComputeDistance(Class from, Class to) {
	int    retval=0;
	Class  current=from;
	
	while(current != null) {
	    current=current.getSuperclass();
	    if(current == null)
		break;
	    else if(current.equals(to))
		return ++retval;	   
	    else
		retval++;		
	}
	return 0;
    }


    /* Assume that args is non-null and that method and args are same size.
       Matches arguments with formal parameters according to 'proximity' of argument
       to parameter, e.g. an exact match yields 0, a direct subtype yields 1, and an
       indirect subtype <n> where <n> is the distance (e.g. 3). No match returns -1.
       The best match is the one with the lowest return value */

    int MatchParameters(Method method, Vector args) {
	Class[]  formal_parms=method.getParameterTypes();
	Class    formal_parm;
	Object   arg;
	int      retval=0;
	
	for(int i=0; i < formal_parms.length; i++) {
	    formal_parm=formal_parms[i];
	    arg=args.elementAt(i);

	    System.out.print("Comparing arg " + arg.getClass().getName() + 
			     " to formal parameter " + formal_parm.getName() + ": " );

	    if(formal_parm.equals(arg.getClass())) {      // exact match argument with formal parm
		retval+=0;
		System.out.println("exact match");
		continue;
	    }
	    if(formal_parm.isInstance(arg)) {             // is arg a subtype of formal_class ?
		int t=ComputeDistance(arg.getClass(), formal_parm);
		System.out.println("rank is " + t);
		return t;
	    }
	    else {                                        // no match
		System.out.println("no match");
		return -1;
	    }
	}
	return retval;
    }

    
    Method FindMethod(Class target_class, String method_name, Vector args) throws Exception {
	Method     retval=null, method;
	Method[]   methods=target_class.getMethods();
	Vector     matching_methods=new Vector();  // contains all possible matches
	int        num_args=args.size();
	Class      parm_cl;
	Class[]    parm_types;
	int        rank=1000, new_rank=0;


	/* 1. Match by name and number of parameters */

	for(int i=0; i < methods.length; i++) {
	    method=methods[i];
	    if(method.getName().equals(method_name) && 
	       method.getParameterTypes().length == num_args) {
		matching_methods.addElement(method);
		continue;
	    }
	}

	if(matching_methods.size() == 1)
	    return (Method)matching_methods.elementAt(0);
	else if(matching_methods.size() < 1)
	    throw new NoSuchMethodException();
	    	

	/* 2. If this is not enough (more than 1 method matching), match formal parameters 
	   with actual arguments. Discard methods whose arguments cannot be cast to the 
	   formal parameters */

	System.out.println(matching_methods.size() + " methods are left, comparing types:");
	
	for(int i=0; i < matching_methods.size(); i++) {
	    method=(Method)matching_methods.elementAt(i);
	    new_rank=MatchParameters(method, args);
	    System.out.println("Method " + method + " has rank " + new_rank + "\n");
	    if(new_rank < 0)
		continue;	
	    if(new_rank <= rank) {  // Discards duplicate methods ! But we don't care ...
		retval=method;
		rank=new_rank;
	    }
	}

	if(retval != null)
	    return retval;
	else 
	    throw new NoSuchMethodException();
    }




    
//     Method FindMethod(Class target_class, String method_name, Vector args) throws Exception {
// 	int       len=args != null ? args.size() : 0;
// 	Class[]   formal_parms=new Class[len];
// 	Method    retval;

// 	for(int i=0; i < len; i++)
// 	    formal_parms[i]=args.elementAt(i).getClass();


// 	/* getDeclaredMethod() is a bit faster, but only searches for methods in the current
// 	   class, not in superclasses */
// 	retval=target_class.getMethod(method_name, formal_parms);

// 	return retval;
//     }




    public Object Invoke(Object target) {
	Class   cl=target.getClass();
	Method  meth;
	Object  retval=null;
	Class   class_args[];
	Object  obj_args[];
	
	if(method_name == null) {
	    System.out.println("Method name is not provided");
	    return null;
	}
	try {
	    meth=FindMethod(cl, method_name, args);
	    obj_args=new Object[args.size()];
	    for(int i=0; i < args.size(); i++)
		obj_args[i]=args.elementAt(i);
	    retval=meth.invoke(target, obj_args);
	    return retval;

	}
	catch(InvocationTargetException inv_ex) {
	    retval=inv_ex.getTargetException();
	}
	catch(NoSuchMethodException no) {
	    System.out.print("Found no method called " + method_name + " in class " +
			     cl.getName() + " with [");
	    for(int i=0; i < args.size(); i++) {
		if(i > 0)
		    System.out.print(", ");
		System.out.print(args.elementAt(i).getClass());
	    }
		System.out.println("] formal parameters");
	    
	    return null;
	}
	catch(Exception e) {
	    System.err.println(e);
	}

	return retval;
    }



    public String toString() {
	StringBuffer ret=new StringBuffer();
	ret.append("MethodCall (name=" + method_name);
	ret.append(", number of args=" + args.size() + ")");
	ret.append("\nArgs:");
	for(int i=0; i < args.size(); i++) {
	    ret.append("\n[" + args.elementAt(i) + " (" + 
		       args.elementAt(i).getClass().getName() + ")]");
	}
	return ret.toString();
    }



//     public void writeExternal(ObjectOutput out) throws IOException {
// 	System.out.println("writeExternal");
// 	out.writeObject(method_name);
// 	out.writeObject(formal_args);
// 	out.writeObject(args);
//     }


//     public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
// 	method_name=(String)in.readObject();
// 	formal_args=(Vector)in.readObject();
// 	args=(Vector)in.readObject();

//     }


    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
	out.writeObject(method_name);
	out.writeObject(args);
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, 
    ClassNotFoundException {
	method_name=(String)in.readObject();
	args=(Vector)in.readObject();
    }
 


}



