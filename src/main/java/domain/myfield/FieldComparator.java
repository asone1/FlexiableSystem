package domain.myfield;

import domain.myfield.FieldInfo;

import java.util.Comparator;
import java.util.UUID;

//fails somehow
public class FieldComparator implements Comparator<FieldInfo> {


        @Override
        public int compare(FieldInfo var1, FieldInfo var2){
//            if(var1.getParent() != null && var2.getParent()!= null){
            UUID v1 = var1.getParent();
            UUID v2 = var2.getParent();
            String s1 = "";
            String s2 = "";
            if(var1.getParent()!= null){
                s1 = v1.toString();
            }
            if(var2.getParent()!= null){
                s2 = v2.toString();
            }
                return s1.compareTo(s2);
//            }
//            else if(var1.getParent()== null && var2.getParent()==null) {
//                return 0;
//            }
//            else{
//                return 999;
//            }
        }

}
