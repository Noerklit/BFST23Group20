package Model;

public class OSMFileException extends RuntimeException {

    public OSMFileException(String unexpectedTag, int lineNumber, String method) {
        super(String.format("Unexpected tag \"%s\" at line %s found in %s.", unexpectedTag, lineNumber, method));
    }

    public OSMFileException(String unexpectedTag, int lineNumber, String method, String expectedTag) {
        super(String.format("Unexpected tag \"%s\" at line %s found in %s. Expected tag at the end of that method: \"%s.\"", unexpectedTag, lineNumber, method, expectedTag));
    }
}
