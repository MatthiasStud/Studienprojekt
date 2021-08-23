package main.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class JsonSolution {

	public String SolutionName = "Default";
	public String SolutionDirectory = "";
	public ArrayList<JsonVerifiedFile> VerifiedFiles = new ArrayList<>();

	public JsonSolution() {
	}

	public static JsonSolution loadFromFile(final File solutionFile) throws IOException {
		try (FileReader reader = new FileReader(solutionFile)) {
			final Gson gson = new Gson();
			return gson.fromJson(reader, JsonSolution.class);
		}
	}

	public static JsonSolution loadFromFile(final String solutionFilePath) throws IOException {
		try (FileReader reader = new FileReader(solutionFilePath)) {
			final Gson gson = new Gson();
			return gson.fromJson(reader, JsonSolution.class);
		}
	}

	public void saveToFile(final String SolutionFilePath) throws IOException {
		this.saveToFile(SolutionFilePath, null);
	}

	public void saveToFile(final String SolutionFilePath, final String Indent) throws IOException {
		try (JsonWriter writer = new JsonWriter(new FileWriter(SolutionFilePath))) {
			if (Indent != null) {
				writer.setIndent("\t");
			}
			final Gson gson = new Gson();
			gson.toJson(this, JsonSolution.class, writer);
		}
	}

}
