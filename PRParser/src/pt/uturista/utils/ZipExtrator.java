package pt.uturista.utils;

import java.io.File;
import java.io.IOException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;

public class ZipExtrator {
	final File fileToExtract;

	private ZipExtrator(File fileToExtract) {
		this.fileToExtract = fileToExtract;
	}

	public static ZipExtrator extract(File fileToExtract) {
		return new ZipExtrator(fileToExtract);
	}

	public boolean to(String destinyPath) {
		File tmp = new File(destinyPath);
		if (tmp.exists())
			try {
				FileUtils.deleteDirectory(tmp);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		try {
			ZipFile zipFile = new ZipFile(fileToExtract);
			zipFile.extractAll(destinyPath);

		} catch (ZipException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}
}
