package BipartiteTopologyAPI;

import com.fasterxml.uuid.Generators;
import BipartiteTopologyAPI.annotations.*;
import BipartiteTopologyAPI.futures.Response;
import org.apache.commons.lang3.ClassUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * A Serializable class representing the extracted description of an object.
 */
public class NodeClass implements Serializable {

    private Class<?> wrappedClass; // The class of the wrapped object.
    private Class<?> proxiedInterface; // The remote proxy interface of the object.
    private HashMap<String, Method> operationTable; // Map String operation id -> method descriptor object.
    private Method initMethod; // The method used to initialize a node.
    private Method defaultMethod; // The method invoked by a RPC that doesn't specify which method to execute.
    private Method processMethod; // The method used to process data.
    private Method mergeMethod; // The method used to merge two wrappedClasses.
    private Method queryMethod; // The method used to answer a query.
    private Class<?> proxyClass; // the proxy class for this node.

    public NodeClass(Class wrappedClass) {
        this.wrappedClass = wrappedClass;
        extractProxyInterface();
        checkRemoteMethods();
        initMethod = checkAuxiliaryMethod(InitOp.class);
        processMethod = checkAuxiliaryMethod(ProcessOp.class);
        mergeMethod = checkAuxiliaryMethod(MergeOp.class);
        queryMethod = checkAuxiliaryMethod(QueryOp.class);
        createProxyClass();
    }

    /*
        Set `proxyInterface` to a unique proxy interface that is implemented by the
        class.

        A proxy interface is an interface such that either:
        (a) its definition is decorated with @RemoteProxy, or
        (b) its use request the class is decorated with @Remote
     */
    protected void extractProxyInterface() {
        // get remote interface
        AnnotatedType[] ifaces = wrappedClass.getAnnotatedInterfaces();
        Class<?> pxy_ifc = null;

        for (AnnotatedType i : ifaces) {
            assert i.getType() instanceof Class<?>;
            Class<?> icls = (Class) i.getType();

            if (!i.isAnnotationPresent(Remote.class) && !icls.isAnnotationPresent(RemoteProxy.class))
                continue;
            check(pxy_ifc == null, "Multiple remote interfaces on wrapped class %s", wrappedClass);
            pxy_ifc = (Class) i.getType();
        }
        check(pxy_ifc != null, "No remote interfaces on wrapped class %s", wrappedClass);

        // success
        proxiedInterface = pxy_ifc;
    }


    static public ArrayList<Object> getInterfaces(Class c) {
        ArrayList<Object> interfaces = new ArrayList<Object>(ClassUtils.getAllInterfaces(c));

        try {
            while (!c.getSuperclass().equals(Object.class)) {
                c = c.getSuperclass();
                interfaces.addAll(Arrays.asList(c.getInterfaces()));
            }
        } catch (NullPointerException ignored) {
        }

        return interfaces;
    }


    static public boolean isSerializable(Class c) {
        return getInterfaces(c).contains(Serializable.class);
    }


    /*
        Check a remote method of proxyInterface:
        * is annotated with @RemoteOp
        * every non-@Response parameter must be Serializable
        * if the first parameter is annotated with @Response then it must be
          of type java.util.function.Consumer
        * return type must be void
     */
    public void checkRemoteMethod(Method m) {

        check(m.getDeclaredAnnotation(RemoteOp.class) != null,
                "Method %s is not annotated with @RemoteOp", m);

        Parameter[] params = m.getParameters();

        for (Parameter param : params) {
            Class pcls = param.getType();

            check(isSerializable(pcls),
                    "Parameter type %s is not Serializable request method %s of remote proxy %s",
                    pcls, m, proxiedInterface);
        }

        check(m.getReturnType() == void.class || m.getReturnType() == Response.class,
                "Return type is not void request method %s of remote proxy %s",
                m, proxiedInterface);
    }

    /*
        Check the methods of remoteInterface.
        * Each method is given to checkRemoteMethod
        * All @RemoteOp operation ids are unique
     */
    public void checkRemoteMethods() {
        assert proxiedInterface != null;

        HashMap<String, Method> op2method = new HashMap<>();

        for (Method m : proxiedInterface.getMethods()) {

            // Check that the method is well-formed.
            checkRemoteMethod(m);

            // Generate a Universal Unique Identifier for the method/operation.
            String method_identifier = Generators
                    .nameBasedGenerator()
                    .generate(m.getName() + Arrays.toString(m.getParameterTypes()))
                    .toString();

            // Make method object accessible.
            try {
                m.setAccessible(true);
            } catch (SecurityException e) {
                throw new RuntimeException(
                        String.format("Interface %s is not accessible (probably not public)", proxiedInterface),
                        e);
            }

            // add to operation table
            op2method.put(method_identifier, m);
        }
        operationTable = op2method;
    }

    public Method checkAuxiliaryMethod(Class<? extends Annotation> C) {
        Class cls = wrappedClass;
        ArrayList<Method> methods = new ArrayList<>(Arrays.asList(cls.getMethods()));
        Method process_method = null;
        for (Method meth : methods) {
            if (meth.isAnnotationPresent(C)) {
                check(process_method == null,
                        "Multiple Auxiliary methods %s declared in wrapped class %s", C.toString(), wrappedClass);
                process_method = meth;
            }
        }
        check(process_method != null, "No %s method in wrapped class %s", C.toString(), wrappedClass);
        return process_method;
    }

    /*
        Create a dynamic proxy class for the proxied interface
     */
    public void createProxyClass() {
        assert proxiedInterface != null;

        proxyClass = Proxy.getProxyClass(getClass().getClassLoader(),
                proxiedInterface);
    }

    static public void check(boolean cond, String format, Object... args) {
        if (!cond)
            throw new RuntimeException(String.format(format, args));
    }

    static protected HashMap<Class<?>, NodeClass> instances = new HashMap<>();

    /*
        Caching instances
     */
    synchronized static public NodeClass forClass(Class<?> _wclass) {
        if (instances.containsKey(_wclass))
            return instances.get(_wclass);
        else {
            NodeClass nc = new NodeClass(_wclass);
            instances.put(_wclass, nc);
            return nc;
        }
    }

    public Class<?> getWrappedClass() {
        return wrappedClass;
    }

    public Class<?> getProxiedInterface() {
        return proxiedInterface;
    }

    public Map<String, Method> getOperationTable() {
        return operationTable;
    }

    public Method getInitMethod() {
        return initMethod;
    }

    public void setDefaultMethod() {
        defaultMethod = checkAuxiliaryMethod(DefaultOp.class);
    }

    public Method getDefaultMethod() {
        return defaultMethod;
    }

    public Method getProccessMethod() {
        return processMethod;
    }

    public Method getMergeMethod() {
        return mergeMethod;
    }

    public Method getQueryMethod() {
        return queryMethod;
    }

    public Class<?> getProxyClass() {
        return proxyClass;
    }

    public static HashMap<Class<?>, NodeClass> getInstances() {
        return instances;
    }

}
