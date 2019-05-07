package com.liyf.messageprocessorlib.utils;

public class RaiseX {

	/**
	 * 
	 * @param buf 为需要调节音量的音频数据块首地址指针
	 * @param size 为长度
	 * @param uRepeat 为重复次数 通常设为1
	 * @param vol 为增益倍数,可以小于1 
	 */
	public static void RaiseVolume(byte[] buf, int size, int uRepeat, double vol){
		if (size == 0)
			return;
		for (int i = 0; i < size; i += 2){
			byte[] toshortb = new byte[]{buf[i],buf[i+1]};
			short wData = getShort(toshortb, 0);
			long dwData = wData;
			for (int j = 0; j < uRepeat; j++){
	            dwData = (long) (dwData * vol);  
	            if (dwData < -0x8000){  
	                dwData = -0x8000;  
	            }else if (dwData > 0x7FFF)   {
	                dwData = 0x7FFF;  
	            }  
	        } 
			byte[] bb = new byte[8];
			putLong(bb, dwData, 0);
			wData = getShort(bb, 0);
//			wData = LOWORD(dwData);
			byte[] b = new byte[2];
			putShort(b, wData, 0);
//	        buf[i] = LOBYTE(wData);  
//	        buf[i + 1] = HIBYTE(wData);
			buf[i] = b[0];  
	        buf[i + 1] = b[1];
			
		}
			
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** 
     * 转换short为byte 
     *  
     * @param b 
     * @param s 
     *            需要转换的short 
     * @param index 
     */  
    public static void putShort(byte b[], short s, int index) {  
        b[index + 1] = (byte) (s >> 8);  
        b[index + 0] = (byte) (s >> 0);  
    } 
	
	/** 
     * 通过byte数组取到short 
     *  
     * @param b 
     * @param index 
     *            第几位开始取 
     * @return 
     */  
    public static short getShort(byte[] b, int index) {  
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));  
    }
    /** 
     * 转换long型为byte数组 
     *  
     * @param bb 
     * @param x 
     * @param index 
     */  
    public static void putLong(byte[] bb, long x, int index) {  
        bb[index + 7] = (byte) (x >> 56);  
        bb[index + 6] = (byte) (x >> 48);  
        bb[index + 5] = (byte) (x >> 40);  
        bb[index + 4] = (byte) (x >> 32);  
        bb[index + 3] = (byte) (x >> 24);  
        bb[index + 2] = (byte) (x >> 16);  
        bb[index + 1] = (byte) (x >> 8);  
        bb[index + 0] = (byte) (x >> 0);  
    } 
}
