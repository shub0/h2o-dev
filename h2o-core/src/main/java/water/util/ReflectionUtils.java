package water.util;

import water.H2O;
import water.Iced;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ReflectionUtils {
  /**
   * Reflection helper which returns the actual class for a type parmater, even if itself is parameterized.
   */
  public static Class<? extends Iced> findActualClassParameter(Class clz, int parm) {
    Class<? extends Iced> iced_class = null;

    if (clz.getGenericSuperclass() instanceof ParameterizedType) {
      Type[] handler_type_parms = ((ParameterizedType)(clz.getGenericSuperclass())).getActualTypeArguments();
      if (handler_type_parms[parm] instanceof Class) {
        // The handler's Iced class is not parameterized (the normal case):
        iced_class = (Class) handler_type_parms[parm];  // E.g., for a Schema [0] is the impl (Iced) type; [1] is the Schema type
      } else if (handler_type_parms[parm] instanceof TypeVariable) {
        // The handler's Iced class is parameterized, e.g. to handle multiple layers of Schema classes as in ModelsHandler:
        iced_class = (Class)((TypeVariable)(handler_type_parms[parm])).getBounds()[parm];
      } else {
        String msg = "Iced parameter for handler: " + clz + " uses a type parameterization scheme that we don't yet handle: " + handler_type_parms[parm];
        Log.warn(msg);
        throw H2O.fail(msg);
      }
    } else {
      // Superclass is not a ParameterizedType, so we just have Iced.
      iced_class = Iced.class; // If the handler isn't parameterized on the Iced class then this has to be Iced.
    }
    return iced_class;
  }
}
