package me.linx.vchat.core.constants;

public class FilesConstants {
    //单文件最大值
    public static final int MAX_SIZE = Integer.MAX_VALUE;

    //文件分片大小，传输的文件超过64KB将启用断点续传
    public static final int PART_ONT_SIZE = 64 * 1024;
}
