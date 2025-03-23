package org.apiimplementation.apiimplementations.security.pattern;


public class IdPattern extends SanitizeString {

    private static final String ID_PATTERN  = "^[a-fA-F0-9]{24}$";

    public IdPattern(String id){
        super(id,ID_PATTERN);
    }



}
