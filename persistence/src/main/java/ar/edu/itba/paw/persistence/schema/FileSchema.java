package ar.edu.itba.paw.persistence.schema;

public final class FileSchema {

    private FileSchema() {}

    public static final String TABLE_NAME = "files";
    public static final String ID = "file_id";
    public static final String FILENAME = "filename";
    public static final String ALT = "alt";
    public static final String CONTENT_TYPE = "content_type";
    public static final String DATA = "data";
}