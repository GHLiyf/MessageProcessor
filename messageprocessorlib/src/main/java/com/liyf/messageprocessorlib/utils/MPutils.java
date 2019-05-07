package com.liyf.messageprocessorlib.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class MPutils {
    public static final String TAG = "MPutils";


    /**
     * 获取时间戳
     */
    public static String getFileNamecurrentTimeMillis() {
        return System.currentTimeMillis() + "";
    }

    /**
     * 时间戳转时间
     * 格式 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getFormat(String time) {
        long timemillis = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timemillis);
        return sdf.format(date);
    }

    // 获取当前目录下所有的amr文件
    public static Vector<String> GetVideoFileName(String fileAbsolutePath) {
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为MP4结尾
                if (filename.trim().toLowerCase().endsWith(".amr")) {
                    vecFile.add(filename);
                }
            }
        }
        return vecFile;
    }


    /**
     * 得到amr的时长
     *
     * @param file
     * @return amr文件时间长度
     * @throws IOException
     */
    public static int getAmrDuration(File file) {
        long duration = -1;
        int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0};
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            long length = file.length();// 文件的长度
            int pos = 6;// 设置初始位置
            int frameCount = 0;// 初始帧数
            int packedPos = -1;

            byte[] datas = new byte[1];// 初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }

            duration += frameCount * 20;// 帧数*20
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "获取文件时间异常1");
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "获取文件时间异常2");
                }
            }
        }
        return (int) ((duration / 1000) + 1);
    }

    /**
     * px->dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp->px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取视频的第一贞图像
     *
     * @param filePath
     * @return
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    /**
     * 更改edittext中hint 的文字大小
     *
     * @param editText
     * @param hintText
     * @param textSize
     */
    public static void setHintTextSize(EditText editText, String hintText, int textSize) {
        Log.i(TAG, "HintTextSize == " + textSize);
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString(hintText);
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(textSize, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置hint
        editText.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }

    /**
     * Intent转String path
     *
     * @param data
     * @return
     */
    public static String getpath(Intent data) {
        String name = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".jpg";
        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

        FileOutputStream b = null;
        //???????????????????????????????为什么不能直接保存在系统相册位置呢？？？？？？？？？？？？
        File file = new File(Environment.getExternalStorageDirectory() + "/BR/myImage/");
        if (!file.exists()) {
            file.mkdirs();// 创建文件夹
        }
        String fileName = Environment.getExternalStorageDirectory() + "/BR/myImage/" + name;

        try {
            b = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName + "@" + name;
    }


    /**
     * uri转String
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String convertUriToString(Context context, Uri contentUri) {
        return GetPathFromUri4kitkat.getPath(context, contentUri);
    }

    /**
     * 获取文件名（不含.xxx）
     *
     * @param f
     * @return
     */
    public static String getFileSuffix(File f) {
        String name = f.getName();
        if (name != null && !"".equals(name)) {
            if (name.contains(".")) {
                return name.substring(name.lastIndexOf("."));
            }
        }
        return name;
    }

    /**
     * 文件复制
     *
     * @param f1
     * @param f2
     * @return
     */
    public static long forChannel(File f1, File f2) {

        long time = new Date().getTime();
        int length = 2097152;
        try {
            FileInputStream in = new FileInputStream(f1);
            FileOutputStream out = new FileOutputStream(f2);
            FileChannel inC = in.getChannel();
            FileChannel outC = out.getChannel();
            ByteBuffer b = null;
            while (true) {
                if (inC.position() == inC.size()) {
                    inC.close();
                    outC.close();
                    return new Date().getTime() - time;
                }
                if ((inC.size() - inC.position()) < length) {
                    length = (int) (inC.size() - inC.position());
                } else
                    length = 2097152;
                b = ByteBuffer.allocateDirect(length);
                inC.read(b);
                b.flip();
                outC.write(b);
                outC.force(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "文件复制异常");
        }
        return 0;
    }
//	/**** 创建对话框 */
//	public static ProgressDialog createDialog(Context context, boolean isMsg) {
//		ProgressDialog dialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
//		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		dialog.setTitle("请稍后");
//		if (isMsg) {
//			dialog.setMessage("正在发送");
//		}else {
//			dialog.setMessage("正在上传");
//		}
//		dialog.setMax(100);
//		dialog.setCancelable(true);
//		dialog.setCanceledOnTouchOutside(false);
//		return dialog;
//	}

    /**
     * Java文件操作 获取文件扩展名
     * IMG_20170905_100435.jpg.mp3-->mp3
     * Created on: 2017-9-28
     * Author: lyf
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     * IMG_20170905_100435.jpg.mp3-->IMG_20170905_100435.jpg
     * Created on: 2017-9-28
     * Author: lyf
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 所有文件后缀
     */
    public static final String[][] MIME_MapTable = {
            //{后缀名， MIME类型}
            {".amr", "audio/*"},
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };


    public static void SENDMESSAGETO(Handler handler, int menu, Object object) {
        if (handler != null) {
            Message.obtain(handler, menu, object).sendToTarget();
        }
    }

    /**
     * 安全删除文件.
     *
     * @param file
     * @return
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    /**
     * 通过包名启动app
     *
     * @param packagename
     * @param context
     */
    public static void doStartApplicationWithPackageName(String packagename, Context context) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }


    /**
     * 将字节数组转换为字符串
     */
    public static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }


    /**
     * 往nfc写入数据
     */
    public static boolean writeNFCToTag(Intent intent) {
        boolean res = false;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            ultralight.writePage(4, "中国".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(5, "美国".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(6, "韩国".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(7, "日本".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(8, "伊朗".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(9, "泰国".getBytes(Charset.forName("GB2312")));
            Log.i(TAG, "写入MifareUltralight格式数据成功");
        } catch (Exception e) {
            // TODO: handle exception
            res = false;
        } finally {
            try {
                ultralight.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                res = false;
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 获取日期（不包含时间）
     *
     * @return yyyy-MM-dd
     */
    public static String getCurrentDay() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
