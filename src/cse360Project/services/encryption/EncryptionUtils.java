package cse360Project.services.encryption;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/*******
 * <p> EncryptionUtils. </p>
 * 
 * <p> Description: A utility class for encrypting and decrypting data. </p>
 * 
 * <p> Copyright: Robert Lynn Carter © 2024. Modified by CSE 360 Team Th02 </p>
 * 
 * @author Robert Lynn Carter.
 * 
 * @version 1.00 2024-10-30 Phase two
 * 
 */
public class EncryptionUtils {
	private static int IV_SIZE = 16;
	
	/**
     * Converts a byte array to a char array.
     * 
     * @param bytes the byte array to convert.
     * @return the converted char array.
     */
	public static char[] toCharArray(byte[] bytes) {		
        CharBuffer charBuffer = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(charBuffer.array(), charBuffer.limit());
	}

	/**
     * Converts a char array to a byte array.
     * 
     * @param chars the char array to convert.
     * @return the converted byte array.
     */
	public static byte[] toByteArray(char[] chars) {		
        ByteBuffer byteBuffer = Charset.defaultCharset().encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
	}
		
	/**
     * Generates an initialization vector from a char array.
     * 
     * @param text the char array to generate the IV from.
     * @return the generated IV.
     */
	public static byte[] getInitializationVector(char[] text) {
		char iv[] = new char[IV_SIZE];
		
		int textPointer = 0;
		int ivPointer = 0;
		while(ivPointer < IV_SIZE) {
			iv[ivPointer++] = text[textPointer++ % text.length];
		}
		
		return toByteArray(iv);
	}
	
	/**
     * Prints a char array.
     * 
     * @param chars the char array to print.
     */
	public static void printCharArray(char[] chars) {
		for(char c : chars) {
			System.out.print(c);
		}
	}
}
