package main.solution;

import java.io.File;
import java.util.Objects;

public class SolutionFile {

	public File FileName;
	public String Author;
	public boolean Verified;

	public SolutionFile(final File fileName, final String author, final boolean verified) {
		this.FileName = (fileName == null ? new File("") : new File(fileName.getAbsoluteFile().toString()));
		this.Author = Objects.toString(author, "");
		this.Verified = verified;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof SolutionFile) {
			return this.FileName.compareTo(((SolutionFile) obj).FileName) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.FileName.hashCode();
	}
}
