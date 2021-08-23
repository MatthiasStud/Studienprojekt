package main.json;

import java.io.IOException;

import main.solution.Solution;
import main.solution.SolutionFile;

public class JsonVerifiedFile {

	public String FileName = "";
	public String Sha256Checksum = "";
	public String Author = "";
	public Boolean Verified = false;

	public JsonVerifiedFile() {
	}

	public JsonVerifiedFile(final SolutionFile file) {
		this.FileName = file.FileName.getAbsolutePath();
		try {
			this.Sha256Checksum = Solution.getFileHash(file.FileName);
		} catch (final IOException e) {
			this.Sha256Checksum = "";
		}
		this.Author = file.Author;
		this.Verified = file.Verified;
	}

}
