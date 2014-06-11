package utils.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WrappedBufferedWriter extends BufferedWriter {

	public WrappedBufferedWriter(File f) throws IOException {
		super(new FileWriter(f));
	}
	
	@Override
	public void close() throws IOException {
		super.close();
	}

	@Override
	public void flush() throws IOException {
		super.flush();
	}

	@Override
	public void write(char[] buff, int index, int length) throws IOException {
		super.write(buff,index,length);
	}
}
