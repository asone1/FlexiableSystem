package repository;

import repository.ClassRepository;
import repository.FieldRepository;
import repository.ObjectfieldRepository;
import repository.StringfieldRepository;

public class RepositoryFactory {
    static ClassRepository classRepository;
    static FieldRepository fieldRepository;
    static StringfieldRepository stringfieldRepository;
    static ObjectfieldRepository objectfieldRepository;

    static {
        classRepository = new ClassRepository();
        fieldRepository = new FieldRepository();
        stringfieldRepository = new StringfieldRepository();
        objectfieldRepository = new ObjectfieldRepository();
    }

    public static ObjectfieldRepository getObjectfieldRepository() {
        return objectfieldRepository;
    }

    public static StringfieldRepository getStringfieldRepository() {
        return stringfieldRepository;
    }

    public static ClassRepository getClassRepository() {
        return classRepository;
    }

    public static FieldRepository getFieldRepository() {
        return fieldRepository;
    }
}
