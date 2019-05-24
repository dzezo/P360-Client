package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import main.Main;

public class ImageCipher {
	
	private static String createEncryptedImagePath(String path) {
		int extension = path.lastIndexOf(".");
		return path.substring(0, extension) + ".pimg";
	}
	
	public static boolean isEncrypted(String path) {
		String extension = path.substring(path.lastIndexOf("."), path.length());
		if(extension.equals(".pimg"))
			return true;
		else
			return false;
	}
	
	/**
	 * Vrsi enkripciju slike, tako sto XOR-uje svaki bajt slike sa jednim karakterom iz kljuca.
	 * @param imagePath - putanja do slike koju treba enkriptovati
	 * @param key - kljuc za enkripciju
	 * @return putanja do enkriptovane slike
	 * @throws Exception IOException / KeyNotFound
	 */
	public static String imageEncrypt(String imagePath, String key) throws Exception {
		if(key == null) throw new Exception("Encryption key not found.");
		
		String encryptedImagePath = createEncryptedImagePath(imagePath);
		
		FileInputStream inStream = new FileInputStream(imagePath);
		FileOutputStream outStream = new FileOutputStream(encryptedImagePath);
		
		byte[] buffer = new byte[1024];
		byte[] outBuffer;
		
		int keyItr = 0;
		int byteNum;
		
		// write key
		byte[] encryptionKey = new byte[32];
		for(int i = 0; i < encryptionKey.length; i++) {
			if(i < key.length()) {
				encryptionKey[i] = (byte) (key.charAt(i) & 0x00FF); // ASCII support
			}
			else {
				encryptionKey[i] = 0;
			}
		}
		outStream.write(encryptionKey);
		
		// write encrypted bytes
		while((byteNum = inStream.read(buffer)) != -1) {
			outBuffer = new byte[byteNum];
			for(int i=0; i < byteNum; i++) {
				outBuffer[i] = (byte) (buffer[i] ^ encryptionKey[keyItr]);
				keyItr = (keyItr + 1) % encryptionKey.length;
			}
			outStream.write(outBuffer);
		}
		
		outStream.flush();
		outStream.close();
		inStream.close();
		
		return encryptedImagePath;
	}
	
	/**
	 * Vrsi dekripciju slike, tako sto XOR-uje svaki bajt slike sa jednim karakterom iz kljuca.
	 * @param imagePath - putanja do slike koju treba dekriptovati
	 * @return dekriptovane bajtove slike
	 * @throws Exception IOException
	 */
	public static byte[] imageDecrypt(String imagePath) throws Exception {
		FileInputStream inStream = new FileInputStream(imagePath);
		File image = new File(imagePath);
		
		byte[] buffer = new byte[1024];
		byte[] outBuffer = new byte[(int) image.length()];
		
		int keyItr = 0;
		int buffItr = 0;
		int byteNum;
		
		// read key
		byte[] key = new byte[32];
		inStream.read(key, 0, key.length);
		
		// read image bytes
		while((byteNum = inStream.read(buffer)) != -1) {
			for(int i=0; i < byteNum; i++) {
				outBuffer[buffItr++] = (byte) (buffer[i] ^ key[keyItr]);
				keyItr = (keyItr + 1) % key.length;
			}
		}
		
		inStream.close();
		return outBuffer;
	}
	
	/**
	 * Vrsi re-enkripciju sliku
	 * @param imagePath - putanja do slike koju treba re-enkriptovati
	 * @param key - novi kljuc za enkripciju
	 * @throws Exception IOException / KeyNotFound
	 */
	public static void imageReEncrypt(String imagePath, String key) throws Exception {
		if(key == null) throw new Exception("New encryption key not found.");
		
		// Decrypted data is at working_dir\tmp.pimg
		
		String strSrcImage = imagePath;
		String strDstImage = Main.WORKING_DIR.getPath() + "\\tmp.pimg";
		
		FileInputStream inStream = new FileInputStream(strSrcImage);
		FileOutputStream outStream = new FileOutputStream(strDstImage);
		
		byte[] buffer = new byte[1024];
		byte[] outBuffer;
		
		int oldKeyItr = 0;
		int newKeyItr = 0;
		int byteNum;
		
		// Read oldKey
		byte[] oldKey = new byte[32];
		inStream.read(oldKey, 0, oldKey.length);
		
		// Decrypt with old key
		while((byteNum = inStream.read(buffer)) != -1) {
			outBuffer = new byte[byteNum];
			for(int i=0; i < byteNum; i++) {
				outBuffer[i] = (byte) (buffer[i] ^ oldKey[oldKeyItr]);
				
				oldKeyItr = (oldKeyItr + 1) % oldKey.length;
			}
			
			outStream.write(outBuffer);
		}
		
		outStream.flush();
		outStream.close();
		inStream.close();
		
		// Encrypt data back to source path
		inStream = new FileInputStream(strDstImage);
		outStream = new FileOutputStream(strSrcImage);
		
		// Generate and write new key
		byte[] newKey = new byte[32];
		for(int i = 0; i < newKey.length; i++) {
			if(i < key.length()) {
				newKey[i] = (byte) (key.charAt(i) & 0x00FF); // ASCII support
			}
			else {
				newKey[i] = 0;
			}
		}
		outStream.write(newKey);
		
		// Encrypt with new key
		while((byteNum = inStream.read(buffer)) != -1) {
			outBuffer = new byte[byteNum];
			for(int i=0; i < byteNum; i++) {
				outBuffer[i] = (byte) (buffer[i] ^ newKey[newKeyItr]);
				
				newKeyItr = (newKeyItr + 1) % newKey.length;
			}
			
			outStream.write(outBuffer);
		}
		
		outStream.flush();
		outStream.close();
		inStream.close();
		
		// Remove tmp.pimg
		File file = new File(strDstImage);
		file.delete();
	}
}
