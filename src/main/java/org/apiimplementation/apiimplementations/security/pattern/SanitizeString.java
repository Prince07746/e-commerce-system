package org.apiimplementation.apiimplementations.security.pattern;

import org.apiimplementation.apiimplementations.error.customError.InvalidFormatException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SanitizeString {
    
    private final String value;
    
    public SanitizeString(String value,String pattern){
        if(!(isValid(value,pattern)) || value == null || value.isBlank()){
            throw new InvalidFormatException("Invalid format: "+value);
        }
        this.value = value;
    }
    
    
    public boolean isValid(String value,String pattern){
        Pattern pattern1 = Pattern.compile(pattern);
        Matcher matcher = pattern1.matcher(value);
        return matcher.matches();
    }
    
    public String getValue(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if ( !(o instanceof  SanitizeString)) return false;
        SanitizeString that = (SanitizeString) o;
        return Objects.equals(this.getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
