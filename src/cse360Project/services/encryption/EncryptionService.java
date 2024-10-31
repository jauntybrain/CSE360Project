package cse360Project.services.encryption;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/*******
 * <p> EncryptionService. </p>
 * 
 * <p> Description: A helper class for encrypting and decrypting data.  </p>
 * 
 * <p> Copyright: Robert Lynn Carter © 2024. Modified by CSE 360 Team Th02 </p>
 * 
 * @author Robert Lynn Carter.
 * 
 * @version 1.00 2024-10-30 Phase two
 * 
 */
public class EncryptionService {

	private static String BOUNCY_CASTLE_PROVIDER_IDENTIFIER = "BC";	
	private Cipher cipher;
	
	byte[] keyBytes = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
	private SecretKey key = new SecretKeySpec(keyBytes, "AES");

	/**
     * Creates an EncryptionService instance and initializes the cipher.
     * 
     * @throws Exception if there is a cipher error.
     */
	public EncryptionService() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER_IDENTIFIER);		
	}
	
	/**
     * Encrypts the plain text using the key and initialization vector.
     * 
     * @param plainText the plain text to encrypt.
     * @param initializationVector the initialization vector.
     * @return the encrypted text.
     * @throws Exception if there is an encryption error.
     */
	public byte[] encrypt(byte[] plainText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(plainText);
	}
	
	/**
     * Decrypts the cipher text using the key and initialization vector.
     * 
     * @param cipherText the cipher text to decrypt.
     * @param initializationVector the initialization vector.
     * @return the decrypted text.
     * @throws Exception if there is a decryption error.
     */
	public byte[] decrypt(byte[] cipherText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(cipherText);
	}
	
}
