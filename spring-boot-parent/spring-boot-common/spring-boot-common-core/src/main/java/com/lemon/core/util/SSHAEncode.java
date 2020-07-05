package com.lemon.core.util;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SSHAEncode {
    /**
     * Openldap 产生SSHA密码的算法
     * 效果等同于 slappasswd -h {ssha} -s password
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String generateSSHAPwd(String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final int SALT_LENGTH = 4;
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password.getBytes("utf-8"));
        crypt.update(salt);
        byte[] hash = crypt.digest();

        byte[] hashPlusSalt = new byte[hash.length + salt.length];
        System.arraycopy(hash, 0, hashPlusSalt, 0, hash.length);
        System.arraycopy(salt, 0, hashPlusSalt, hash.length, salt.length);

        return new StringBuilder().append("{SSHA}")
                .append(new String(Base64.encodeBase64(hashPlusSalt), Charset.forName("utf-8")))
                .toString();
    }

}
