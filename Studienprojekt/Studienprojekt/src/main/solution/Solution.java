package main.solution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

import main.json.JsonSolution;
import main.json.JsonVerifiedFile;

public class Solution {

	public String SolutionName = "Default";
	public String SolutionDirectory = "";
	public ArrayList<SolutionFile> Files;

	public Solution() {
		this.Files = new ArrayList<>();
	}

	public Solution(final JsonSolution solution, final String solutionFilePath) {
		this.SolutionName = Objects.toString(solution.SolutionName, "");

		Path dir = Paths.get(Objects.toString(solution.SolutionDirectory, ""));
		if (dir.isAbsolute()) {
			this.SolutionDirectory = dir.normalize().toString();
		} else {
			this.SolutionDirectory = Paths.get(Objects.toString(solutionFilePath, "a"), "../", dir.toString())
					.toAbsolutePath().normalize().toString();
		}

		final ArrayList<JsonVerifiedFile> verifiedFiles = new ArrayList<>();
		if (solution.VerifiedFiles != null) {
			for (final JsonVerifiedFile verifiedFile : solution.VerifiedFiles) {
				if (verifiedFile == null) {
					continue;
				}

				dir = Paths.get(Objects.toString(verifiedFile.FileName, ""));
				if (dir.isAbsolute()) {
					verifiedFile.FileName = dir.normalize().toString();
				} else {
					verifiedFile.FileName = Paths.get(this.SolutionDirectory, dir.toString()).toAbsolutePath()
							.normalize().toString();
				}
				verifiedFile.Sha256Checksum = Objects.toString(verifiedFile.Sha256Checksum, "");
				verifiedFiles.add(verifiedFile);
			}
		}

		final File file = new File(this.SolutionDirectory);
		final File[] allFiles = file.listFiles();
		this.Files = new ArrayList<>();

		if (allFiles == null) {
			return;
		}

		for (final File nextFile : allFiles) {
			String author = "";
			boolean verified = false;
			int index = 0;
			for (final JsonVerifiedFile verifiedFile : verifiedFiles) {
				if (nextFile.getAbsoluteFile().compareTo(new File(verifiedFile.FileName)) == 0) {

					try {
						if (!Solution.getFileHash(nextFile).equalsIgnoreCase(verifiedFile.Sha256Checksum)) {
							continue;
						}
					} catch (final IOException e1) {
						continue;
					}

					author = Objects.toString(verifiedFile.Author, "");
					if (verifiedFile.Verified != null && verifiedFile.Verified) {
						verified = true;
					}
					verifiedFiles.remove(index);
					break;
				}
				++index;
			}

			this.Files.add(new SolutionFile(nextFile, author, verified));
		}
	}

	public static String getFileHash(final File file) throws FileNotFoundException, IOException {
		MessageDigest shaDigest;
		try {
			shaDigest = MessageDigest.getInstance("SHA-256");
		} catch (final NoSuchAlgorithmException e) {
			return null;
		}

		try (FileInputStream stream = new FileInputStream(file)) {

			final byte[] byteArray = new byte[4096];
			int bytesCount = 0;
			while ((bytesCount = stream.read(byteArray)) != -1) {
				shaDigest.update(byteArray, 0, bytesCount);
			}
		}

		final byte[] bytes = shaDigest.digest();
		final StringBuilder sb = new StringBuilder();
		for (final byte element : bytes) {
			sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

}
