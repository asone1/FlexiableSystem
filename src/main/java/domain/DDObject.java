package domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class DDObject implements Cloneable {
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        Method[] methods = this.getClass().getMethods();
        StringBuilder result = new StringBuilder();

        for (Method method : methods) {
            if (method.getName().contains("get")
                    && !method.getName().equalsIgnoreCase("getClass")) {

                    try {
                        Object string = method.invoke(this);
                        if (string != null) result.append(method.getName()+": ").append(string).append("\n");
                    } catch (IllegalAccessException e) {
//                   e.printStackTrace();
                        return super.toString();
                    } catch (InvocationTargetException e) {
//                  e.printStackTrace();
                        return super.toString();
                    }

                }
            }
            return result.toString();
        }
    }
