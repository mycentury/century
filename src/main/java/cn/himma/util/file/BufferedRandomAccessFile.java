package cn.himma.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年8月12日
 * @ClassName BufferedRandomAccessFile
 */
public final class BufferedRandomAccessFile extends RandomAccessFile {

    private static final int DEFAULT_BUFFER_BIT_LEN = 10;
    private static final int DEFAULT_BUFFER_SIZE = 1 << DEFAULT_BUFFER_BIT_LEN;// 1024
    private static final byte LINE_BREAK = 10;
    private static final byte CARRIAGE_RETURN = 13;

    private byte buf[];
    private int bufbitlen;
    private int bufsize;
    private long bufmask;
    private boolean bufdirty;
    private int bufusedsize;
    private long curpos;
    private byte[] lastNextLine;

    private long bufstartpos;
    private long bufendpos;
    private long fileendpos;

    private boolean append;
    private String filename;
    private long initfilelen;

    public BufferedRandomAccessFile(String name) throws IOException {
        this(name, "r", DEFAULT_BUFFER_BIT_LEN);
    }

    public BufferedRandomAccessFile(File file) throws IOException, FileNotFoundException {
        this(file.getPath(), "r", DEFAULT_BUFFER_BIT_LEN);
    }

    public BufferedRandomAccessFile(String name, int bufbitlen) throws IOException {
        this(name, "r", bufbitlen);
    }

    public BufferedRandomAccessFile(File file, int bufbitlen) throws IOException, FileNotFoundException {
        this(file.getPath(), "r", bufbitlen);
    }

    public BufferedRandomAccessFile(String name, String mode) throws IOException {
        this(name, mode, DEFAULT_BUFFER_BIT_LEN);
    }

    public BufferedRandomAccessFile(File file, String mode) throws IOException, FileNotFoundException {
        this(file.getPath(), mode, DEFAULT_BUFFER_BIT_LEN);
    }

    public BufferedRandomAccessFile(String name, String mode, int bufbitlen) throws IOException {
        super(name, mode);
        this.init(name, mode, bufbitlen);
    }

    public BufferedRandomAccessFile(File file, String mode, int bufbitlen) throws IOException, FileNotFoundException {
        this(file.getPath(), mode, bufbitlen);
    }

    public String readLineByBuffer() throws IOException {
        int readLength = 0;
        byte[] data = null;
        while (lastNextLine != null || (readLength = super.read(buf)) != -1) {
            byte[] temp = lastNextLine != null ? lastNextLine : Arrays.copyOfRange(buf, 0, readLength);
            lastNextLine = null;
            System.out.println(Arrays.toString(temp));
            int index = indexOfEleInArray(temp, LINE_BREAK);
            index = indexOfEleInArray(temp, CARRIAGE_RETURN);
            if (temp[0] == LINE_BREAK) {
                temp = Arrays.copyOfRange(temp, 1, temp.length);
            }
            if (index >= 0) {
                // 返回长度
                curpos += index + 2;
                lastNextLine = index + 2 < temp.length ? Arrays.copyOfRange(temp, index + 2, temp.length) : null;
                // this.seek(curpos);
                data = addTwoArrays(data, Arrays.copyOfRange(temp, 0, index));
                break;
            } else {
                // curpos += readLength;
                data = addTwoArrays(data, temp);
            }
        }
        // 文件末尾
        // if (readLength > 0 && readLength < buf.length) {
        // long pos = super.getFilePointer() + 1;
        // super.seek(pos);
        // }
        return data == null ? null : new String(data, "UTF-8");
    }

