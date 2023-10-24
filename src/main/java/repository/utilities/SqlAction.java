package repository.utilities;

public enum SqlAction {

    CREATE("CREATE"), UPDATE("UPDATE"), SELECT("SELECT"), INSERT("INSERT");
    String value;

    SqlAction(String s) {
        value = s;
    }

    public boolean equals(SqlAction action){
        return action.value.equalsIgnoreCase(this.value);
    }
}

