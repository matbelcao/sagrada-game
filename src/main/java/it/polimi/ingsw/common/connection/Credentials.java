package it.polimi.ingsw.common.connection;
import java.util.Base64;

/**
 * Utility class used for hashing password and/or converting it into strings
 */
public class Credentials {
    private Credentials(){}
    private static final int HASH_SIZE=32;

    public static char[] hash(String username, char[] passwd){
        char[] credentials=concat(username.trim().toCharArray(),passwd);
        char[] hash= new char[HASH_SIZE];
        for(int i=0;i<credentials.length;i++){
            for(int  j=0, k=0;k<7*(HASH_SIZE-1);k++,j= (j + Math.abs(credentials[j%credentials.length] ^ hash[j]) + HASH_SIZE)%HASH_SIZE) {
                hash[j]= (char)Math.abs(j%2==0?hash[j]-credentials[i]:(hash[j]+credentials[i])%0xffff);
            }
        }

        return Base64.getEncoder().encodeToString(new String (hash).getBytes()).toCharArray();
    }

    public static String toString(char[] charArray){
        StringBuilder string = new StringBuilder();
        for(char c: charArray){
            string.append(c);
        }
        return string.toString();
    }


    private static char[] concat(char[]a,char[] b){
        char[] concat = new char[a.length+b.length];
        for(int i=0; i<a.length+b.length;i++){
            if(i<a.length) {
                concat[i] = a[i];
            }else{
                concat[i] = b[i-a.length];
            }
        }
        return concat;
    }
}
