package com.juxin.library.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件操作工具
 */
public class FileUtil {

    /**
     * 判断是否是URL。
     *
     * @param url 要判定的url。
     * @return 如果是一个url，返回true。
     */
    public static boolean isURL(String url) {
        if (TextUtils.isEmpty(url)) return false;
        if (url.startsWith("http://")) {
            return true;
        } else if (url.startsWith("https://")) {
            return true;
        } else if (url.startsWith("ftp://")) {
            return true;
        }
        return false;
    }

    /**
     * 判断文件夹是否存在，如果不存在则创建
     *
     * @param strFolder 文件夹路径
     * @return 是否存在或者创建成功
     */
    public static boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取文件扩展名。
     *
     * @param filename 文件名。
     * @return 返回扩展名。
     */
    public static String getExtensionName(String filename, String defName) {
        if (TextUtils.isEmpty(filename)) return defName;

        String extensionName = "";
        int dot = filename.lastIndexOf('.');

        if (dot > -1 && dot < (filename.length() - 1)) {
            extensionName = filename.substring(dot + 1);
        }

        if (TextUtils.isEmpty(extensionName)) {
            return defName;
        }
        return extensionName;
    }

    /**
     * 获取文件名。
     *
     * @param filename 文件名。
     * @return 返回文件名。
     */
    public static String getFileNameNoEx(String filename) {
        if (TextUtils.isEmpty(filename)) return "";

        int dot = filename.lastIndexOf('.');
        if ((dot > -1) && (dot < (filename.length()))) {
            return filename.substring(0, dot);
        }
        return filename;
    }

