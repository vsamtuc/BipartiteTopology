package BipartiteTopologyAPI.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotates a "default method", i.e. a method invoked when an RPC does not
 * match any method. 
 * 
 * This should probably be used for error handling, but it may not be the best way...
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultOp {
}
