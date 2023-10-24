import com.google.gson.Gson;
import domain.myobject.MyObjFormatter;
import repository.ClassRepository;
import repository.FieldRepository;
import domain.myobject.MyObject;
import repository.RepositoryFactory;
import repository.StringfieldRepository;
import repository.utilities.JDBCConnector;
import utilities.common.JsonFormatter;
import web.controller.RestServiceFactory;

import java.util.List;

public class Main {

//    static ClassRepository classRepository = new ClassRepository();
    static FieldRepository fieldRepository = RepositoryFactory.getFieldRepository();
    static StringfieldRepository stringfieldRepository  = RepositoryFactory.getStringfieldRepository();

    public static void main(String[] args) throws Exception {

        JDBCConnector.startConnect();
        try {
//            RestServiceFactory.start();
//            System.out.println("Server start!");

//            RepositoryFactory.getClassRepository().insertClass(new ClassInfo("quote"));
//            ClassInfo myClass = RepositoryFactory.getClassRepository().getClassByName("order");
//            System.out.println(myClass.getClassId());
//           FieldInfo newField = fieldRepository.createField("discount","products");
//System.out.println(fieldRepository.addFieldToClass("order","products"));
//            List<FieldInfo> fieldInfos = fieldRepository.getFieldsByClass("order");
//            Collections.sort(fieldInfos,new FieldComparator());
//            System.out.println(fieldInfos);
//            for(FieldInfo f:fieldInfos){
//                Log.in(f);
//            }
//            fieldRepository.toJson(fieldInfos);
//            FieldInfo newField = fieldRepository.getFieldByName("price");
//            FieldInfo newField = fieldRepository.getFieldByName("price");
//            Result result = fieldRepository.addFieldToClass("Order", "order date","customer","totals","image","order number","note","product","product position","price","test");
//            System.out.println(result);
//            System.out.println(newField);
//            StringfieldRepository stringfieldRepository = RepositoryFactory.getStringfieldRepository();
//            stringfieldRepository.getKeyFields("order");

//            System.out.println(
//                    JsonFormatter.toJson(MyObjFormatter.convert("order",stringfieldRepository.getDetails("23-0001"))));
            System.out.println(stringfieldRepository.getFieldsByClass("order"));


//            fieldRepository.getFullFieldsByClass("order");
//            List<StringField> list =  stringfieldRepository.retrieveField("order");
//            System.out.println(list);
//            for(StringField s: list){
//                System.out.println(s);

//            System.out.println(stringfieldRepository.addFieldToClass("order","order number",null,"23-0003"));
//            System.out.println(StringfieldController.toJson(stringfieldRepository.getStringFieldsByParent("order","23-0001")));
//          System.out.println(stringfieldRepository.addFieldToClass("order","order date","23-0001","3034"));
//           StringField o = stringfieldRepository.getParent("23-0001");
//            System.out.println(StringfieldController.toJson(o));
//            stringfieldRepository.addFieldToClass("Order","products","GW0012");
//            List<StringField> o = stringfieldRepository.retrieveField("Order");
//            System.out.println(StringfieldController.toJson(o));
//            List<FieldInfo> fieldInfo = fieldRepository.getFields("Order");
//            System.out.println(CustomJson.toJson(fieldInfo));;

//
//            fieldRepository.updateField("order","Order date","order date");

//            classRepository.updateClassByName("order","orders");
//            List objs = classRepository.getAllClass();
//            System.out.println(objs);


        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(e.toString());
        }

//        JDBCConnector.stopConnect();

    }
}