package org.zero.studio.toolbox.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * auto generate annotation message via Class properties<br>
 * 
 * example : source code-><br>
 * class MyTest{ //<br>
 * @NotNull<br>
 * 				String testProp1; //<br>
 *              }
 * 
 * @author Administrator
 */
public class AnnotationMessageGenerator
{
	private String msgRegex = ".*@[A-Za-z]{3,16}\\(.*message.*\\)";
	private String bracketsRegex = ".*@[A-Za-z]{3,16}\\(.*\\)";
	private String validOnlyRegex = ".*@[A-Za-z]{3,16}";
	//
	private File[] javaFiles;
	private File propertiesFile;
	private int startErrorCode;

	public AnnotationMessageGenerator(String fileUrl) throws IOException
	{
		File file = new File(fileUrl);
		if (file.exists())
		{
			if (file.isDirectory())
			{
				this.javaFiles = file.listFiles();
				this.propertiesFile = new File(fileUrl + "/message.properties");
				if (!propertiesFile.exists())
				{
					this.propertiesFile.createNewFile();
				}
			}
			else if (file.isFile())
			{
				this.javaFiles = new File[] { file };
				propertiesFile = new File(file.getParentFile().getAbsolutePath() + "/message.properties");
				if (!propertiesFile.exists())
				{
					this.propertiesFile.createNewFile();
				}
			}
			else
			{
				throw new RuntimeException("File is not a real file or a directory");
			}
		}
		else
		{
			throw new RuntimeException("File is not exists");
		}
	}

	/**
	 * modify source.java annotations codes
	 * 
	 * @return
	 */
	public void modifyFileContent()
	{
		System.out.println("------ begins to modify java content ------");
		List<String> propertiesLines = new ArrayList<String>();
		for (File f : javaFiles)
		{
			try
			{
				List<String> javaContentLines = new ArrayList<String>();
				this.readAndUpdateCodeLines(f, javaContentLines, propertiesLines);
				this.writeNewCodeLines(f, javaContentLines);
				this.generateProperties(propertiesLines);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("====== ending of modify java content ======");
	}

	/**
	 * read code lines from [source].java and update annotation codes
	 * 
	 * @param fileUrl
	 * @param javaContentLines
	 * @param propertiesLines
	 * @throws Exception
	 */
	private void readAndUpdateCodeLines(File f, List<String> javaContentLines, List<String> propertiesLines) throws Exception
	{
		String fileName = f.getName();
		System.out.println("	1.1 read file:" + fileName);
		if (fileName.endsWith(".java"))
		{
			List<String> needModifyLines = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(f));
			System.out.println("	1.2 loop read line from " + fileName + ", and update it");
			String line = reader.readLine();
			while (line != null)
			{
				if (line.trim().startsWith("@"))
				{
					needModifyLines.add(line);
				}
				else if (line.trim().endsWith(";"))
				{
					if (needModifyLines.isEmpty())
					{
						javaContentLines.add(line);
					}
					else
					{
						for (String updateLine : needModifyLines)
						{
							String prop = line.trim().substring(line.trim().lastIndexOf(" ") + 1, line.trim().length() - 1);
							String[] msgs = this.updateAnnotationLine(updateLine, f.getName().replaceAll("\\.java", ""), prop);
							javaContentLines.add(msgs[0]);
							propertiesLines.add(msgs[1]);
						}
						javaContentLines.add(line);
						needModifyLines.clear();
					}
				}
				else
				{
					javaContentLines.add(line);
				}
				line = reader.readLine();
			}
			reader.close();
			System.out.println("	1.3 read and update over, file:" + fileName);
		}
	}

	/**
	 * write new code lines to source.java
	 * 
	 * @param fileUrl
	 * @param javaContentLines
	 * @throws Exception
	 */
	private void writeNewCodeLines(File f, List<String> javaContentLines) throws Exception
	{
		System.out.println("	2.1 write file:" + f.getName());
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		for (String newLine : javaContentLines)
		{
			writer.write(newLine + "\r\n");
		}
		writer.close();
		System.out.println("	2.2 write over, file:" + f.getName());
	}

	/**
	 * write properties content to message.properties
	 * 
	 * @param propertiesLines
	 * @throws Exception
	 */
	private void generateProperties(List<String> propertiesLines) throws Exception
	{
		System.out.println("	3.1 read line number form properties :" + propertiesFile.getName());
		BufferedReader propReader = new BufferedReader(new FileReader(propertiesFile));
		int n = 0;
		String pl = propReader.readLine();
		List<String> storedPropKeys = new ArrayList<String>();
		while (pl != null && !"".equals(pl.trim()))
		{
			storedPropKeys.add(pl.split("\\=")[0]);
			n++;
			pl = propReader.readLine();
		}
		propReader.close();
		System.out.println("	3.2 read properties lines number over, and start write file:" + propertiesFile.getName());
		BufferedWriter propWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propertiesFile, true)));
		int i = n + this.startErrorCode;
		for (String propLine : propertiesLines)
		{
			if (!storedPropKeys.contains(propLine))
			{
				i++;
				propWriter.write(propLine + "=" + i + "~" + propLine + "\r\n");
			}
		}
		propWriter.close();
		System.out.println("	3.3 write properties over, file:" + propertiesFile.getName());
	}

	/**
	 * add message content to annotation code
	 * 
	 * @param line : annotation code line
	 * @param className
	 * @param prop
	 * @return {'completed annotation code', 'message content'}
	 */
	private String[] updateAnnotationLine(String line, String className, String prop)
	{
		String value = setAnnotationLine(line, className, prop);
		if (line.matches(validOnlyRegex))
		{
			line += "(message = \"{" + value + "}\")";
		}
		else
		{
			if (!line.matches(msgRegex) && line.matches(bracketsRegex))
			{
				if (line.indexOf("()") > -1)
				{
					line = line.replaceFirst("\\)", "message = \"{" + value + "}\")");
				}
				else
				{
					line = line.substring(0, line.length() - 1) + ", message = \"{" + value + "}\"" + line.substring(line.length() - 1);
				}
			}
		}
		return new String[] { line, value };
	}

	/**
	 * generate message content
	 * 
	 * @param line : annotation code line
	 * @param className
	 * @param prop
	 * @return message content
	 */
	private String setAnnotationLine(String line, String className, String prop)
	{
		String trimLine = line.trim();
		String suffix = trimLine.substring(1, trimLine.indexOf("(") > -1 ? trimLine.indexOf("(") : trimLine.length());
		String value = (className + "." + prop + suffix).replaceAll("[A-Z]", "\\.$0").toLowerCase();
		return value.charAt(0) == '.' ? value.substring(1) : value;
	}

	public int getStartErrorCode()
	{
		return startErrorCode;
	}

	public void setStartErrorCode(int startErrorCode)
	{
		this.startErrorCode = startErrorCode;
	}

	public static void main(String[] args)
	{
		String fileUrl = "D:\\ide\\workspace_eclipse-jee-oxygen-R-win32-x86_64\\carriage-api-game-inviter\\src\\main\\java\\org\\zero\\studio\\carriage\\api\\game\\inviter\\AddPlayerReq.java";
		try
		{
			AnnotationMessageGenerator amg = new AnnotationMessageGenerator(fileUrl);
			amg.setStartErrorCode(1000);
			amg.modifyFileContent();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
