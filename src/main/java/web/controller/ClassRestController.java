package web.controller;

import com.sun.net.httpserver.HttpExchange;
import domain.myclass.ClassInfo;
import org.apache.commons.lang.StringUtils;
import repository.ClassRepository;
import repository.RepositoryFactory;
import utilities.common.JsonFormatter;

import static web.controller.RestServiceFactory.getParameterByIdx;

public class ClassRestController extends RestController {

    static ClassRepository classRepository;

    static {
        classRepository = RepositoryFactory.getClassRepository();
    }

    //api/class/{name}/{newName}/{keyField}
    public static Object updateClass(HttpExchange exchange) {
        String className = getParameterByIdx(exchange.getRequestURI(), first);
        String newClassName = getParameterByIdx(exchange.getRequestURI(), second);
        if (StringUtils.isEmpty(newClassName)) {
            return classRepository.insertClass(new ClassInfo(className));
        } else {
            String keyField = getParameterByIdx(exchange.getRequestURI(), third);
           return classRepository.updateClassByName(className, newClassName,keyField);
        }
    }

    //api/class/{name}
    public static String getClassObj(HttpExchange exchange) {
        String className = getParameterByIdx(exchange.getRequestURI(), first);

        if (StringUtils.isEmpty(className)) {
            return JsonFormatter.toJson(classRepository.getAllClass());
        }
        ClassInfo myClass = classRepository.getClassByName(className);
        if (myClass != null) {
            return JsonFormatter.toJson(myClass);
        } else {
            return "no such class";
        }
    }
}