    /**
     * 从文件路径中切出带后缀的文件名
     *
     * @param filePath 文件路径
     * @return 带后缀的文件名
     */
    public static String getFileNameFromUrl(String filePath) {
        if (TextUtils.isEmpty(filePath)) return "";

        String fileName = "";
        int slash = filePath.lastIndexOf('/');
        if ((slash > -1) && (slash + 1 < (filePath.length()))) {
            fileName = filePath.substring(slash + 1, filePath.length());
        }
        fileName = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]").matcher(fileName).replaceAll("");
        return fileName;
    }

    /**
     * 写文件。
     *
     * @param fileName 文件路径。
     * @param bytes    数据。
     */
    public static void writeFileSdcard(String fileName, byte[] bytes) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(bytes);
            fos.close();
        } catch (Throwable t) {
            PLogger.printThrowable(t);
        }
    }

    /**
     * 获取除扩展名以外的部分
     *
     * @param fileName
     * @return
     */
    public static String getFileNameWithoutSuffix(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        int lastIndex = fileName.lastIndexOf(".");
        String fileNameWithoutSuffix = "";
        if (lastIndex != -1) {
            fileNameWithoutSuffix = fileName.substring(0, lastIndex);
        }
        return fileNameWithoutSuffix;
    }

    /**
     * 获取文件扩展名
     *
     * @param path
     * @return
     */
    public static String getFileExtension(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        int index = path.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        String extension = path.substring(index + 1, path.length());
        if (TextUtils.isEmpty(extension)) {
            return "";
        }
        return extension;
    }

    /**
     * 获取文件目录
     */
    public static String getFilePath(String fullPathName) {
        int lastIndex = fullPathName.lastIndexOf(File.separator);
        String path = "";
        if (lastIndex != -1) {
            path = fullPathName.substring(0, lastIndex);
        }
        return path;
    }

    /**
     * 获取不带路径和后缀的文件名
     *
     * @param path
     * @return
     */
    public static String getFileNameByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        int separatorIndex = path.lastIndexOf(File.separator);
        if (separatorIndex > 0 && separatorIndex != path.length() - 1) {
            String fullName = path.substring(separatorIndex + 1, path.length());
            String name = getFileNameWithoutSuffix(fullName);
            return name;
        }
        return path;
    }

    /**
     * 获取不带路径的文件名
     *
     * @param path
     * @return
     */
    public static String getFullFileNameByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }

        int separatorIndex = path.lastIndexOf(File.separator);

        if (separatorIndex > 0 && separatorIndex != path.length() - 1) {
            String fullName = path.substring(separatorIndex + 1, path.length());
            return fullName;
        }

        return path;
    }

    // 文件改名
    public static boolean fileMove(String from, String to, boolean overwrite) {
        File fromFile = new File(from);
        if (!fromFile.exists()) {
            return false;
        }
        File toFile = new File(to);
        if (toFile.exists()) {
            if (overwrite) {
                toFile.delete();
            } else {
                return false;
            }
        }
        return fromFile.renameTo(toFile);
    }

    // 过滤掉不可当文件名的字符
    static public String delInvalidFileNameStr(String title) {
        if (title != null && title.length() > 0) {
            String illegal = "[`\\\\~!@#\\$%\\^&\\*+=\\|\\{\\}:;\\,/\\.<>\\?·\\s]";
            Pattern pattern = Pattern.compile(illegal);
            Matcher matcher = pattern.matcher(title);
            return matcher.replaceAll("_").trim();
        }

        return title;
    }


    /**
     * 创建空文件
     *
     * @param path 待创建的文件路径
     * @param size 空文件大小
     * @return 创建是否成功
     * @throws IOException
     */
    public static boolean createEmptyFile(String path, long size) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(path, "rw");
            try {
                raf.setLength(size);
            } finally {
                raf.close();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return 是否存在
     */
    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        return file.exists();
    }

    // 拷贝文件
    public static boolean fileCopy(String from_file, String to_file) {
        return fileCopy(new File(from_file), new File(to_file));
    }

    public static boolean fileCopy(File from_file, File to_file) {
        if (!from_file.exists()) {
            return false;
        }

        if (to_file.exists()) {
            to_file.delete();
        }
        boolean success = true;
        FileInputStream from = null;
        FileOutputStream to = null;
        byte[] buffer;
        try {
            buffer = new byte[1024];
        } catch (OutOfMemoryError oom) {
            return false;
        }
        try {
            from = new FileInputStream(from_file);
            to = new FileOutputStream(to_file); // Create output stream
            int bytes_read;

            while ((bytes_read = from.read(buffer)) != -1) {
                // Read until EOF
                to.write(buffer, 0, bytes_read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            buffer = null;
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                }
                from = null;
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
                to = null;
            }
        }

        if (!success) {
            to_file.delete();
        }

        return success;
    }

    /**
     * 将文件从assets目录拷贝到指定文件目录
     *
     * @param context  上下文，用于读取assets目录
     * @param fileName 文件在assets目录中的文件名
     * @param path     文件拷贝路径
     * @return 是否拷贝成功
     */
    public static boolean copyFileFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(fileName);
            File file = new File(path);
            if (file.exists()) file.delete();
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }
        return copyIsFinish;
    }

    /**
     * 删除文件或者目录
     *
     * @param path 指定路径的文件或目录
     * @return 返回操作结果
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        File file = new File(path);
        if (!file.exists()) return true;

        if (file.isDirectory()) {
            String[] subPaths = file.list();
            for (String p : subPaths) {
                if (!deleteFile(path + File.separator + p)) {
                    return false;
                }
            }
        }

        return file.delete();
    }

    /**
     * 删除文件
     * @param list 文件路径列表
     */
    public static void deleteFiles(List<String> list) {
        if(list == null || list.size() == 0) {
            return;
        }
        for (String str : list) {
            if(!TextUtils.isEmpty(str)) {
                File file = new File(str);
                if (!file.exists()) return;
                file.delete();
            }
        }
    }

    public static boolean deleteFilesExcept(String path, String fileName) {
        File[] files = getFiles(path);
        if (files != null) {
            for (File f : files) {
                if (!f.getAbsolutePath().endsWith(fileName)) {
                    f.delete();
                }
            }
        }
        return true;
    }

    /**
     * 创建目录，包括必要的父目录的创建，如果未创建
     *
     * @param path 待创建的目录路径
     * @return 返回操作结果
     */
    public static boolean mkdir(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return true;
        }

        return file.mkdirs();
    }

    /**
     * 检查当前sdcard剩余空间大小
     */
    public static long getAvailableExternalMemorySize() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = null;
            try {
                stat = new StatFs(path.getPath());
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }

            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long availableSize = availableBlocks * blockSize;
            return availableSize - 5 * 1024 * 1024;// 预留5M的空间
        }
        return -1;
    }

    /**
     * 检查当前sdcard剩余空间大小是否够用，32M以上返回true
     */
    public static boolean isExternalSpaceAvailable() {
        return getAvailableExternalMemorySize() > 32 * 1024 * 1024;
    }

    /**
     * 获取当前目录的文件夹列表
     */
    public static ArrayList<File> getDirs(String path) {
        ArrayList<File> fileList = new ArrayList<File>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        fileList.add(f);
                    }
                }
            }
        }
        return fileList;
    }

    public static File[] getFiles(String path) {
        return getFiles(path, null);
    }

    /**
     * 获取当前目录的文件列表
     */
    public static File[] getFiles(String path, final String[] filters) {
        File file = new File(path);
        if (!file.isDirectory()) {
            return null;
        }

        FilenameFilter filter = null;
        if (filters != null && filters.length > 0) {
            filter = new FilenameFilter() {
                @Override
                public boolean accept(File directory, String filename) {
                    if (!TextUtils.isEmpty(filename)) {
                        for (String type : filters) {
                            if (filename.toLowerCase().endsWith(type)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };
        }

        File[] fileList = file.listFiles(filter);
        return fileList;
    }

    /**
     * 获取当前目录的文件列表，用正则匹配
     */
    public static File[] getFilesByRegex(String path, final String regex,
                                         final String exceptRegex) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(regex)) {
            return null;
        }
        File[] fileList = null;
        File file = new File(path);
        if (file.isDirectory()) {
            fileList = file.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File directory, String filename) {
                    if (filename != null && !"".equals(filename)) {
                        try {
                            if (filename.matches(regex)) {
                                if (exceptRegex == null || exceptRegex.length() == 0) {
                                    return true;
                                } else {
                                    return !filename.matches(exceptRegex);
                                }
                            }
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return false;
                }
            });
        }

        return fileList;
    }

    public static long getFileSize(final String file) {
        if (TextUtils.isEmpty(file)) {
            return 0;
        }
        return new File(file).length();
    }

    // 传统的＊和？匹配
    public static File[] getFilesClassic(final String dir, final String pattern) {
        if (TextUtils.isEmpty(dir) || TextUtils.isEmpty(pattern)) {
            return null;
        }
        StringBuilder builder = new StringBuilder("^");
        int state = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char word = pattern.charAt(i);
            if (state == 0) {
                if (word == '?') {
                    builder.append('.');
                } else if (word == '*') {
                    builder.append(".*");
                } else {
                    builder.append("\\Q");
                    builder.append(word);
                    state = 1;
                }
            } else {
                if (word == '?' || word == '*') {
                    builder.append("\\E");
                    state = 0;
                    if (word == '?') {
                        builder.append('.');
                    } else {
                        builder.append(".*");
                    }
                } else {
                    builder.append(word);
                }
            }
        }
        if (state == 1) {
            builder.append("\\E");
        }
        builder.append('$');
        ArrayList<File> list = null;
        try {
            Pattern p = Pattern.compile(builder.toString());
            list = filePattern(new File(dir), p);
        } catch (Exception e) {
            return null;
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        File[] rtn = new File[list.size()];
        list.toArray(rtn);
        return rtn;
    }

    private static ArrayList<File> filePattern(File file, Pattern p) {
        if (file == null) {
            return null;
        } else if (file.isFile()) {
            Matcher fMatcher = p.matcher(file.getName());
            if (fMatcher.matches()) {
                ArrayList<File> list = new ArrayList<File>();
                list.add(file);
                return list;
            }
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                ArrayList<File> list = new ArrayList<File>();
                for (File f : files) {
                    if (p.matcher(f.getName()).matches()) {
                        list.add(f);
                    }
                }
                return list;
            }
        }
        return null;
    }

    // 李建衡：读文件。如果无法读出数据，就返回null
    public static String fileRead(String file) {
        if (TextUtils.isEmpty(file)) {
            return null;
        }

        byte[] buffer = fileRead(new File(file));

        if (buffer == null) {
            return null;
        }

        return new String(buffer);
    }

    public static String fileRead(String file, String charsetName) {
        if (TextUtils.isEmpty(file) || TextUtils.isEmpty(charsetName)) {
            return null;
        }

        byte[] buffer = fileRead(new File(file));

        if (buffer == null) {
            return null;
        }

        try {
            return new String(buffer, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] fileReadBytes(String file) {
        if (TextUtils.isEmpty(file)) {
            return null;
        }

        return fileRead(new File(file));
    }

    public static byte[] fileRead(File file) {
        byte[] buffer = null;

        if (!file.exists()) {
            return buffer;
        }

        FileInputStream fis;

        try {
            fis = new FileInputStream(file);
            try {
                int len = fis.available();
                buffer = new byte[len];
                fis.read(buffer);
            } finally {
                fis.close();
            }
        } catch (Throwable e) { // new byte有可能是OOM异常，要用Throwable跟IOException一起捕获
            e.printStackTrace();
            return null;
        }

        return buffer;
    }

    public static boolean fileWrite(String file, String content) {
        if (TextUtils.isEmpty(file) || TextUtils.isEmpty(content)) {
            return false;
        }

        return fileWrite(new File(file), content);
    }

    public static boolean fileWrite(File file, String content) {
        if (file == null || TextUtils.isEmpty(content)) {
            return false;
        }

        if (file.exists()) {
            file.delete();
        }

        FileOutputStream to = null;

        try {
            to = new FileOutputStream(file);
            to.write(content.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            file.delete();
        } finally {
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
                to = null;
            }
        }

        return false;
    }

    /**
     * 获取文件夹下的所有文件路径
     *
     * @param path
     * @return 所有文件路径的集合
     */
    public static ArrayList<String> getFilesPahts(String path) {
        ArrayList<String> paths = new ArrayList<String>();
        File file = new File(path);
        getAllFiles(file, paths);
        return paths;
    }

    // 遍历接收一个文件路径，然后把文件子目录中的所有文件遍历并添加到集合中
    private static void getAllFiles(File root, ArrayList<String> paths) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    getAllFiles(f, paths);
                } else {
                    paths.add(f.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 从assets里边读取字符串
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readAssets(Context context, String fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            if (is != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 解压zip文件
     * @param zipFileString zip文件路径
     * @param outPathString 解压后文件路径
     * @throws Exception
     */
    public static String unZipFile(String zipFileString, String outPathString){
        String path = null;
        try {

            int count = 0;
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                count++;
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    // get the folder name of the widget
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                    if(count == 1)
                        path = folder.getAbsolutePath();
                } else {

                    File file = new File(outPathString + File.separator + szName);
                    file.createNewFile();
                    // get the output stream of the file
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // read (len) bytes into buffer
                    while ((len = inZip.read(buffer)) != -1) {
                        // write (len) byte from buffer at the position 0
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            inZip.close();
        }catch (Exception e){
            return null;
        }
        return path;
    }

}