    private int indexOfEleInArray(byte[] data, byte lineBreak) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == lineBreak) {
                return i;
            }
        }
        return -1;
    }

    private byte[] addTwoArrays(byte[] former, byte[] latter) {
        if (former == null) {
            return latter;
        } else if (latter == null) {
            return former;
        }
        byte[] array3 = new byte[former.length + latter.length];
        System.arraycopy(former, 0, array3, 0, former.length);
        System.arraycopy(latter, 0, array3, former.length, latter.length);
        return array3;
    }

    private void init(String name, String mode, int bufbitlen) throws IOException {
        if (mode.equals("r") == true) {
            this.append = false;
        } else {
            this.append = true;
        }

        this.filename = name;
        this.initfilelen = super.length();
        this.fileendpos = this.initfilelen - 1;
        this.curpos = super.getFilePointer();

        if (bufbitlen < 0) {
            throw new IllegalArgumentException("bufbitlen size must >= 0");
        }

        this.bufbitlen = bufbitlen;
        this.bufsize = 1 << bufbitlen;
        this.buf = new byte[this.bufsize];
        this.bufmask = ~(this.bufsize - 1L);
        this.bufdirty = false;
        this.bufusedsize = 0;
        this.bufstartpos = -1;
        this.bufendpos = -1;
    }

    private void flushbuf() throws IOException {
        if (this.bufdirty == true) {
            if (super.getFilePointer() != this.bufstartpos) {
                super.seek(this.bufstartpos);
            }
            super.write(this.buf, 0, this.bufusedsize);
            this.bufdirty = false;
        }
    }

    private int fillbuf() throws IOException {
        super.seek(this.bufstartpos);
        this.bufdirty = false;
        return super.read(this.buf);
    }

    public byte read(long pos) throws IOException {
        if (pos < this.bufstartpos || pos > this.bufendpos) {
            this.flushbuf();
            this.seek(pos);

            if ((pos < this.bufstartpos) || (pos > this.bufendpos)) {
                throw new IOException();
            }
        }
        this.curpos = pos;
        return this.buf[(int) (pos - this.bufstartpos)];
    }

    public boolean write(byte bw) throws IOException {
        return this.write(bw, this.curpos);
    }

    public boolean append(byte bw) throws IOException {
        return this.write(bw, this.fileendpos + 1);
    }

    public boolean write(byte bw, long pos) throws IOException {

        if ((pos >= this.bufstartpos) && (pos <= this.bufendpos)) { // write pos in buf
            this.buf[(int) (pos - this.bufstartpos)] = bw;
            this.bufdirty = true;

            if (pos == this.fileendpos + 1) { // write pos is append pos
                this.fileendpos++;
                this.bufusedsize++;
            }
        } else { // write pos not in buf
            this.seek(pos);

            if ((pos >= 0) && (pos <= this.fileendpos) && (this.fileendpos != 0)) { // write pos is modify file
                this.buf[(int) (pos - this.bufstartpos)] = bw;

            } else if (((pos == 0) && (this.fileendpos == 0)) || (pos == this.fileendpos + 1)) { // write pos is append
                                                                                                 // pos
                this.buf[0] = bw;
                this.fileendpos++;
                this.bufusedsize = 1;
            } else {
                throw new IndexOutOfBoundsException();
            }
            this.bufdirty = true;
        }
        this.curpos = pos;
        return true;
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {

        long writeendpos = this.curpos + len - 1;

        if (writeendpos <= this.bufendpos) { // b[] in cur buf
            System.arraycopy(b, off, this.buf, (int) (this.curpos - this.bufstartpos), len);
            this.bufdirty = true;
            this.bufusedsize = (int) (writeendpos - this.bufstartpos + 1);// (int)(this.curpos - this.bufstartpos + len
                                                                          // - 1);

        } else { // b[] not in cur buf
            super.seek(this.curpos);
            super.write(b, off, len);
        }

        if (writeendpos > this.fileendpos) this.fileendpos = writeendpos;

        this.seek(writeendpos + 1);
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {

        long readendpos = this.curpos + len - 1;

        if (readendpos <= this.bufendpos && readendpos <= this.fileendpos) { // read in buf
            System.arraycopy(this.buf, (int) (this.curpos - this.bufstartpos), b, off, len);
        } else { // read b[] size > buf[]

            if (readendpos > this.fileendpos) { // read b[] part in file
                len = (int) (this.length() - this.curpos + 1);
            }

            super.seek(this.curpos);
            len = super.read(b, off, len);
            readendpos = this.curpos + len - 1;
        }
        this.seek(readendpos + 1);
        return len;
    }

    @Override
    public void write(byte b[]) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public int read(byte b[]) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public void seek(long pos) throws IOException {

        if ((pos < this.bufstartpos) || (pos > this.bufendpos)) { // seek pos not in buf
            this.flushbuf();

            if ((pos >= 0) && (pos <= this.fileendpos) && (this.fileendpos != 0)) { // seek pos in file (file length >
                                                                                    // 0)
                this.bufstartpos = pos & this.bufmask;
                this.bufusedsize = this.fillbuf();

            } else if (((pos == 0) && (this.fileendpos == 0)) || (pos == this.fileendpos + 1)) { // seek pos is append
                                                                                                 // pos

                this.bufstartpos = pos;
                this.bufusedsize = 0;
            }
            this.bufendpos = this.bufstartpos + this.bufsize - 1;
        }
        this.curpos = pos;
    }

    @Override
    public long length() throws IOException {
        return this.max(this.fileendpos + 1, this.initfilelen);
    }

    @Override
    public void setLength(long newLength) throws IOException {
        if (newLength > 0) {
            this.fileendpos = newLength - 1;
        } else {
            this.fileendpos = 0;
        }
        super.setLength(newLength);
    }

    @Override
    public long getFilePointer() throws IOException {
        return this.curpos;
    }

    private long max(long a, long b) {
        if (a > b) return a;
        return b;
    }

    @Override
    public void close() throws IOException {
        this.flushbuf();
        super.close();
    }

    public static void main(String[] args) throws IOException {
        long readfilelen = 0;
        BufferedRandomAccessFile brafReadFile, brafWriteFile;

        String sourcePath = "D:\\项目读写文件\\高效读写\\BufferedRandomAccessFile.xml";
        String copyByBufferedRandom = "D:\\项目读写文件\\高效读写\\copyByBufferedRandom.xml";
        String copyByDataBufferedIOS = "D:\\项目读写文件\\高效读写\\copyByDataBufferedIOS.xml";
        brafReadFile = new BufferedRandomAccessFile(sourcePath);
        readfilelen = brafReadFile.initfilelen;
        brafWriteFile = new BufferedRandomAccessFile(copyByBufferedRandom, "rw", 10);

        byte buf[] = new byte[DEFAULT_BUFFER_SIZE];
        int readLength;

        long start = System.currentTimeMillis();

        while ((readLength = brafReadFile.read(buf)) != -1) {
            brafWriteFile.write(buf, 0, readLength);
        }

        brafWriteFile.close();
        brafReadFile.close();

        System.out.println("BufferedRandomAccessFile Copy & Write File: " + brafReadFile.filename + "    FileSize: "
                + Integer.toString((int) readfilelen >> 1024) + " (KB)    " + "Spend: " + (double) (System.currentTimeMillis() - start) / 1000
                + "(s)");

        FileInputStream fdin = new FileInputStream(sourcePath);
        BufferedInputStream bis = new BufferedInputStream(fdin, 1024);
        DataInputStream dis = new DataInputStream(bis);

        FileOutputStream fdout = new FileOutputStream(copyByDataBufferedIOS);
        BufferedOutputStream bos = new BufferedOutputStream(fdout, 1024);
        DataOutputStream dos = new DataOutputStream(bos);

        start = System.currentTimeMillis();

        for (int i = 0; i < readfilelen; i++) {
            dos.write(dis.readByte());
        }

        dos.close();
        dis.close();

        System.out.println("DataBufferedios Copy & Write File: " + brafReadFile.filename + "    FileSize: "
                + Integer.toString((int) readfilelen >> 1024) + " (KB)    " + "Spend: " + (double) (System.currentTimeMillis() - start) / 1000
                + "(s)");
    }
}
