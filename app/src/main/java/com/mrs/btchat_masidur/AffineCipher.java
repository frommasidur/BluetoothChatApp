package com.mrs.btchat_masidur;

public class AffineCipher {
    public static char[] encryption(char[] s,int a,int b){
        char[] str=s;
        int i = 0;
        for(char c:s){
            int ic = c;     //char to int
            int ci = ((ic*a)+b ) % 256; //mod 256
            char cc =(char)ci;
            str[i++]= cc;
        }
        return str;
    }
    public static char[] decryption(char[] s,int a,int b){
        char[] str=s;
        int i = 0;
        for(char c:s){
            int ic = c;
            int ci = ((ic+b)*a) % 256; //mod 256
            char cc =(char)ci;
            str[i++]= cc;
        }
        return str;
    }

}
